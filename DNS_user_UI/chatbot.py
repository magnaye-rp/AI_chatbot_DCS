import json
import numpy as np
import gensim
import tensorflow as tf
from tensorflow.keras.models import load_model
from tensorflow.keras.preprocessing.sequence import pad_sequences
from sklearn.preprocessing import LabelEncoder
from nltk.tokenize import word_tokenize
import customtkinter as ctk

# Load Pretrained Model
model = load_model("ai/chatbot_model.h5")
word2vec_model = gensim.models.Word2Vec.load("ai/word2vec.model")

# Load Tokenizer
with open("ai/tokenizer.json", "r") as file:
    word_index = json.load(file)

# Load Label Encoder
label_encoder = LabelEncoder()
label_encoder.classes_ = np.load("ai/label_encoder.npy", allow_pickle=True)

# Load Training Data
with open("ai/training_data.json", "r") as file:
    data = json.load(file)

responses = {intent["tag"]: intent["responses"] for intent in data["intents"]}

def preprocess_input(user_input):
    """Tokenize and vectorize user input."""
    words = word_tokenize(user_input.lower())
    word_vectors = [word2vec_model.wv[word] for word in words if word in word2vec_model.wv]

    if not word_vectors:
        return None  # If no words are found in Word2Vec, return None

    sentence_vector = np.mean(word_vectors, axis=0)
    return np.expand_dims(sentence_vector, axis=0)

def predict_intent(user_input):
    """Predict the intent of user input using the trained model."""
    sequence = [[word_index.get(word, 0) for word in word_tokenize(user_input.lower())]]
    padded_sequence = pad_sequences(sequence, maxlen=model.input_shape[1], padding="post")
    prediction = model.predict(padded_sequence)[0]
    tag = label_encoder.inverse_transform([np.argmax(prediction)])[0]
    return np.random.choice(responses[tag])

# Function for GUI Integration
def get_chatbot_response(user_message):
    """Returns the chatbot's response for a given user input."""
    if not user_message.strip():
        return "Please say something 😊"
    return predict_intent(user_message)

class Chatbot(ctk.CTkToplevel):
    def __init__(self):
        super().__init__()
        self.title("Chat with DCS Bot")
        self.geometry("500x600")
        self.configure(fg_color="#222831")

        self.chat_log = ctk.CTkTextbox(self, width=480, height=500, font=("Arial", 14), fg_color="#393E46", text_color="white")
        self.chat_log.pack(pady=10)

        self.entry = ctk.CTkEntry(self, width=380, font=("Arial", 14))
        self.entry.pack(side="left", padx=10, pady=10)
        self.entry.bind("<Return>", self.send_message)

        self.send_button = ctk.CTkButton(self, text="Send", command=self.send_message)
        self.send_button.pack(side="right", padx=10, pady=10)

        self.chat_log.insert("end", "🦷 Chatbot: Hello! How can I assist you with your dental care today?\n\n")

    def send_message(self, event=None):
        user_input = self.entry.get()
        if user_input.strip():
            self.chat_log.insert("end", f"👤 You: {user_input}\n")
            response = get_chatbot_response(user_input)
            self.chat_log.insert("end", f"🦷 Dental-Bot: {response}\n\n")
            self.chat_log.see("end")
            self.entry.delete(0, "end")
