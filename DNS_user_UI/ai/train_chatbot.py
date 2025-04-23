import json
import numpy as np
import gensim
import tensorflow as tf
from sklearn.utils import class_weight
from sklearn.preprocessing import LabelEncoder
from tensorflow import keras
from tensorflow.keras.layers import Embedding, Bidirectional, LSTM, Dense, Attention, Input, Dropout
from tensorflow.keras.preprocessing.text import Tokenizer
from tensorflow.keras.preprocessing.sequence import pad_sequences
from tensorflow.keras.callbacks import EarlyStopping, ModelCheckpoint, TensorBoard
import datetime
import os
import pickle
from sklearn.model_selection import train_test_split

# Load intents JSON
with open("training_data.json") as file:
    data = json.load(file)

# Extract patterns and labels
patterns = []
labels = []
responses = {}

for intent in data["intents"]:
    for pattern in intent["patterns"]:
        patterns.append(pattern.lower())
        labels.append(intent["tag"])
    responses[intent["tag"]] = intent["responses"]

# Encode labels numerically
label_encoder = LabelEncoder()
labels_encoded = label_encoder.fit_transform(labels)
np.save("label_encoder.npy", label_encoder.classes_)

# Tokenize patterns
tokenizer = Tokenizer(num_words=5000, oov_token="<OOV>")
tokenizer.fit_on_texts(patterns)
sequences = tokenizer.texts_to_sequences(patterns)
padded_sequences = pad_sequences(sequences, padding="post")
with open("tokenizer.json", "w") as file:
    json.dump(tokenizer.word_index, file)

# Load Word2Vec model
word2vec_model = gensim.models.Word2Vec.load("word2vec.model")
vocab_size = len(tokenizer.word_index) + 1
embedding_dim = word2vec_model.vector_size
embedding_matrix = np.zeros((vocab_size, embedding_dim))
for word, i in tokenizer.word_index.items():
    if word in word2vec_model.wv:
        embedding_matrix[i] = word2vec_model.wv[word]

# Split data
X_train, X_val, y_train, y_val = train_test_split(
    padded_sequences, np.array(labels_encoded), test_size=0.2, random_state=42, stratify=np.array(labels)
)

# Build the Model with Two BiLSTM Layers + Dropout
input_layer = Input(shape=(padded_sequences.shape[1],))
embedding_layer = Embedding(
    vocab_size,
    embedding_dim,
    weights=[embedding_matrix],
    trainable=True
)(input_layer)
dropout_embedding = Dropout(0.4)(embedding_layer)

# First BiLSTM Layer (return_sequences=True to feed to the next layer)
lstm_layer_1 = Bidirectional(LSTM(256, return_sequences=True))(dropout_embedding)
dropout_lstm_1 = Dropout(0.3)(lstm_layer_1)

# Second BiLSTM Layer
lstm_layer_2 = Bidirectional(LSTM(256))(dropout_lstm_1)
dropout_lstm_2 = Dropout(0.3)(lstm_layer_2)

output_layer = Dense(len(label_encoder.classes_), activation="softmax")(dropout_lstm_2)

model = keras.Model(inputs=input_layer, outputs=output_layer)
model.compile(
    optimizer=tf.keras.optimizers.Adam(learning_rate=0.001),
    loss="sparse_categorical_crossentropy",
    metrics=["accuracy"]
)

log_dir = os.path.join("logs", datetime.datetime.now().strftime("%Y%m%d-%H%M%S"))
tensorboard_callback = TensorBoard(log_dir=log_dir, histogram_freq=1)
checkpoint_callback = ModelCheckpoint(
    "chatbot_model_stacked_lstm.h5",
    monitor="val_accuracy",
    save_best_only=True,
    verbose=1,
)
class_weights = class_weight.compute_class_weight(
    'balanced',
    classes=np.unique(labels_encoded),
    y=labels_encoded
)
class_weight_dict = dict(enumerate(class_weights))
early_stop = EarlyStopping(monitor="val_loss", patience=100, restore_best_weights=True)

history = model.fit(
    X_train,
    y_train,
    epochs=300,
    batch_size=32,
    validation_data=(X_val, y_val),
    class_weight=class_weight_dict,
    callbacks=[tensorboard_callback, early_stop, checkpoint_callback],
    verbose=1
)
# Evaluate the model performance on the validation set
loss, accuracy = model.evaluate(X_val, y_val)
print(f"Validation Loss: {loss}")
print(f"Validation Accuracy: {accuracy}")

# Output data to check the preprocessing steps (using training data for example)
print(f"Number of unique labels: {len(label_encoder.classes_)}")
print(f"Sample Training Padded Sequences: {X_train[:5]}")
print(f"Corresponding Training Labels: {y_train[:5]}")

decoded_labels = label_encoder.inverse_transform(y_train[:5])
for i in range(len(decoded_labels)):
    print(f"Pattern: {patterns[np.where(np.array(labels) == decoded_labels[i])[0][0]]}, Label: {decoded_labels[i]}")

# Check for NaN or Inf values in the sequences
print(f"Any NaN values in training padded sequences? {np.isnan(X_train).any()}")
print(f"Any Inf values in training padded sequences? {np.isinf(X_train).any()}")
print(f"Any NaN values in validation padded sequences? {np.isnan(X_val).any()}")
print(f"Any Inf values in validation padded sequences? {np.isinf(X_val).any()}")