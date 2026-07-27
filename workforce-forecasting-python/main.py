from .serviceFast.controller.train_pipeline import train_pipeline


def main():

    train_pipeline(
        "dataset/workforce_forecasting_dataset.csv",
        ["Linear Regression", "Random Forest", "XGBoost", "LSTM"],
    )


if __name__ == "__main__":
    main()
