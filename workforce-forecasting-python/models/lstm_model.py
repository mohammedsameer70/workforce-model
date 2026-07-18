import numpy as np

from tensorflow.keras.models import Sequential
from tensorflow.keras.layers import LSTM, Dense
from tensorflow.keras.optimizers import Adam


def train_lstm(X_train, y_train):
    """
    Train LSTM Model
    """

    print("\n========== LSTM TRAINING ==========\n")

    # Reshape data
    X_train = np.array(X_train)
    X_train = X_train.reshape(
        X_train.shape[0],
        X_train.shape[1],
        1
    )

    model = Sequential()

    model.add(
        LSTM(
            units=64,
            input_shape=(X_train.shape[1], 1)
        )
    )

    model.add(Dense(32, activation="relu"))
    model.add(Dense(1))

    model.compile(
        optimizer=Adam(learning_rate=0.001),
        loss="mse"
    )

    model.fit(
        X_train,
        y_train,
        epochs=20,
        batch_size=32,
        verbose=1
    )

    print("Training completed successfully.")

    return model


def predict_lstm(model, X_test):
    """
    Make predictions using trained LSTM Model
    """

    print("\n========== MAKING PREDICTIONS ==========\n")

    X_test = np.array(X_test)

    X_test = X_test.reshape(
        X_test.shape[0],
        X_test.shape[1],
        1
    )

    predictions = model.predict(
        X_test,
        verbose=0
    )

    print("Prediction completed successfully.")

    return predictions.flatten()