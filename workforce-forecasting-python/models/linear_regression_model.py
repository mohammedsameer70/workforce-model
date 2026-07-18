
from sklearn.linear_model import LinearRegression


def train_linear_regression(X_train, y_train):
    """
    Train a Linear Regression model.
    """

    print("\n========== LINEAR REGRESSION TRAINING ==========\n")

    model = LinearRegression()

    model.fit(X_train, y_train)

    print("Training completed successfully.")

    return model


def predict_linear_regression(model, X_test):
    """
    Predict Workforce Demand.
    """

    print("\n========== MAKING PREDICTIONS ==========\n")

    predictions = model.predict(X_test)

    print("Prediction completed successfully.")

    return predictions