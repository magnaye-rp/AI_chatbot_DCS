import json
import numpy as np
import gensim
import tensorflow as tf
from tensorflow.keras.models import load_model
from tensorflow.keras.preprocessing.sequence import pad_sequences
from sklearn.preprocessing import LabelEncoder
from nltk.tokenize import word_tokenize

# Load Pretrained Model
model = load_model("chatbot_model.h5")
word2vec_model = gensim.models.Word2Vec.load("word2vec.model")

# Load Tokenizer
with open("tokenizer.json", "r") as file:
    word_index = json.load(file)

# Load Label Encoder
label_encoder = LabelEncoder()
label_encoder.classes_ = np.load("label_encoder.npy", allow_pickle=True)

# Load Training Data
with open("training_data.json", "r") as file:
    data = json.load(file)

responses = {intent["tag"]: intent["responses"] for intent in data["intents"]}

# Preprocess user input
def preprocess_input(user_input):
    """Tokenize and vectorize user input."""
    words = word_tokenize(user_input.lower())
    word_vectors = [word2vec_model.wv[word] for word in words if word in word2vec_model.wv]

    if not word_vectors:
        return None  # If no words are found in Word2Vec, return None

    sentence_vector = np.mean(word_vectors, axis=0)
    return np.expand_dims(sentence_vector, axis=0)

# Predict intent
def predict_intent(user_input):
    """Predict the intent of user input using the trained model."""
    sequence = [[word_index.get(word, 0) for word in word_tokenize(user_input.lower())]]  # Convert words to indexes
    padded_sequence = pad_sequences(sequence, maxlen=model.input_shape[1], padding="post")  # Match input size

    prediction = model.predict(padded_sequence)[0]
    tag = label_encoder.inverse_transform([np.argmax(prediction)])[0]

    return np.random.choice(responses[tag])

# Start Chatbot Interaction
print("🤖 Chatbot is ready! Type 'quit' to exit.")
while True:
    user_input = input("You: ").strip()
    if user_input.lower() == "quit":
        print("Bot: Goodbye! 👋")
        break

    response = predict_intent(user_input)
    print("Bot:", response)
