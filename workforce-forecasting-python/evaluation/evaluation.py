import os
import numpy as np
import pandas as pd

from sklearn.metrics import (
    mean_squared_error,
    mean_absolute_error,
    r2_score,
)


def evaluate_model(y_true, predictions, model_name):

    # ============================================
    # Convert to NumPy
    # ============================================

    y_true = np.asarray(y_true).flatten()
    predictions = np.asarray(predictions).flatten()

    # ============================================
    # Metrics
    # ============================================

    rmse = np.sqrt(
        mean_squared_error(
            y_true,
            predictions,
        )
    )

    mae = mean_absolute_error(
        y_true,
        predictions,
    )

    epsilon = 1e-8

    mape = np.mean(np.abs((y_true - predictions) / (y_true + epsilon))) * 100

    r2 = r2_score(
        y_true,
        predictions,
    )

    print("\n")
    print("=" * 60)
    print(f"{model_name.upper()} RESULTS")
    print("=" * 60)

    print(f"RMSE : {rmse:.4f}")
    print(f"MAE  : {mae:.4f}")
    print(f"MAPE : {mape:.2f}%")
    print(f"R²   : {r2:.4f}")

    return {
        "Model": model_name,
        "RMSE": round(rmse, 4),
        "MAE": round(mae, 4),
        "MAPE": round(mape, 2),
        "R2": round(r2, 4),
    }


def save_results(results):

    os.makedirs("results", exist_ok=True)

    comparison = pd.DataFrame(results)

    comparison = comparison.sort_values(
        by="RMSE",
        ascending=True,
    ).reset_index(drop=True)

    print("\n")
    print("=" * 80)
    print("MODEL COMPARISON")
    print("=" * 80)

    print(comparison)

    comparison.to_csv(
        "results/model_comparison.csv",
        index=False,
    )

    print("\nResults saved successfully.")

    print("Location : results/model_comparison.csv")

    print("\nBest Model")

    print(comparison.iloc[0])
