import os
import numpy as np

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

    # --------------------------------------------------
    # Convert NumPy values
    # --------------------------------------------------

    def convert_numpy(self, obj):

        if isinstance(obj, dict):
            return {k: self.convert_numpy(v) for k, v in obj.items()}

        if isinstance(obj, list):
            return [self.convert_numpy(v) for v in obj]

        if isinstance(obj, np.integer):
            return int(obj)

        if isinstance(obj, np.floating):
            return float(obj)

        if isinstance(obj, np.bool_):
            return bool(obj)

        if isinstance(obj, np.ndarray):
            return obj.tolist()

        return obj

    # --------------------------------------------------
    # Train
    # --------------------------------------------------

    def train(self, dataset_path, selected_models):

        print("\n====================================")
        print("TRAINING STARTED")
        print("====================================")

        # --------------------------------------------
        # Load Dataset
        # --------------------------------------------

        workforce_df = load_dataset(dataset_path)

        inspect_dataset(workforce_df)

        # --------------------------------------------
        # Preprocess Dataset
        # --------------------------------------------

        workforce_df = preprocess_dataset(workforce_df)

        os.makedirs("results", exist_ok=True)

        cleaned_dataset_path = os.path.join(
            "results",
            "cleaned_dataset.csv",
        )

        workforce_df.to_csv(
            cleaned_dataset_path,
            index=False,
        )

        print(f"Cleaned dataset saved : {cleaned_dataset_path}")

        # --------------------------------------------
        # Copies
        # --------------------------------------------

        dashboard_df = workforce_df.copy()

        lstm_df = workforce_df.copy()

        # --------------------------------------------
        # Encode Dataset
        # --------------------------------------------

        encoded_df = encode_dataset(workforce_df.copy())

        X, y = split_features_target(encoded_df)

        X_train, X_test, y_train, y_test = split_train_test(
            X,
            y,
        )

        # --------------------------------------------
        # Train Models
        # --------------------------------------------

        (
            results,
            trained_models,
            X_dashboard,
            target_scaler,
        ) = self.trainer.train_models(
            selected_models=selected_models,
            X_train=X_train,
            X_test=X_test,
            y_train=y_train,
            y_test=y_test,
            lstm_df=lstm_df,
        )
        # --------------------------------------------
        # Best Model
        # --------------------------------------------

        best_model = self.selector.select_best(results)

        if hasattr(best_model, "to_dict"):
            best_model = best_model.to_dict()

        best_model_name = best_model["Model"]

        prediction_model = trained_models[best_model_name]

        print("\n========== DEPLOYMENT DEBUG ==========")
        print("Selected Models :", trained_models.keys())
        print("Best Model      :", best_model_name)
        print("Model Type      :", prediction_model["type"])
        print("======================================")

        # --------------------------------------------
        # Generate Predictions
        # --------------------------------------------

        if prediction_model["type"] == "ml":

            model = prediction_model["model"]

            predictions = model.predict(X)

        else:

            lstm_model = prediction_model["model"]

            X_dashboard = prediction_model["dashboard_data"]

            target_scaler = prediction_model["target_scaler"]

            predictions = lstm_model.predict(
                X_dashboard,
                verbose=0,
            )

            predictions = target_scaler.inverse_transform(predictions).flatten()

            # LSTM loses the first sequence_length rows
            dashboard_df = dashboard_df.iloc[-len(predictions) :].copy()

        print(f"Prediction Count : {len(predictions)}")

        # --------------------------------------------
        # Add Predictions
        # --------------------------------------------

        dashboard_df["PredictedDemand"] = predictions
        # --------------------------------------------
        # Save Dashboard Prediction File
        # --------------------------------------------

        self.prediction_saver.save(dashboard_df)

        # --------------------------------------------
        # Choose Deployment Model
        # --------------------------------------------

        deployment_model = best_model_name

        if deployment_model == "LSTM":

            for candidate in [
                "XGBoost",
                "Random Forest",
                "Linear Regression",
            ]:

                if candidate in trained_models:

                    deployment_model = candidate
                    break

        # --------------------------------------------
        # Save Deployment Model
        # --------------------------------------------

        model_info = best_model.copy()

        model_info["DeploymentModel"] = deployment_model

        self.saver.save(
            model_info,
            trained_models,
            X.columns.tolist(),
        )

        print(f"Rows in Dataset : {len(dashboard_df)}")
        print(f"Predictions     : {len(predictions)}")

        print("\n====================================")
        print("TRAINING COMPLETED")
        print("====================================")

        # --------------------------------------------
        # Convert NumPy Values
        # --------------------------------------------

        model_info = self.convert_numpy(model_info)

        if hasattr(results, "to_dict"):
            results = results.to_dict(orient="records")

        results = self.convert_numpy(results)

        return {
            "status": "SUCCESS",
            "message": "Training completed successfully.",
            "bestModel": best_model_name,
            "deploymentModel": deployment_model,
            "metrics": model_info,
            "comparison": results,
            "cleanedDataset": "cleaned_dataset.csv",
        }
