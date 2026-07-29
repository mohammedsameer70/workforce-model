import os
import pandas as pd


class PredictionResultSaver:

    def save(self, dataframe):

        base_path = os.path.dirname(os.path.dirname(__file__))

        results_folder = os.path.join(base_path, "results")
        os.makedirs(results_folder, exist_ok=True)

        output_file = os.path.join(results_folder, "predictions.csv")

        dataframe.to_csv(output_file, index=False)

        print(f"\nPredictions saved to {output_file}")
