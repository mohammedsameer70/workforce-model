import time

from preprocessing.preprocessing import (
    load_dataset,
    inspect_dataset,
    clean_dataset,
    encode_dataset,
    split_features_target,
    split_train_test
)

from evaluation.evaluation import evaluate_model

# ===========================
# Import Models
# ===========================

from models.linear_regression_model import (
    train_linear_regression,
    predict_linear_regression
)

from models.random_forest_model import (
    train_random_forest,
    predict_random_forest
)

from models.xgboost_model import (
    train_xgboost,
    predict_xgboost
)

from models.lstm_model import (
    train_lstm,
    predict_lstm
)

# ===================================================
# Select Model
# ===================================================

MODEL = "linear_regression"

# Available Options:
# linear_regression
# random_forest
# xgboost
# lstm
# all


# ===================================================
# Model Dictionary
# ===================================================

MODELS = {
    "linear_regression": {
        "name": "Linear Regression",
        "train": train_linear_regression,
        "predict": predict_linear_regression
    },
    "random_forest": {
        "name": "Random Forest",
        "train": train_random_forest,
        "predict": predict_random_forest
    },
    "xgboost": {
        "name": "XGBoost",
        "train": train_xgboost,
        "predict": predict_xgboost
    },
    "lstm": {
        "name": "LSTM",
        "train": train_lstm,
        "predict": predict_lstm
    }
}


# ===================================================
# Run Model
# ===================================================

def run_model(model_key, X_train, X_test, y_train, y_test):

    model_info = MODELS[model_key]

    print("\n" + "=" * 70)
    print(f"RUNNING MODEL : {model_info['name']}")
    print("=" * 70)

    print(f"Training Samples : {len(X_train)}")
    print(f"Testing Samples  : {len(X_test)}")
    print(f"Features         : {X_train.shape[1]}")

    start_time = time.time()

    # Train Model
    model = model_info["train"](X_train, y_train)

    # Predict
    predictions = model_info["predict"](model, X_test)

    # Evaluate
    results = evaluate_model(y_test, predictions)

    end_time = time.time()

    execution_time = end_time - start_time

    print("\nMODEL SUMMARY")
    print("-" * 35)
    print(f"Model          : {model_info['name']}")
    print(f"RMSE           : {results['RMSE']:.4f}")
    print(f"MAE            : {results['MAE']:.4f}")
    print(f"MAPE           : {results['MAPE']:.2f}%")
    print(f"R²             : {results['R2']:.4f}")
    print(f"Execution Time : {execution_time:.2f} seconds")

    results["Execution Time"] = execution_time

    return results


# ===================================================
# Main
# ===================================================

def main():

    print("\n========== WORKFORCE FORECASTING ==========\n")

    print(f"Selected Model : {MODEL}")

    # Load Dataset
    df = load_dataset(
        "dataset/workforce_forecasting_dataset_2024.csv"
    )

    # Inspect Dataset
    inspect_dataset(df)

    # Clean Dataset
    df, ml_df = clean_dataset(df)

    # Encode Dataset
    ml_df = encode_dataset(ml_df)

    # Features & Target
    X, y = split_features_target(ml_df)

    # Train Test Split
    X_train, X_test, y_train, y_test = split_train_test(X, y)

    # ===================================================

    if MODEL != "all":

        run_model(
            MODEL,
            X_train,
            X_test,
            y_train,
            y_test
        )

    else:

        comparison_results = {}

        for model in MODELS.keys():

            comparison_results[model] = run_model(
                model,
                X_train,
                X_test,
                y_train,
                y_test
            )

        print("\n")
        print("=" * 100)
        print("FINAL MODEL COMPARISON")
        print("=" * 100)

        print(
            f'{"Model":25}'
            f'{"RMSE":>10}'
            f'{"MAE":>10}'
            f'{"MAPE":>12}'
            f'{"R²":>10}'
            f'{"Time(s)":>12}'
        )

        print("-" * 100)

        for model_key, metrics in comparison_results.items():

            print(
                f'{MODELS[model_key]["name"]:25}'
                f'{metrics["RMSE"]:10.4f}'
                f'{metrics["MAE"]:10.4f}'
                f'{metrics["MAPE"]:11.2f}%'
                f'{metrics["R2"]:10.4f}'
                f'{metrics["Execution Time"]:12.2f}'
            )


if __name__ == "__main__":
    main()