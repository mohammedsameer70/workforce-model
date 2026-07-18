import pandas as pd
from preprocessing.preprocessing import (
    load_dataset,
    inspect_dataset,
    clean_dataset,
    encode_dataset,
    split_features_target,
    split_train_test
)

from models.linear_regression_model import (
    train_linear_regression,
    predict_linear_regression
)

from evaluation.evaluation import (
    evaluate_model
)


def main():

    print("\n========== WORKFORCE FORECASTING ==========\n")

    # Step 1: Load Dataset
    df = load_dataset("dataset/workforce_forecasting_dataset_2024.csv")

    # Step 2: Inspect Dataset
    inspect_dataset(df)

    # Step 3: Clean Dataset
    df, ml_df = clean_dataset(df)

    # Step 4: Encode Dataset
    ml_df = encode_dataset(ml_df)
    print(ml_df.dtypes[ml_df.dtypes == "datetime64[ns]"])
    # Step 5: Split Features and Target
    X, y = split_features_target(ml_df)

    # Step 6: Train-Test Split
    X_train, X_test, y_train, y_test = split_train_test(X, y)
    print("\nChecking for Timestamp values...")

    for col in X_train.columns:
     if X_train[col].apply(lambda x: isinstance(x, pd.Timestamp)).any():
        print("Timestamp found in:", col)

    # Step 7: Train Model
    model = train_linear_regression(X_train, y_train)

    # Step 8: Predict
    predictions = predict_linear_regression(model, X_test)

    # Step 9: Evaluate
    results = evaluate_model(y_test, predictions)

    print("\n========== FINAL RESULTS ==========\n")

    for metric, value in results.items():

        if metric == "MAPE":
            print(f"{metric}: {value:.2f}%")
        else:
            print(f"{metric}: {value:.4f}")


if __name__ == "__main__":
    main()