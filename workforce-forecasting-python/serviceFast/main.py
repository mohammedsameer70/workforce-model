from fastapi import FastAPI

from serviceFast.controller.train_controller import router as train_router
from serviceFast.controller.prediction_controller.prediction_router import (
    router as prediction_router,
)

app = FastAPI()

app.include_router(train_router)
app.include_router(prediction_router)
