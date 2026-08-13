from fastapi import APIRouter, UploadFile, File, HTTPException
import pandas as pd
import traceback

from preprocessing.preprocessing import prepare_prediction_data
from serviceFast.model_service.prediction_service import PredictionService

router = APIRouter(
    prefix="/predict",
    tags=["Prediction"],
)

prediction_service = PredictionService()


@router.post("")
async def predict(file: UploadFile = File(...)):

    try:

        # Read uploaded CSV
        dataframe = pd.read_csv(file.file)

        # Apply preprocessing
        prediction_df = prepare_prediction_data(dataframe)

        # Predict
        result = prediction_service.predict(prediction_df)

        return result

    except Exception:

        print("\n========== PREDICTION ERROR ==========")
        traceback.print_exc()
        print("======================================\n")

        raise HTTPException(
            status_code=500,
            detail="Prediction failed. Check server logs.",
        )
