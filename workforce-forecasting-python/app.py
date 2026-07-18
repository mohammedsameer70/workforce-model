from preprocessing.preprocessing  import load_dataset  , inspect_dataset

# Load dataset
df = load_dataset("dataset/workforce_forecasting_dataset_2024.csv")

# Inspect dataset
inspect_dataset(df)