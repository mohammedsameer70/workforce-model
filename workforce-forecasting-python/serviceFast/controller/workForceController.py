from fastapi import APIRouter, UploadFile
import pandas as pd
import io

from ..model_service.prediction_service import PredictionService

router = APIRouter()

predictionService = PredictionService()


@router.get("/")
async def home():
    return {"message": "FastAPI is Running"}


@router.post("/predict")
async def predict(file: UploadFile):

    content = await file.read()
    df = pd.read_csv(io.BytesIO(content))
    print(df.columns.tolist())

    from preprocessing.preprocessing import prepare_prediction_data

    X = prepare_prediction_data(df)

    return predictionService.predict(X)
