import json
import random
import numpy as np
import tensorflow as tf
from tensorflow.keras.models import Sequential
from tensorflow.keras.layers import Dense, Dropout
from tensorflow.keras.optimizers import Adam
import pickle

# Load intents
with open('training_data.json', 'r') as file:
    intents = json.load(file)

# Preprocess data
words = []
labels = []
docs = []

for intent in intents["intents"]:
    for pattern in intent["patterns"]:
        word_list = pattern.lower().split()  # Simple tokenization (no NLTK)
        words.extend(word_list)
        docs.append((word_list, intent["tag"]))
        if intent["tag"] not in labels:
            labels.append(intent["tag"])

words = sorted(set(words))
labels = sorted(set(labels))

# Save words and labels
pickle.dump(words, open("words.pkl", "wb"))
pickle.dump(labels, open("labels.pkl", "wb"))

# Convert text to numerical data
training = []
output_empty = [0] * len(labels)

for doc in docs:
    bag = [1 if w in doc[0] else 0 for w in words]
    output_row = list(output_empty)
    output_row[labels.index(doc[1])] = 1
    training.append([bag, output_row])

random.shuffle(training)
train_x = np.array([x[0] for x in training])
train_y = np.array([x[1] for x in training])

# Build model
model = Sequential([
    Dense(128, input_shape=(len(train_x[0]),), activation='relu'),
    Dropout(0.5),
    Dense(64, activation='relu'),
    Dropout(0.5),
    Dense(len(labels), activation='softmax')
])

model.compile(loss='categorical_crossentropy', optimizer=Adam(learning_rate=0.01), metrics=['accuracy'])

# Train model
model.fit(train_x, train_y, epochs=200, batch_size=5, verbose=1)
model.save("chatbot_model.h5")

print("✅ Chatbot model trained and saved!")
