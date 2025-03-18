import json
import random
import numpy as np
import pickle
import tensorflow as tf
from tensorflow.keras.models import load_model

# Load data
words = pickle.load(open("words.pkl", "rb"))
labels = pickle.load(open("labels.pkl", "rb"))
model = load_model("chatbot_model.h5")

# Load intents
with open('training_data.json', 'r') as file:
    intents = json.load(file)

# Process user input
def preprocess_input(text):
    tokens = text.lower().split()
    bag = [1 if w in tokens else 0 for w in words]
    return np.array([bag])

# Get response
def get_response(tag):
    for intent in intents["intents"]:
        if intent["tag"] == tag:
            return random.choice(intent["responses"])
    return "Sorry, I didn't understand that."

# Predict class
def predict_class(text):
    bow = preprocess_input(text)
    res = model.predict(bow)[0]
    confidence_threshold = 0.25
    results = [[i, r] for i, r in enumerate(res) if r > confidence_threshold]
    results.sort(key=lambda x: x[1], reverse=True)
    return labels[results[0][0]] if results else "unknown"

# Chat loop
print("🤖 Chatbot is ready! Type 'exit' to quit.")
while True:
    user_input = input("You: ")
    if user_input.lower() == "exit":
        break
    tag = predict_class(user_input)
    response = get_response(tag)
    print("Dental Bot:", response)
