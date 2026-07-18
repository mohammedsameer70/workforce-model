from sklearn.ensemble import RandomForestRegressor


def train_random_forest(X_train, y_train):
    """
    Train Random Forest Regression Model
    """

    print("\n========== RANDOM FOREST TRAINING ==========\n")

    model = RandomForestRegressor(
        n_estimators=100,
        random_state=42,
        n_jobs=-1
    )

    model.fit(X_train, y_train)

    print("Training completed successfully.")

    return model


def predict_random_forest(model, X_test):
    """
    Make predictions using trained Random Forest model
    """

    print("\n========== MAKING PREDICTIONS ==========\n")

    predictions = model.predict(X_test)

    print("Prediction completed successfully.")

    return predictions