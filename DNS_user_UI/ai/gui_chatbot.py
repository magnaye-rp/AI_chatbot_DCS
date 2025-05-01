import json
import numpy as np
import tensorflow as tf
from tensorflow.keras.models import load_model
from tensorflow.keras.preprocessing.sequence import pad_sequences
from sklearn.preprocessing import LabelEncoder
import requests
import re
from datetime import datetime, timedelta

CONFIDENCE_THRESHOLD = 0.6

class Chatbot:
    def __init__(self, user_id, gui_callback):
        self.api_url = "http://localhost:5000/book_appointment"
        self.api_key = "chatbot123"
        self.user_id = user_id
        self.model = load_model("/Users/magnaye.rp/Documents/GitHub/AI_chatbot_DCS/DNS_user_UI/ai/chatbot_model_stacked_lstm.h5")
        self.gui_callback = gui_callback

        with open("/Users/magnaye.rp/Documents/GitHub/AI_chatbot_DCS/DNS_user_UI/ai/tokenizer.json", "r") as file:
            word_index = json.load(file)
            self.tokenizer = tf.keras.preprocessing.text.Tokenizer(num_words=None, oov_token="<OOV>")
            self.tokenizer.word_index = word_index

        self.label_encoder = LabelEncoder()
        self.label_encoder.classes_ = np.load("/Users/magnaye.rp/Documents/GitHub/AI_chatbot_DCS/DNS_user_UI/ai/label_encoder.npy", allow_pickle=True)

        with open("/Users/magnaye.rp/Documents/GitHub/AI_chatbot_DCS/DNS_user_UI/ai/training_data.json", "r") as file:
            self.data = json.load(file)

        self.responses = {intent["tag"]: intent["responses"] for intent in self.data["intents"]}
        self.fallback_responses = self.responses.get("fallback", ["I'm not sure how to respond to that."])

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

    def convert_to_12hr_format(self, time_str):
        try:
            time_str = time_str.strip().lower().replace(" ", "")
            if 'am' in time_str or 'pm' in time_str:
                fmt = "%I%p" if ':' not in time_str else "%I:%M%p"
                time_obj = datetime.strptime(time_str, fmt)
            else:
                time_obj = datetime.strptime(time_str, "%H:%M")

            return time_obj.strftime("%I:%M %p")
        except ValueError:
            print(f"Error converting time: {time_str}")
            return time_str

    def parse_date(self, date_str, current_date=None):
        """
        Parse various date formats and return a standardized date
        """
        if not current_date:
            current_date = datetime.now()

        try:
            if date_str.lower() == "today":
                return current_date.strftime("%Y-%m-%d")
            elif date_str.lower() == "tomorrow":
                next_day = current_date + timedelta(days=1)
                return next_day.strftime("%Y-%m-%d")

            # Handle day names (Monday, Tuesday, etc.)
            days = ["monday", "tuesday", "wednesday", "thursday", "friday", "saturday", "sunday"]
            if date_str.lower() in days:
                day_idx = days.index(date_str.lower())
                current_day_idx = current_date.weekday()
                days_ahead = (day_idx - current_day_idx) % 7
                if days_ahead == 0:  # Same day of week, so use next week
                    days_ahead = 7
                target_date = current_date + timedelta(days=days_ahead)
                return target_date.strftime("%Y-%m-%d")

            # Handle dates like "April 15" or "Apr 15"
            try:
                # Try to parse as Month Day
                parsed_date = datetime.strptime(date_str, "%B %d")
                year = current_date.year
                # If the date has passed this year, use next year
                if (parsed_date.month < current_date.month or
                        (parsed_date.month == current_date.month and parsed_date.day < current_date.day)):
                    year += 1
                return datetime(year, parsed_date.month, parsed_date.day).strftime("%Y-%m-%d")
            except ValueError:
                try:
                    # Try abbreviated month
                    parsed_date = datetime.strptime(date_str, "%b %d")
                    year = current_date.year
                    if (parsed_date.month < current_date.month or
                            (parsed_date.month == current_date.month and parsed_date.day < current_date.day)):
                        year += 1
                    return datetime(year, parsed_date.month, parsed_date.day).strftime("%Y-%m-%d")
                except ValueError:
                    # Handle dates like "4/15" (MM/DD)
                    try:
                        month, day = map(int, date_str.split("/"))
                        year = current_date.year
                        if (month < current_date.month or
                                (month == current_date.month and day < current_date.day)):
                            year += 1
                        return datetime(year, month, day).strftime("%Y-%m-%d")
                    except (ValueError, IndexError):
                        return date_str
        except Exception:
            return date_str

    def extract_appointment_details(self, user_input):
        details = {}
        text = user_input.lower()

        # Date extraction
        date_patterns = [
            (r"\b(tomorrow)\b", None),
            (r"\b(today)\b", None),
            (r"\bon\s+(monday|tuesday|wednesday|thursday|friday|saturday|sunday)\b", None),
            (r"\bon\s+(\w+\s+\d{1,2})\b", None),  # "on April 15"
            (r"\bon\s+(\d{1,2}/\d{1,2})\b", None),  # "on 4/15"
            (r"\b(\d{1,2}/\d{1,2})\b", None),  # "4/15" without "on"
            (r"\bnext\s+(monday|tuesday|wednesday|thursday|friday|saturday|sunday)\b", "next"),  # "next Monday"
            (r"\bthis\s+(monday|tuesday|wednesday|thursday|friday|saturday|sunday)\b", "this")  # "this Monday"
        ]

        for pattern, modifier in date_patterns:
            match = re.search(pattern, text)
            if match:
                date_str = match.group(1)

                # Handle "next" and "this" modifiers
                if modifier == "next" or modifier == "this":
                    days = ["monday", "tuesday", "wednesday", "thursday", "friday", "saturday", "sunday"]
                    day_name = date_str.lower()
                    if day_name in days:
                        current_date = datetime.now()
                        day_idx = days.index(day_name)
                        current_day_idx = current_date.weekday()

                        if modifier == "next":
                            # "Next Monday" means the Monday that follows the upcoming Monday
                            days_ahead = (day_idx - current_day_idx) % 7
                            if days_ahead == 0:  # Today is the day mentioned
                                days_ahead = 7
                            target_date = current_date + timedelta(days=days_ahead)
                            if modifier == "next":
                                target_date += timedelta(days=7)
                        else:  # "this"
                            # "This Monday" typically means the upcoming Monday, unless today is Monday
                            days_ahead = (day_idx - current_day_idx) % 7
                            if days_ahead == 0:  # Today is the day mentioned
                                days_ahead = 0  # Keep it today for "this Monday" when today is Monday
                            target_date = current_date + timedelta(days=days_ahead)

                        details["date"] = target_date.strftime("%Y-%m-%d")
                else:
                    details["date"] = self.parse_date(date_str)
                break

        # Time extraction with more patterns
        time_patterns = [
            (r"\bat\s+(\d{1,2}:\d{2})\s*(am|pm|a\.m\.|p\.m\.)", None),  # "at 10:30 am"
            (r"\bat\s+(\d{1,2})\s*(am|pm|a\.m\.|p\.m\.)", None),  # "at 10 am"
            (r"\b(\d{1,2}:\d{2})\s*(am|pm|a\.m\.|p\.m\.)", None),  # "10:30 am" without "at"
            (r"\b(\d{1,2})\s*(am|pm|a\.m\.|p\.m\.)", None),  # "10 am" without "at"
            (r"\bat\s+(\d{1,2}:\d{2})\b", "24h"),  # "at 14:30" (24-hour format)
            (r"\bat\s+(\d{1,2})\b", "24h"),  # "at 14" (24-hour format)
            (r"\b(\d{1,2}:\d{2})\b", "24h_check"),  # "14:30" without "at" (check if 24h)
            (r"\bin the (morning|afternoon|evening)", "period")  # General time of day
        ]

        for pattern, time_type in time_patterns:
            match = re.search(pattern, text)
            if match:
                if time_type == "period":
                    period = match.group(1)
                    # Assign a reasonable time based on period
                    period_times = {
                        "morning": "9:00 AM",
                        "afternoon": "2:00 PM",
                        "evening": "6:00 PM"
                    }
                    details["time"] = period_times.get(period)
                    details["time_period"] = period  # Store the period separately
                elif time_type == "24h":
                    time_str = match.group(1)
                    details["time"] = self.convert_to_12hr_format(time_str)
                elif time_type == "24h_check":
                    time_str = match.group(1)
                    # Check if this is likely a 24-hour time or just numbers in text
                    parts = time_str.split(":")
                    if len(parts) == 2:
                        hour, minute = map(int, parts)
                        if 0 <= hour <= 23 and 0 <= minute <= 59:
                            details["time"] = self.convert_to_12hr_format(time_str)
                else:
                    time_str = f"{match.group(1)} {match.group(2)}"
                    details["time"] = self.convert_to_12hr_format(time_str)
                break

        # Extract duration if mentioned
        duration_patterns = [
            (r"(\d+)\s*(?:hour|hr)s?\s*(?:and\s*)?(\d+)\s*(?:minute|min)s?", "both"),
            (r"(\d+)\s*(?:hour|hr)s?", "hour"),
            (r"(\d+)\s*(?:minute|min)s?", "minute"),
            (r"half\s*(?:an)?\s*hour", "half_hour"),
            (r"quarter\s*(?:of an)?\s*hour", "quarter_hour")
        ]

        for pattern, dur_type in duration_patterns:
            match = re.search(pattern, text)
            if match:
                if dur_type == "both":
                    hours = int(match.group(1))
                    minutes = int(match.group(2))
                    details["duration"] = hours * 60 + minutes
                elif dur_type == "hour":
                    details["duration"] = int(match.group(1)) * 60
                elif dur_type == "minute":
                    details["duration"] = int(match.group(1))
                elif dur_type == "half_hour":
                    details["duration"] = 30
                elif dur_type == "quarter_hour":
                    details["duration"] = 15
                break

        # Service extraction
        for pattern, service in self.services.items():
            if re.search(r"\b" + pattern + r"\b", text, re.IGNORECASE):
                details["service"] = service
                break

        # Extract name if provided
        name_patterns = [
            r"(?:my name is|this is|for)\s+([A-Z][a-z]+(?:\s+[A-Z][a-z]+)*)",
            r"([A-Z][a-z]+(?:\s+[A-Z][a-z]+)*)'s appointment"
        ]

        for pattern in name_patterns:
            match = re.search(pattern, user_input)  # Use original case
            if match:
                details["name"] = match.group(1).strip()
                break

        # Extract phone number
        phone_patterns = [
            r"(\d{3}[-.\s]?\d{3}[-.\s]?\d{4})",  # 123-456-7890, 123.456.7890, 123 456 7890
            r"(\(\d{3}\)\s?\d{3}[-.\s]?\d{4})"  # (123) 456-7890
        ]

        for pattern in phone_patterns:
            match = re.search(pattern, text)
            if match:
                details["phone"] = match.group(1)
                break

        # Extract any special notes or requests
        notes_patterns = [
            r"(?:note|please note|please|special request)[:\s]+(.+?)(?:$|\.|\band\b)",
            r"(?:i need|i want|i would like|asking for)[:\s]+(.+?)(?:$|\.|\band\b)"
        ]

        for pattern in notes_patterns:
            match = re.search(pattern, text, re.IGNORECASE)
            if match:
                details["notes"] = match.group(1).strip()
                break

        return details

    def extract_appointment_details_easy(self, user_input):
        details = {}
        text = user_input.lower()

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
            (r"at (\d{1,2}):(\d{2})\s?(am|pm)", "%I:%M %p"),
            (r"at (\d{1,2})\s?(am|pm)", "%I %p")
        ]

        for pattern, fmt in time_patterns:
            match = re.search(pattern, text)
            if match:
                time_str = f"{match.group(1)}:{match.group(2)} {match.group(3)}" if ":" in fmt else f"{match.group(1)} {match.group(2)}"
                details["time"] = self.convert_to_12hr_format(time_str)
                break

        # Extract Service
        for pattern, service in self.services.items():
            if re.search(pattern, text):
                details["service"] = service
                break

        return details

    def predict_intent(self, user_input):
        sequence = self.tokenizer.texts_to_sequences([user_input.lower()])
        padded_sequence = pad_sequences(sequence, maxlen=self.model.input_shape[1], padding="post")
        prediction = self.model.predict(padded_sequence, verbose=0)[0]
        predicted_class_index = np.argmax(prediction)
        confidence = prediction[predicted_class_index]
        predicted_tag = self.label_encoder.inverse_transform([predicted_class_index])[0]
        return predicted_tag, confidence

    def run_chat(self, user_input):
        predicted_tag, confidence = self.predict_intent(user_input)

        if confidence >= CONFIDENCE_THRESHOLD and predicted_tag in self.responses:
            bot_response = np.random.choice(self.responses[predicted_tag])
        else:
            bot_response = np.random.choice(self.fallback_responses)

        if predicted_tag == "appointment":
            appointment_details = self.extract_appointment_details_easy(user_input)
            required = ['date', 'time', 'service']

            if all(k in appointment_details for k in required):
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
                    response = requests.post(self.api_url, json=payload, headers=headers)
                    response.raise_for_status()
                    api_result = response.json()
                    bot_response = api_result.get("message", "Appointment booked successfully!")
                except requests.exceptions.RequestException as e:
                    bot_response = f"Error communicating with booking service: {e}"

        self.gui_callback(bot_response)

    def get_response(self, tag):
        if tag in self.responses:
            return np.random.choice(self.responses[tag])
        return np.random.choice(self.fallback_responses)

    def process_appointment(self, user_input):
        appointment_details = self.extract_appointment_details_easy(user_input)
        required = ['date', 'time', 'service']

        if all(k in appointment_details for k in required):
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
                response = requests.post(self.api_url, json=payload, headers=headers)
                response.raise_for_status()
                api_result = response.json()
                return api_result.get("message", "Appointment booked successfully!")
            except requests.exceptions.RequestException as e:
                return f"Error communicating with booking service: {e}"
        return "I need more details to book an appointment. Please provide date, time, and service."

