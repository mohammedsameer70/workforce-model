from fastapi import APIRouter, UploadFile, File, Form, HTTPException
import shutil
import traceback
import os
import json
import pandas as pd

from serviceFast.controller.prediction_controller.prediction_router import (
    prediction_service,
)
from training.training_service import TrainingService
from fastapi.responses import FileResponse

router = APIRouter(prefix="/train", tags=["Training"])

training_service = TrainingService()


@router.post("")
async def train_model(file: UploadFile = File(...), algorithms: str = Form(...)):

    try:

        # -----------------------------------------
        # Create uploads folder
        # -----------------------------------------
        os.makedirs("uploads", exist_ok=True)

        dataset_path = os.path.join("uploads", file.filename)

        # -----------------------------------------
        # Save uploaded CSV
        # -----------------------------------------
        with open(dataset_path, "wb") as buffer:
            shutil.copyfileobj(file.file, buffer)

        # -----------------------------------------
        # Convert algorithms
        # Example:
        # "Linear Regression,XGBoost,LSTM"
        # -----------------------------------------
        selected_models = [
            model.strip() for model in algorithms.split(",") if model.strip()
        ]

        # -----------------------------------------
        # Start Training
        # -----------------------------------------
        result = training_service.train(
            dataset_path=dataset_path, selected_models=selected_models
        )

        return result

    except Exception:
        print("\n========== FULL ERROR ==========")
        traceback.print_exc()
        print("================================\n")
        raise HTTPException(
            status_code=500, detail="Training failed. Check server logs."
        )


@router.get("/cleaned-dataset")
async def download_cleaned_dataset():

    file_path = "results/cleaned_dataset.csv"

    if not os.path.exists(file_path):
        raise HTTPException(status_code=404, detail="Cleaned dataset not found.")

    return FileResponse(
        path=file_path, filename="cleaned_dataset.csv", media_type="text/csv"
    )


@router.get("/fetch-data")
def fetch_data():

    return prediction_service.fetch_data()
