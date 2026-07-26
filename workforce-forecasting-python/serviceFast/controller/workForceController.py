from fastapi import APIRouter, UploadFile, HTTPException
import pandas as pd
import logging
import io

from ..service.dataset_service import DatasetService

logging.basicConfig(level=logging.INFO)
logger = logging.getLogger(__name__)

router = APIRouter()

datasetService = DatasetService()


@router.get("/")
async def home():
    return {"message": "FastAPI is Running"}


@router.post("/predict")
async def predict(file: UploadFile):

    print("STEP 1")

    content = await file.read()

    print("STEP 2")

    df = pd.read_csv(io.BytesIO(content))

    print("STEP 3")
    print(df.head())

    result = datasetService.read_csv(df)

    print("STEP 4")
    print(result)

    return result
