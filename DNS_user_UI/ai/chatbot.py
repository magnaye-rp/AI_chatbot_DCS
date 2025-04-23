import json
import numpy as np
import tensorflow as tf
from tensorflow.keras.models import load_model
from tensorflow.keras.preprocessing.sequence import pad_sequences
from sklearn.preprocessing import LabelEncoder
from nltk.tokenize import word_tokenize

# Load Pretrained Model
model = load_model("chatbot_model_stacked_lstm.h5")

with open("tokenizer.json", "r") as file:
    word_index = json.load(file)
    tokenizer = tf.keras.preprocessing.text.Tokenizer(num_words=None, oov_token="<OOV>")
    tokenizer.word_index = word_index

# Load Label Encoder
label_encoder = LabelEncoder()
label_encoder.classes_ = np.load("label_encoder.npy", allow_pickle=True)
print("Label Encoder Classes:", label_encoder.classes_) # Check the order of your classes

# Load Training Data
with open("training_data.json", "r") as file:
    data = json.load(file)

responses = {intent["tag"]: intent["responses"] for intent in data["intents"]}
fallback_responses = responses.get("fallback", ["I'm not sure how to respond to that."])

# Confidence Threshold
CONFIDENCE_THRESHOLD = 0.6

# Predict intent with confidence
def predict_intent(user_input):
    """Predict the intent of user input using the trained model with confidence."""
    sequence = tokenizer.texts_to_sequences([user_input.lower()])
    padded_sequence = pad_sequences(sequence, maxlen=model.input_shape[1], padding="post")

    prediction = model.predict(padded_sequence)[0]
    predicted_class_index = np.argmax(prediction)
    confidence = prediction[predicted_class_index]
    predicted_tag = label_encoder.inverse_transform([predicted_class_index])[0]

    return predicted_tag, confidence

# Start Chatbot Interaction
print("🤖 Chatbot is ready! Type 'quit' to exit.")
while True:
    user_input = input("You: ").strip()
    if user_input.lower() == "quit":
        print("Bot: Goodbye! 👋")
        break

    try:
        predicted_tag, confidence = predict_intent(user_input)
        print("Raw Prediction:", model.predict(pad_sequences(tokenizer.texts_to_sequences([user_input.lower()]), maxlen=model.input_shape[1], padding="post"))[0]) # Debug
        print("Confidence:", f"{confidence:.2f}") # Debug
        print("Predicted Intent:", predicted_tag) # Debug
        if confidence >= CONFIDENCE_THRESHOLD and predicted_tag in responses:
            print("Bot:", np.random.choice(responses[predicted_tag]))
        else:
            print("Bot:", np.random.choice(fallback_responses))
            print("(Confidence:", f"{confidence:.2f}", "- Predicted Intent:", predicted_tag + ")")
    except Exception as e:
        print(f"Bot: An error occurred: {e}")