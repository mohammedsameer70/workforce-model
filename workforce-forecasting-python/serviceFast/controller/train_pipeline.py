import os
import json
import joblib
import numpy as np

from preprocessing.preprocessing import (
    load_dataset,
    inspect_dataset,
    preprocess_dataset,
    encode_dataset,
    split_features_target,
    split_train_test,
)

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


def convert_numpy(obj):

    if isinstance(obj, dict):
        return {k: convert_numpy(v) for k, v in obj.items()}

    if isinstance(obj, list):
        return [convert_numpy(v) for v in obj]

    if isinstance(obj, np.integer):
        return int(obj)

    if isinstance(obj, np.floating):
        return float(obj)

    if isinstance(obj, np.ndarray):
        return obj.tolist()

    return obj


def train_pipeline(dataset_path: str, selected_models: list):

    print("\n====================================")
    print("TRAINING STARTED")
    print("====================================")

    # ------------------------------------
    # Load Dataset
    # ------------------------------------

    workforce_df = load_dataset(dataset_path)

    inspect_dataset(workforce_df)

    # ------------------------------------
    # Preprocess
    # ------------------------------------

    workforce_df = preprocess_dataset(workforce_df)

    os.makedirs("results", exist_ok=True)

    workforce_df.to_csv(
        "results/cleaned_dataset.csv",
        index=False,
    )

    print("Cleaned dataset saved.")

    # ------------------------------------
    # Copies
    # ------------------------------------

    dashboard_df = workforce_df.copy()

    lstm_df = workforce_df.copy()

    # ------------------------------------
    # Encode Dataset
    # ------------------------------------

    encoded_df = encode_dataset(workforce_df.copy())

    X, y = split_features_target(encoded_df)

    X_train, X_test, y_train, y_test = split_train_test(
        X,
        y,
    )

    results = []

    trained_models = {}
