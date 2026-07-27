from preprocessing.preprocessing import (
    load_dataset,
    inspect_dataset,
    preprocess_dataset,
    encode_dataset,
    split_features_target,
    split_train_test,
)
import os
import json
import joblib
from preprocessing.lstm_preprocessing import prepare_lstm_data

from models.linear_regression_model import train_linear_regression
from models.random_forest_model import train_random_forest
from models.xgboost_model import train_xgboost
from models.lstm_model import train_lstm

from evaluation.evaluation import (
    evaluate_model,
    save_results,
)

import numpy as np


def train_pipeline(dataset_path: str, selected_models: list):

    print("\n====================================")
    print("TRAINING STARTED")
    print("====================================")

    # ===========================================
    # LOAD DATASET
    # ===========================================

    workforce_df = load_dataset(dataset_path)

    inspect_dataset(workforce_df)

    # ===========================================
    # PREPROCESS
    # ===========================================

    workforce_df = preprocess_dataset(workforce_df)

    # Keep original copy for LSTM

    lstm_df = workforce_df.copy()

    # ===========================================
    # ENCODE
    # ===========================================

    workforce_df = encode_dataset(workforce_df)

    # ===========================================
    # SPLIT
    # ===========================================

    X, y = split_features_target(workforce_df)

    X_train, X_test, y_train, y_test = split_train_test(X, y)

    # ===========================================
    # STORE RESULTS
    # ===========================================

    results = []

    trained_models = {}

    # ===========================================
    # LINEAR REGRESSION
    # ===========================================

    if "Linear Regression" in selected_models:

        print("\nTraining Linear Regression...")

        lr_model = train_linear_regression(X_train, y_train)

        predictions = lr_model.predict(X_test)

        metrics = evaluate_model(
            y_test,
            predictions,
            "Linear Regression",
        )

        results.append(metrics)

        trained_models["Linear Regression"] = lr_model

    # ===========================================
    # RANDOM FOREST
    # ===========================================

    if "Random Forest" in selected_models:

        print("\nTraining Random Forest...")

        rf_model = train_random_forest(X_train, y_train)

        predictions = rf_model.predict(X_test)

        metrics = evaluate_model(
            y_test,
            predictions,
            "Random Forest",
        )

        results.append(metrics)

        trained_models["Random Forest"] = rf_model

    # ===========================================
    # XGBOOST
    # ===========================================

    if "XGBoost" in selected_models:

        print("\nTraining XGBoost...")

        xgb_model = train_xgboost(X_train, y_train)

        predictions = xgb_model.predict(X_test)

        metrics = evaluate_model(
            y_test,
            predictions,
            "XGBoost",
        )

        results.append(metrics)

        trained_models["XGBoost"] = xgb_model

    # ===========================================
    # LSTM
    # ===========================================

    if "LSTM" in selected_models:

        print("\nTraining LSTM...")

        (
            X_train_lstm,
            X_test_lstm,
            y_train_lstm,
            y_test_lstm,
            target_scaler,
            test_departments,
        ) = prepare_lstm_data(lstm_df)

        lstm_model, history = train_lstm(
            X_train_lstm,
            y_train_lstm,
        )

        predictions = lstm_model.predict(X_test_lstm)

        predictions = target_scaler.inverse_transform(predictions)

        y_actual = target_scaler.inverse_transform(y_test_lstm.reshape(-1, 1))

        metrics = evaluate_model(
            y_actual.flatten(),
            predictions.flatten(),
            "LSTM",
        )

        results.append(metrics)

        trained_models["LSTM"] = lstm_model

    # ===========================================
    # SAVE RESULTS
    # ===========================================

    save_results(results)
    # ===========================================
    # SAVE BEST MODEL
    # ===========================================

    best_model_metrics = min(results, key=lambda x: x["RMSE"])

    best_model_name = best_model_metrics["Model"]

    best_model_object = trained_models[best_model_name]

    os.makedirs("saved_models", exist_ok=True)

    # Save trained model
    joblib.dump(best_model_object, "saved_models/best_model.pkl")

    # Save feature names
    with open("saved_models/feature_columns.json", "w") as file:
        json.dump(list(X.columns), file, indent=4)

    # Save best model information
    with open("saved_models/model_info.json", "w") as file:
        json.dump(best_model_metrics, file, indent=4)

    print("\n====================================")
    print("BEST MODEL SAVED")
    print("====================================")
    print(f"Model : {best_model_name}")

    # ===========================================
    # FIND BEST MODEL
    # ===========================================

    best_model = best_model_metrics

    print("\n====================================")
    print("TRAINING COMPLETED")
    print("====================================")

    return {
        "status": "SUCCESS",
        "trainedModels": selected_models,
        "bestModel": best_model["Model"],
        "results": results,
    }