import customtkinter as ctk

class ChatGUIApp:
    def __init__(self, root, user_id):
        self.root = root
        self.root.title("Dental Chatbot")
        self.user_id = user_id

        # Initialize the chatbot
        self.chatbot = Chatbot(self.user_id, self.display_bot_response)

        # Set up the chat window
        self.chat_display = ctk.CTkTextbox(self.root, state="disabled", height=400)
        self.chat_display.pack(padx=10, pady=10, fill="both", expand=True)

        # User input field
        self.input_entry = ctk.CTkEntry(self.root, placeholder_text="Type your message...")
        self.input_entry.pack(padx=10, pady=(0, 10), fill="x")
        self.input_entry.bind("<Return>", self.send_message)

        # Send button
        self.send_button = ctk.CTkButton(self.root, text="Send", command=self.send_message)
        self.send_button.pack(padx=10, pady=(0, 10), anchor="e")

    def send_message(self, event=None):
        user_input = self.input_entry.get().strip()
        if user_input:
            self.add_user_message(f"You: {user_input}")
            self.input_entry.delete(0, "end")
            self.chatbot.run_chat(user_input)

    def add_user_message(self, message):
        self.chat_display.configure(state="normal")
        self.chat_display.insert("end", message + "\n", "user")
        self.chat_display.configure(state="disabled")
        self.chat_display.see("end")

    def display_bot_response(self, bot_response):
        self.chat_display.configure(state="normal")
        self.chat_display.insert("end", f"Bot: {bot_response}\n", "bot")
        self.chat_display.configure(state="disabled")
        self.chat_display.see("end")

if __name__ == "__main__":
    root = ctk.CTk()
    user_id = 9
    app = ChatGUIApp(root, user_id)
    root.mainloop()
