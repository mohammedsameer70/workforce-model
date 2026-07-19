from sklearn.ensemble import RandomForestRegressor
from sklearn.model_selection import RandomizedSearchCV


def train_random_forest(X_train, y_train):

    print("\n===================================")
    print("TRAINING RANDOM FOREST")
    print("===================================")

    rf = RandomForestRegressor(random_state=42)

    param_grid = {
        "n_estimators": [100, 200, 300, 500],
        "max_depth": [5, 10, 15, 20, None],
        "min_samples_split": [2, 5, 10],
        "min_samples_leaf": [1, 2, 4],
        "max_features": ["sqrt", "log2"],
    }

    random_search = RandomizedSearchCV(
        estimator=rf,
        param_distributions=param_grid,
        n_iter=10,
        cv=3,
        scoring="neg_root_mean_squared_error",
        random_state=42,
        n_jobs=-1,
        verbose=1,
    )

    random_search.fit(X_train, y_train)

    print("\nBest Parameters")
    print(random_search.best_params_)

    print(f"\nBest CV Score : {-random_search.best_score_:.4f}")

    print("\nRandom Forest Training Completed.")

    return random_search.best_estimator_
