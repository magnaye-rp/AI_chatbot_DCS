import json
import numpy as np
import tensorflow as tf
from tensorflow.keras.models import load_model
from tensorflow.keras.preprocessing.sequence import pad_sequences
from sklearn.preprocessing import LabelEncoder
import requests
from nltk.tokenize import word_tokenize
import re
from datetime import datetime
from datetime import datetime, timedelta

CONFIDENCE_THRESHOLD = 0.6

class Chatbot:
    def __init__(self, user_id):
        self.api_url = "http://localhost:5000/book_appointment"
        self.api_key = "chatbot123"
        self.user_id = user_id
        self.model = load_model("chatbot_model_stacked_lstm.h5")

        with open("tokenizer.json", "r") as file:
            word_index = json.load(file)
            self.tokenizer = tf.keras.preprocessing.text.Tokenizer(num_words=None, oov_token="<OOV>")
            self.tokenizer.word_index = word_index

        # Load Label Encoder
        self.label_encoder = LabelEncoder()
        self.label_encoder.classes_ = np.load("label_encoder.npy", allow_pickle=True)
        print("Label Encoder Classes:", self.label_encoder.classes_)

        # Load Training Data
        with open("training_data.json", "r") as file:
            self.data = json.load(file)

        self.responses = {intent["tag"]: intent["responses"] for intent in self.data["intents"]}
        self.fallback_responses = self.responses.get("fallback", ["I'm not sure how to respond to that."])

    def convert_to_12hr_format(self, time_str):
        try:
            # Handle time like "10 am" (without minutes)
            if 'am' in time_str or 'pm' in time_str:
                time_str = time_str.replace('am', '').replace('pm', '').strip()
                if len(time_str.split(":")) == 1:
                    time_str += ":00"  # Add ':00' if minutes are missing
                time_obj = datetime.strptime(time_str, "%I:%M")  # Convert to 24-hour format
                return time_obj.strftime("%I:%M %p")  # Convert to 12-hour format with AM/PM
            else:
                time_obj = datetime.strptime(time_str, "%H:%M")  # Convert to 24-hour format
                return time_obj.strftime("%I:%M %p")  # Convert to 12-hour format with AM/PM
        except ValueError:
            print(f"Error converting time: {time_str}")
            return time_str

    def extract_appointment_details_easy(self, user_input):
        details = {}
        text = user_input.lower()

        # Extract Date (excluding "today")
        date_patterns = [
            (r"tomorrow", "tomorrow"),
            (r"today", "today"),
            (r"on (\w+\s?\d{1,2})", "%B %d"),
            (r"on (\w+)", "%A"),
            (r"(\d{1,2}/\d{1,2})", "%m/%d")
        ]

        for pattern, fmt in date_patterns:
            match = re.search(pattern, text)
            if match:
                date_str = match.group(1) if fmt != "tomorrow" else "tomorrow"
                details["date"] = date_str
                break

        time_patterns = [
            (r"at (\d{1,2}):(\d{2})\s?(am|pm)", "%I:%M %p"),  # "2:30 pm"
            (r"at (\d{1,2})\s?(am|pm)", "%I %p")  # "2 pm"
        ]

        for pattern, fmt in time_patterns:
            match = re.search(pattern, text)
            if match:
                time_str = f"{match.group(1)}:{match.group(2)} {match.group(3)}" if ":" in fmt else f"{match.group(1)} {match.group(2)}"
                details["time"] = self.convert_to_12hr_format(time_str)
                break

        self.services = {
            "clean(?:ing)?": "Dental Cleaning",
            "extract(?:ion)?": "Tooth Extraction",
            "fill(?:ing)?": "Tooth Filling",
            "root canal": "Root Canal Treatment",
            "braces": "Braces Adjustment",
            "whiten(?:ing)?": "Teeth Whitening",
            "x[\s-]?ray": "Dental X-Ray",
            "consult": "Consultation"
        }

        for pattern, service in self.services.items():
            if re.search(pattern, text):
                details["service"] = service
                break

        return details

    def predict_intent(self, user_input):
        print(f"Processing input: '{user_input}'")  # Debug input
        sequence = self.tokenizer.texts_to_sequences([user_input.lower()])
        padded_sequence = pad_sequences(sequence, maxlen=self.model.input_shape[1], padding="post")

        prediction = self.model.predict(padded_sequence, verbose=0)[0]
        predicted_class_index = np.argmax(prediction)
        confidence = prediction[predicted_class_index]
        predicted_tag = self.label_encoder.inverse_transform([predicted_class_index])[0]

        # print(f"Raw prediction scores: {prediction}") #debugger
        return predicted_tag, confidence

    def run(self):
        while True:
            user_input = input("You: ").strip()
            if user_input.lower() == "quit":
                print("Bot: Goodbye! 👋")
                break
            try:
                predicted_tag, confidence = self.predict_intent(user_input)
                if predicted_tag == "appointment":
                    print("Raw Prediction:", self.model.predict(
                        pad_sequences(self.tokenizer.texts_to_sequences([user_input.lower()]),
                                      maxlen=self.model.input_shape[1], padding="post"))[0])
                    print("Confidence:", f"{confidence:.2f}") #debugger
                else:
                    print("Raw Prediction:", self.model.predict(pad_sequences(self.tokenizer.texts_to_sequences([user_input.lower()]), maxlen=self.model.input_shape[1], padding="post"))[0])  # Debug
                    print("Confidence:", f"{confidence:.2f}")
                    print("Predicted Intent:", predicted_tag)
                    if confidence >= CONFIDENCE_THRESHOLD and predicted_tag in self.responses:
                        print("Bot:", np.random.choice(self.responses[predicted_tag]))
                    else:
                        print("Bot:", np.random.choice(self.fallback_responses))
                        print("(Confidence:", f"{confidence:.2f}", "- Predicted Intent:", predicted_tag + ")")
            except Exception as e:
                print(f"Bot: An error occurred: {e}")

            if predicted_tag == "appointment":
                appointment_details = self.extract_appointment_details_easy(user_input)

                required = ['date', 'time', 'service']
                if all(k in appointment_details for k in required):
                    api_url = "http://localhost:5000/book_appointment"
                    payload = {
                        "patient_id": self.user_id,
                        "intent": "book_appointment",
                        "date": appointment_details["date"],
                        "time": appointment_details["time"],
                        "service": appointment_details["service"]
                    }

                    headers = {
                        'X-API-Key': self.api_key,
                        'Content-Type': 'application/json'
                    }

                    try:
                        print("Final Payload Sent to Server:")
                        print(json.dumps(payload, indent=4))
                        response = requests.post(api_url, json=payload, headers=headers)
                        print("Response Content:", response.text)  # Check the full response

                        response.raise_for_status()
                        api_result = response.json()
                        print("Bot:", api_result.get("message", "Appointment booked successfully!"))
                    except requests.exceptions.RequestException as e:
                        print("Bot: Error communicating with booking service:", e)
                        if e.response is not None:
                            print("Bot: Response content:", e.response.text)
                else:
                    missing = [k for k in required if k not in appointment_details]
                    response_parts = ["Bot: "]

                    if 'date' in missing:
                        response_parts.append("What date would you like to come in?")
                    if 'time' in missing:
                        response_parts.append("What time works best for you?")
                    if 'service' in missing:
                        services = {"consultation": "Consultation", "tooth extraction": "Tooth Extraction",
                                    "dental cleaning": "Dental Cleaning", "tooth filling": "Tooth Filling",
                                    "root canal treatment": "Root Canal Treatment",
                                    "braces adjustment": "Braces Adjustment", "teeth whitening": "Teeth Whitening",
                                    "dental x-ray": "Dental X-Ray"}
                        response_parts.append("Which service would you like? We offer: " + ", \n".join(services.keys()))

                    print(" ".join(response_parts))

if __name__ == "__main__":
    user_id = 9
    chatbot = Chatbot(user_id)
    chatbot.run()
