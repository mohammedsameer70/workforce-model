from preprocessing.lstm_preprocessing import prepare_lstm_data

from models.linear_regression_model import train_linear_regression
from models.random_forest_model import train_random_forest
from models.xgboost_model import train_xgboost
from models.lstm_model import train_lstm

from evaluation.evaluation import evaluate_model
class ModelTrainer:
    def train_models( self, selected_models, X_train, X_test, y_train, y_test,lstm_df,):
        results = []
        trained_models = {}
        # Default values
        X_dashboard = None
        target_scaler = None
        # ======================================================
        # LINEAR REGRESSION
        # ======================================================
        if "Linear Regression" in selected_models:
            print("\nTraining Linear Regression...")
            lr_model = train_linear_regression(X_train, y_train)
            predictions = lr_model.predict(X_test)
            metrics = evaluate_model(y_test,predictions,"Linear Regression",)
            results.append(metrics)
            trained_models["Linear Regression"] = {"type": "ml", "model": lr_model, "dashboard_data": None,"target_scaler": None,}
        # ======================================================
        # RANDOM FOREST
        # ======================================================
        if "Random Forest" in selected_models:
            print("\nTraining Random Forest...")
            rf_model = train_random_forest(X_train, y_train)
            predictions = rf_model.predict(X_test)
            metrics = evaluate_model( y_test, predictions,"Random Forest",)
            results.append(metrics)
            trained_models["Random Forest"] = { "type": "ml", "model": rf_model, "dashboard_data": None, "target_scaler": None,}
        # ======================================================
        # XGBOOST
        # ======================================================
        if "XGBoost" in selected_models:
            print("\nTraining XGBoost...")
            xgb_model = train_xgboost(X_train, y_train)
            predictions = xgb_model.predict(X_test)
            metrics = evaluate_model(y_test,predictions,"XGBoost",)
            results.append(metrics)
            trained_models["XGBoost"] = {"type": "ml","model": xgb_model,}
        # ======================================================
        # LSTM
        # ======================================================
        if "LSTM" in selected_models:
            print("\nTraining LSTM...")
            ( X_train_lstm, X_test_lstm, y_train_lstm, y_test_lstm, X_dashboard, target_scaler, test_departments,) = prepare_lstm_data(lstm_df)
            lstm_model, history = train_lstm( X_train_lstm, y_train_lstm,)
            predictions = lstm_model.predict( X_test_lstm, verbose=0,)
            predictions = target_scaler.inverse_transform(predictions)
            y_actual = target_scaler.inverse_transform(y_test_lstm.reshape(-1, 1))
            metrics = evaluate_model(y_actual.flatten(),predictions.flatten(),"LSTM",)
            results.append(metrics)
            trained_models["LSTM"] = { "type": "lstm", "model": lstm_model, "dashboard_data": X_dashboard,"target_scaler": target_scaler,}
        return (results, trained_models,X_dashboard,target_scaler,)
