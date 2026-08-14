import joblib
import json
import os
import pandas as pd


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

        # Add missing columns using concat to avoid fragmentation
        missing_columns = [col for col in self.training_columns if col not in dataframe.columns]
        if missing_columns:
            missing_df = pd.DataFrame(0, index=dataframe.index, columns=missing_columns)
            dataframe = pd.concat([dataframe, missing_df], axis=1)

        # Keep only training columns
        dataframe = dataframe[self.training_columns]

        predictions = self.model.predict(dataframe)

        # Create results with metadata for backend
        results = []
        for i, pred in enumerate(predictions):
            result = {
                "attendanceDate": dataframe.iloc[i].get("AttendanceDate", ""),
                "department": dataframe.iloc[i].get("Department", ""),
                "actualDemand": dataframe.iloc[i].get("WorkforceDemand", None),
                "predictedDemand": float(pred)
            }
            results.append(result)

        return {
            "model": self.model_info["Model"],
            "total_records": len(predictions),
            "predictions": predictions.tolist(),
            "results": results
        }
