from xgboost import XGBRegressor
from sklearn.model_selection import RandomizedSearchCV


def train_xgboost(X_train, y_train):

    print("\n===================================")
    print("TRAINING XGBOOST")
    print("===================================")

    xgb = XGBRegressor(
        objective="reg:squarederror",
        random_state=42,
        tree_method="hist",
        n_jobs=-1,
        verbosity=0,
    )

    param_grid = {
        "n_estimators": [100, 200, 300, 500],
        "max_depth": [3, 4, 5, 6, 8],
        "learning_rate": [0.01, 0.03, 0.05, 0.1],
        "subsample": [0.7, 0.8, 0.9, 1.0],
        "colsample_bytree": [0.7, 0.8, 0.9, 1.0],
        "min_child_weight": [1, 3, 5],
        "gamma": [0, 0.1, 0.3, 0.5],
        "reg_alpha": [0, 0.01, 0.1],
        "reg_lambda": [1, 1.5, 2],
    }

    random_search = RandomizedSearchCV(
        estimator=xgb,
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

    print(f"\nBest CV RMSE : {-random_search.best_score_:.4f}")

    print("\nXGBoost Training Completed.")

    return random_search.best_estimator_
