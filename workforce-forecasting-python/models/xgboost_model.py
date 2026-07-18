from xgboost import XGBRegressor


def train_xgboost(X_train, y_train):
    """
    Train XGBoost Regression Model
    """

    print("\n========== XGBOOST TRAINING ==========\n")

    model = XGBRegressor(
        n_estimators=100,
        learning_rate=0.1,
        max_depth=6,
        random_state=42,
        objective="reg:squarederror"
    )

    model.fit(X_train, y_train)

    print("Training completed successfully.")

    return model


def predict_xgboost(model, X_test):
    """
    Make predictions using trained XGBoost model
    """

    print("\n========== MAKING PREDICTIONS ==========\n")

    predictions = model.predict(X_test)

    print("Prediction completed successfully.")

    return predictions