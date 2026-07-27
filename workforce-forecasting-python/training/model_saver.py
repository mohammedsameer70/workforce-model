import os
import json
import joblib


class ModelSaver:

    def save(self, best_model, trained_models, feature_columns):

        model_name = best_model["Model"]

        # ----------------------------------------------------
        # LSTM cannot be used by the current PredictionService
        # ----------------------------------------------------
        if model_name == "LSTM":

            print("\nLSTM was selected as the best model.")
            print("Searching for the best ML model for deployment...")

            for candidate in ["XGBoost", "Random Forest", "Linear Regression"]:
                if candidate in trained_models:
                    model_name = candidate
                    break

        model = trained_models[model_name]

        os.makedirs("saved_models", exist_ok=True)

        # ----------------------------------------------------
        # Save trained model
        # ----------------------------------------------------
        joblib.dump(model, "saved_models/best_model.pkl")

        # ----------------------------------------------------
        # Save feature columns
        # ----------------------------------------------------
        with open("saved_models/feature_columns.json", "w") as file:

            json.dump(feature_columns, file, indent=4)

        # ----------------------------------------------------
        # Save model information
        # ----------------------------------------------------
        model_info = best_model.copy()

        model_info["DeploymentModel"] = model_name

        with open("saved_models/model_info.json", "w") as file:

            json.dump(model_info, file, indent=4)

        print("\n====================================")
        print("MODEL SAVED SUCCESSFULLY")
        print("====================================")

        print(f"Deployment Model : {model_name}")

        print("\nGenerated Files")

        print("saved_models/best_model.pkl")
        print("saved_models/feature_columns.json")
        print("saved_models/model_info.json")
