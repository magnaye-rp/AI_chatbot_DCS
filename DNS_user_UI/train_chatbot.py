import json
import numpy as np
import gensim
import tensorflow as tf
from tensorflow import keras
from tensorflow.keras.layers import Embedding, Bidirectional, LSTM, Dense, Attention, Input
from tensorflow.keras.preprocessing.text import Tokenizer
from tensorflow.keras.preprocessing.sequence import pad_sequences
from sklearn.preprocessing import LabelEncoder
import pickle

# Load intents JSON
with open("training_data.json") as file:
    data = json.load(file)

# Extract training data
patterns = []
labels = []
responses = {}  # Store responses for each tag

for intent in data["intents"]:
    for pattern in intent["patterns"]:
        patterns.append(pattern.lower())  # Normalize text
        labels.append(intent["tag"])
    responses[intent["tag"]] = intent["responses"]

# Encode labels numerically
label_encoder = LabelEncoder()
labels_encoded = label_encoder.fit_transform(labels)

# Save Label Encoder for chatbot.py
np.save("label_encoder.npy", label_encoder.classes_)

# Tokenize patterns
tokenizer = Tokenizer(num_words=5000, oov_token="<OOV>")
tokenizer.fit_on_texts(patterns)
sequences = tokenizer.texts_to_sequences(patterns)
padded_sequences = pad_sequences(sequences, padding="post")

# Save Tokenizer for chatbot.py
with open("tokenizer.json", "w") as file:
    json.dump(tokenizer.word_index, file)

# Load Word2Vec Model
word2vec_model = gensim.models.Word2Vec.load("word2vec.model")

# Create embedding matrix
vocab_size = len(tokenizer.word_index) + 1
embedding_dim = word2vec_model.vector_size
embedding_matrix = np.zeros((vocab_size, embedding_dim))

for word, i in tokenizer.word_index.items():
    if word in word2vec_model.wv:
        embedding_matrix[i] = word2vec_model.wv[word]

# Build the Model with BiLSTM + Attention
input_layer = Input(shape=(padded_sequences.shape[1],))

embedding_layer = Embedding(
    vocab_size,
    embedding_dim,
    weights=[embedding_matrix],
    input_length=padded_sequences.shape[1],
    trainable=False,
)(input_layer)

bilstm = Bidirectional(LSTM(128, return_sequences=True))(embedding_layer)

query = Dense(128)(bilstm)
value = Dense(128)(bilstm)
attention = Attention()([query, value])  # Corrected Attention usage

lstm_output = LSTM(64)(attention)
output_layer = Dense(len(set(labels)), activation="softmax")(lstm_output)

model = keras.Model(inputs=input_layer, outputs=output_layer)

# Compile Model
model.compile(loss="sparse_categorical_crossentropy", optimizer="adam", metrics=["accuracy"])

# Train the model
model.fit(padded_sequences, np.array(labels_encoded), epochs=500, batch_size=8, verbose=1)

# Save the model
model.save("chatbot_model.h5")
