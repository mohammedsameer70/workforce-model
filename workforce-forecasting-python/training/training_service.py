import os

from preprocessing.preprocessing import (
    load_dataset,
    inspect_dataset,
    preprocess_dataset,
    encode_dataset,
    split_features_target,
    split_train_test,
)

from training.model_trainer import ModelTrainer
from training.model_selector import ModelSelector
from training.model_saver import ModelSaver
from training.prediction_result_saver import PredictionResultSaver


class TrainingService:

    def __init__(self):
        self.trainer = ModelTrainer()
        self.selector = ModelSelector()
        self.saver = ModelSaver()
        self.prediction_saver = PredictionResultSaver()

    def train(self, dataset_path, selected_models):

        print("\n====================================")
        print("TRAINING STARTED")
        print("====================================")

        # Load Dataset
        workforce_df = load_dataset(dataset_path)
        inspect_dataset(workforce_df)

        # Preprocess
        workforce_df = preprocess_dataset(workforce_df)

        # Copy for Dashboard/Reports
        dashboard_df = workforce_df.copy()

        # Copy for LSTM
        lstm_df = workforce_df.copy()

        # Encode
        workforce_df = encode_dataset(workforce_df)

        # Split
        X, y = split_features_target(workforce_df)

        X_train, X_test, y_train, y_test = split_train_test(X, y)

        # Train Models
        results, trained_models = self.trainer.train_models(
            selected_models=selected_models,
            X_train=X_train,
            X_test=X_test,
            y_train=y_train,
            y_test=y_test,
            lstm_df=lstm_df,
        )

        # Select Best Model
        best_model = self.selector.select_best(results)
        deployment_model_name = best_model["Model"]

        # If LSTM is selected, use the deployment model instead
        if deployment_model_name == "LSTM":
            for candidate in ["XGBoost", "Random Forest", "Linear Regression"]:
                if candidate in trained_models:
                    deployment_model_name = candidate
                    break

        deployment_model = trained_models[deployment_model_name]
        predictions = deployment_model.predict(X)
        dashboard_df["PredictedDemand"] = predictions
        dashboard_df = dashboard_df[
            [
                "AttendanceDate",
                "Department",
                "Team",
                "Shift",
                "WorkforceDemand",
                "PredictedDemand",
                "CurrentCapacity",
                "RequiredCapacity",
                "AvailableHeadroom",
                "CapacityLoad",
                "CapacityUtilization",
                "UtilizationRate",
                "EfficiencyScore",
                "ProductivityScore",
                "HistoricalDemand",
                "PeakUtilization",
                "ScalingEvents",
                "WorkforceStatus",
                "DayOfWeek",
                "Month",
                "Quarter",
                "Year",
            ]
        ]

        self.prediction_saver.save(dashboard_df)

        # Save Model
        self.saver.save(best_model, trained_models, X.columns.tolist())
        print(f"Rows in X           : {len(X)}")
        print(f"Prediction Count    : {len(predictions)}")

        print("\n====================================")
        print("TRAINING COMPLETED")
        print("====================================")

        return {
            "status": "SUCCESS",
            "message": "Training completed successfully.",
            "bestModel": best_model["Model"],
            "metrics": best_model,
            "comparison": results,
        }
