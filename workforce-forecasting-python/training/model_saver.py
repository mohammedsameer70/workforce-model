import os
import json
import joblib
import numpy as np


class ModelSaver:

    # ----------------------------------------------------
    # Convert NumPy values to Python values
    # ----------------------------------------------------
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

    # ----------------------------------------------------
    # Save Model
    # ----------------------------------------------------
    def save(self, best_model, trained_models, feature_columns):

        model_name = best_model["Model"]

        # ----------------------------------------------------
        # If LSTM is best, save the best available ML model
        # ----------------------------------------------------

        if model_name == "LSTM":

            print("\nLSTM was selected as the best model.")
            print("Searching for the best ML model for deployment...")

            for candidate in [
                "XGBoost",
                "Random Forest",
                "Linear Regression",
            ]:

                if candidate in trained_models:
                    model_name = candidate
                    break

        # ----------------------------------------------------
        # Get model object
        # ----------------------------------------------------

        model = trained_models[model_name]["model"]

        os.makedirs("saved_models", exist_ok=True)

        # ----------------------------------------------------
        # Save trained model
        # ----------------------------------------------------

        joblib.dump(
            model,
            "saved_models/best_model.pkl",
        )

        # ----------------------------------------------------
        # Save feature columns
        # ----------------------------------------------------

        with open(
            "saved_models/feature_columns.json",
            "w",
        ) as file:

            json.dump(
                feature_columns,
                file,
                indent=4,
            )

        # ----------------------------------------------------
        # Save model information
        # ----------------------------------------------------

        info = self.convert_numpy(best_model.copy())

        info["DeploymentModel"] = model_name

        with open(
            "saved_models/model_info.json",
            "w",
        ) as file:

            json.dump(
                info,
                file,
                indent=4,
            )

        print("\n====================================")
        print("MODEL SAVED SUCCESSFULLY")
        print("====================================")

        print(f"Best Model       : {best_model['Model']}")
        print(f"Deployment Model : {model_name}")

        print("\nGenerated Files")
        print("saved_models/best_model.pkl")
        print("saved_models/feature_columns.json")
        print("saved_models/model_info.json")
