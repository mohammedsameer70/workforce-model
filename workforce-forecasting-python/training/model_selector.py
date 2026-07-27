class ModelSelector:

    def select_best(self, results):

        if not results:
            raise Exception("No models were trained.")

        best_model = min(results, key=lambda x: x["RMSE"])

        print("\n====================================")
        print("BEST MODEL")
        print("====================================")

        print(f"Model : {best_model['Model']}")
        print(f"RMSE  : {best_model['RMSE']}")
        print(f"MAE   : {best_model['MAE']}")
        print(f"MAPE  : {best_model['MAPE']}")
        print(f"R²    : {best_model['R2']}")

        return best_model
