from fastapi import APIRouter, UploadFile, File, HTTPException
import pandas as pd

from preprocessing.preprocessing import prepare_prediction_data
from serviceFast.model_service.prediction_service import PredictionService

router = APIRouter(prefix="/predict", tags=["Prediction"])


@router.post("")
async def predict(file: UploadFile = File(...)):
    try:
        prediction_service = PredictionService()  # <-- create here

        df = pd.read_csv(file.file)
        prediction_df = prepare_prediction_data(df)

        return prediction_service.predict(prediction_df)

    except Exception as e:
        raise HTTPException(status_code=500, detail=str(e))
