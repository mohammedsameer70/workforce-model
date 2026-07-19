from sklearn.linear_model import LinearRegression


def train_linear_regression(X_train, y_train):

    print("\n===================================")
    print("TRAINING LINEAR REGRESSION")
    print("===================================")

    model = LinearRegression()

    model.fit(X_train, y_train)

    train_score = model.score(X_train, y_train)

    print("Linear Regression Training Completed.")
    print(f"Training R² Score : {train_score:.4f}")

    return model
