import json
import numpy as np
import gensim
from tensorflow.keras.models import load_model
from sklearn.preprocessing import LabelEncoder
from spellchecker import SpellChecker
from nltk.tokenize import word_tokenize

model = load_model("chatbot_model.keras")
word2vec_model = gensim.models.Word2Vec.load("word2vec.model")
spell = SpellChecker()

# Load training_data
with open("training_data.json", "r") as file:
    data = json.load(file)

classes = [intent["tag"] for intent in data["intents"]]

def predict_intent(sentence):
    words = sentence.split()
    word_vectors = [word2vec_model.wv[word] for word in words if word in word2vec_model.wv]

    if not word_vectors:
        return "I'm not sure how to respond to that. Can you rephrase?"

    sentence_vector = np.mean(word_vectors, axis=0)
    sentence_vector = np.expand_dims(sentence_vector, axis=0)
    prediction = model.predict(sentence_vector)[0]
    tag = classes[np.argmax(prediction)]

    for intent in data["intents"]:
        if intent["tag"] == tag:
            return np.random.choice(intent["responses"])

    return "Sorry, I didn't understand that."

def preprocess_user_input(user_input):
    words = word_tokenize(user_input.lower())
    corrected_words = [spell.correction(word) if word in spell.unknown(words) else word for word in words]
    return " ".join(corrected_words)


print("🤖 Chatbot is ready! Type 'quit' to exit.")
while True:
    user_input = input("You: ").strip().lower()
    if user_input.lower() == "quit":
        break
    corrected_input = preprocess_user_input(user_input)
    response = predict_intent(user_input)
    print("Bot:", response)
