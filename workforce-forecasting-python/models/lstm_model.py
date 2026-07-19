import tensorflow as tf

from keras.models import Sequential
from keras.layers import (
    Input,
    LSTM,
    Dense,
    Dropout,
    BatchNormalization,
)

from keras.callbacks import (
    EarlyStopping,
    ReduceLROnPlateau,
)


def train_lstm(X_train, y_train):

    print("\n===================================")
    print("TRAINING LSTM")
    print("===================================")

    # =====================================================
    # BUILD MODEL
    # =====================================================

    model = Sequential(
        [
            Input(
                shape=(
                    X_train.shape[1],
                    X_train.shape[2],
                )
            ),
            # -------------------------
            # LSTM Block 1
            # -------------------------
            LSTM(
                128,
                return_sequences=True,
            ),
            BatchNormalization(),
            Dropout(0.30),
            # -------------------------
            # LSTM Block 2
            # -------------------------
            LSTM(
                64,
                return_sequences=False,
            ),
            BatchNormalization(),
            Dropout(0.30),
            # -------------------------
            # Dense Layers
            # -------------------------
            Dense(
                32,
                activation="relu",
            ),
            Dropout(0.20),
            Dense(
                16,
                activation="relu",
            ),
            Dense(
                1,
            ),
        ]
    )

    # =====================================================
    # COMPILE MODEL
    # =====================================================

    optimizer = tf.keras.optimizers.Adam(learning_rate=0.001)

    model.compile(
        optimizer=optimizer,
        loss="mse",
        metrics=[tf.keras.metrics.RootMeanSquaredError(name="rmse")],
    )

    # =====================================================
    # CALLBACKS
    # =====================================================

    early_stopping = EarlyStopping(
        monitor="val_loss",
        patience=12,
        restore_best_weights=True,
        verbose=1,
    )

    reduce_lr = ReduceLROnPlateau(
        monitor="val_loss",
        factor=0.5,
        patience=5,
        min_lr=1e-5,
        verbose=1,
    )

    # =====================================================
    # TRAIN MODEL
    # =====================================================

    history = model.fit(
        X_train,
        y_train,
        epochs=150,
        batch_size=64,
        validation_split=0.20,
        shuffle=False,
        callbacks=[
            early_stopping,
            reduce_lr,
        ],
        verbose=1,
    )

    # =====================================================
    # TRAIN PERFORMANCE
    # =====================================================

    train_loss, train_rmse = model.evaluate(
        X_train,
        y_train,
        verbose=0,
    )

    print("\nLSTM Training Completed.")
    print(f"Training RMSE : {train_rmse:.4f}")

    print("\n===================================")
    print("MODEL SUMMARY")
    print("===================================")

    model.summary()

    return (
        model,
        history,
    )
