from fastapi import APIRouter, UploadFile
import pandas as pd

from ..service.dataset_service import DatasetService

router = APIRouter()

datasetService = DatasetService()


@router.get("/")
async def home():
    return {"message": "Hello"}


@router.post("/predict")
async def predict(file: UploadFile):
    df = pd.read_csv(file.file)
    return datasetService.read_csv(df)
