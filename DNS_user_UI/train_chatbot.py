import json
import numpy as np
import gensim
from gensim.models import Word2Vec
from tensorflow.keras.models import Sequential
from tensorflow.keras.layers import Dense, Dropout
from tensorflow.keras.optimizers import Adam
from sklearn.preprocessing import LabelEncoder
from nltk.tokenize import word_tokenize

import nltk
nltk.download('punkt')

# Load training data
with open("training_data.json", "r") as file:
    data = json.load(file)

# Prepare training data
sentences = []
labels = []
classes = set()

for intent in data["intents"]:
    for pattern in intent["patterns"]:
        tokenized_sentence = word_tokenize(pattern.lower())  # Lowercasing & tokenization
        sentences.append(tokenized_sentence)
        labels.append(intent["tag"])
        classes.add(intent["tag"])

classes = sorted(classes)  # Keep label order consistent

# Train Word2Vec model with more efficient parameters
word2vec_model = Word2Vec(sentences, vector_size=300, window=5, min_count=1, workers=4, epochs=20)
word2vec_model.save("word2vec.model")

# Convert sentences to vectors, handling missing words
X_train = np.array([
    np.mean([word2vec_model.wv[word] for word in sentence if word in word2vec_model.wv], axis=0)
    if any(word in word2vec_model.wv for word in sentence) else np.zeros(300)  # Handle empty cases
    for sentence in sentences
])

# Encode labels
label_encoder = LabelEncoder()
y_train = label_encoder.fit_transform(labels)

# Build a better neural network model
model = Sequential([
    Dense(128, input_shape=(300,), activation="relu"),
    Dropout(0.3),
    Dense(64, activation="relu"),
    Dropout(0.3),
    Dense(len(classes), activation="softmax")
])

model.compile(loss="sparse_categorical_crossentropy", optimizer=Adam(learning_rate=0.001), metrics=["accuracy"])
model.fit(X_train, y_train, epochs=150, batch_size=8)  # Increased epochs for better accuracy

# Save models
model.save("chatbot_model.keras")
np.save("label_classes.npy", classes)
print("✅ Model trained and saved successfully!")
