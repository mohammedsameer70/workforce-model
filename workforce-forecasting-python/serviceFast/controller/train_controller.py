from fastapi import APIRouter, UploadFile, File, Form, HTTPException
import os
import shutil

from training.training_service import TrainingService

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

    except Exception as e:
        raise HTTPException(status_code=500, detail=str(e))
