from preprocessing.preprocessing import (
    load_dataset,
    inspect_dataset,
    preprocess_dataset,
    encode_dataset,
    split_features_target,
    split_train_test,
)
import numpy as np

from preprocessing.lstm_preprocessing import prepare_lstm_data

from models.linear_regression_model import train_linear_regression
from models.random_forest_model import train_random_forest
from models.xgboost_model import train_xgboost
from models.lstm_model import train_lstm

from evaluation.evaluation import (
    evaluate_model,
    save_results,
)


def main():

    # =====================================================
    # LOAD DATASET
    # =====================================================

    dataset_path = "dataset/workforce_forecasting_dataset_3years.csv"

    workforce_df = load_dataset(dataset_path)

    # =====================================================
    # INSPECT DATASET
    # =====================================================

    inspect_dataset(workforce_df)

    # =====================================================
    # PREPROCESS DATASET
    # =====================================================

    workforce_df = preprocess_dataset(workforce_df)

    # Keep a copy for LSTM before encoding
    lstm_df = workforce_df.copy()

    # =====================================================
    # ENCODE DATASET
    # =====================================================

    workforce_df = encode_dataset(workforce_df)

    # =====================================================
    # SPLIT FEATURES & TARGET
    # =====================================================

    X, y = split_features_target(workforce_df)

    X_train, X_test, y_train, y_test = split_train_test(X, y)

    # =====================================================
    # STORE RESULTS
    # =====================================================

    results = []

    # =====================================================
    # LINEAR REGRESSION
    # =====================================================

    print("\n==============================")
    print("LINEAR REGRESSION")
    print("==============================")

    lr_model = train_linear_regression(X_train, y_train)

    lr_predictions = lr_model.predict(X_test)

    results.append(
        evaluate_model(
            y_test,
            lr_predictions,
            "Linear Regression",
        )
    )

    # =====================================================
    # RANDOM FOREST
    # =====================================================

    print("\n==============================")
    print("RANDOM FOREST")
    print("==============================")

    rf_model = train_random_forest(X_train, y_train)

    rf_predictions = rf_model.predict(X_test)

    results.append(
        evaluate_model(
            y_test,
            rf_predictions,
            "Random Forest",
        )
    )

    # =====================================================
    # XGBOOST
    # =====================================================

    print("\n==============================")
    print("XGBOOST")
    print("==============================")

    xgb_model = train_xgboost(X_train, y_train)

    xgb_predictions = xgb_model.predict(X_test)

    results.append(
        evaluate_model(
            y_test,
            xgb_predictions,
            "XGBoost",
        )
    )

    # =====================================================
    # LSTM
    # =====================================================

    print("\n==============================")
    print("LSTM")
    print("==============================")

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
    errors = np.abs(y_actual.flatten() - predictions.flatten())

    print("\n================ DEBUG ================")
    print("Max Error   :", np.max(errors))
    print("Mean Error  :", np.mean(errors))
    print("Median Error:", np.median(errors))

    idx = np.argmax(errors)

    print("\nWorst Prediction")
    print("Index      :", idx)
    print("Actual     :", y_actual[idx][0])
    print("Prediction :", predictions[idx][0])
    print("Error      :", errors[idx])
    print("Department :", test_departments[idx])
    print("=======================================\n")
    results.append(
        evaluate_model(
            y_actual.flatten(),
            predictions.flatten(),
            "LSTM",
        )
    )

    # =====================================================
    # SAVE RESULTS
    # =====================================================

    save_results(results)

    print("\n====================================")
    print("PROJECT COMPLETED SUCCESSFULLY")
    print("====================================")


if __name__ == "__main__":
    main()
