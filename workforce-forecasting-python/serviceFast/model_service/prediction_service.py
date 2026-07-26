import joblib
import json
import os


class PredictionService:

    def __init__(self):

        base_path = os.path.dirname(os.path.dirname(os.path.dirname(__file__)))

        model_path = os.path.join(base_path, "saved_models", "best_model.pkl")
        feature_path = os.path.join(base_path, "saved_models", "feature_columns.json")
        info_path = os.path.join(base_path, "saved_models", "model_info.json")

        self.model = joblib.load(model_path)

        with open(feature_path, "r") as file:
            self.training_columns = json.load(file)

        with open(info_path, "r") as file:
            self.model_info = json.load(file)

    def predict(self, dataframe):

        # Add missing columns
        for column in self.training_columns:
            if column not in dataframe.columns:
                dataframe[column] = 0

        # Keep only training columns
        dataframe = dataframe[self.training_columns]

        predictions = self.model.predict(dataframe)

        return {
            "model": self.model_info["Model"],
            "total_records": len(predictions),
            "predictions": predictions.tolist(),
        }
