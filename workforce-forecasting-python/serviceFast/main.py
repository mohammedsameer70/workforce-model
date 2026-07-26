from fastapi import FastAPI
from serviceFast.controller.workForceController import router

app = FastAPI(title="Workforce Forecasting API", version="1.0.0")

app.include_router(router)
