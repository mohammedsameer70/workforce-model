# Workforce Forecasting System

An AI-driven workforce demand forecasting and decision-support system. It predicts staffing needs per department from historical attendance data, using and comparing four machine learning approaches (Linear Regression, Random Forest, XGBoost, and LSTM), and surfaces the results through a live dashboard.

Built as an MSc final year project.

## Architecture

The system is three separate services that talk to each other over HTTP:

```
 Vue.js Frontend  <---->  Spring Boot Backend  <---->  Python ML Service
 (dashboard,               (auth, controllers,          (preprocessing,
  training UI,              services, repositories,      training,
  analytics,                entities)                    prediction)
  employees)                     |
                                  v
                            MySQL Database
```

The backend never performs ML itself — it forwards training/prediction requests to the Python service and persists whatever comes back. This keeps the ML service stateless, independently testable, and swappable.

## Tech stack

**Backend** — Java 17, Spring Boot 3.5, Spring Data JPA, Spring Security (JWT), MySQL, springdoc-openapi (Swagger)

**Frontend** — Vue 3, TypeScript, Vite, PrimeVue, Tailwind CSS, Chart.js, Axios

**ML service** — FastAPI, TensorFlow/Keras, scikit-learn, XGBoost, pandas, NumPy

## Project structure

```
Backend/workForceApplication/
  src/main/java/com/boostphysioclinic/workforceapplication/
    controller/     REST endpoints (Dashboard, Training, Prediction, Employee, Analytics, Auth, ...)
    service/        Business logic
    Repository/      Spring Data JPA repositories
    dto/entity/      JPA entities
    dto/             Data transfer objects
    security/        JWT filter and service
    config/          CORS, security, Swagger config

Frontend/workforce-forecaasting-x/
  src/
    settings/        Feature modules (dashboard, training/aiModels, analytics, employees, ...)
    component/        Shared UI (top bar, etc.)
    sideBar/          Navigation
    auth/             Login
    services/         Shared Axios client
    router/           Vue Router config

workforce-forecasting-python/
  serviceFast/        FastAPI app, controllers, prediction service
  preprocessing/       Feature engineering (calendar + lag features)
  training/            Model training orchestration and model saving
  models/              Linear Regression, Random Forest, XGBoost, LSTM
  evaluation/           RMSE / MAE / MAPE / R² scoring
  tests/                pytest test suite
```

## How it works

1. **Upload** — a CSV of attendance records (`AttendanceDate, Department, WorkforceDemand`) is uploaded through the frontend.
2. **Preprocess** — the ML service engineers calendar features (month, week of year, weekend flag) and lag features (previous-day demand, 3-day/7-day rolling averages per department), then splits the data **chronologically** (not randomly) into train/test sets.
3. **Train** — the selected algorithms are trained and evaluated on the same metrics (RMSE, MAE, MAPE, R²) so results are directly comparable.
4. **Persist** — the backend saves the trained model's metadata and the full model comparison to the database, and the best model is saved to disk.
5. **Predict** — new data is aligned to the exact feature columns the model was trained on (missing columns filled with zero) before predicting.
6. **Dashboard** — the frontend polls the backend every 30 seconds for updated KPIs, charts, and alerts.

## API overview

### Backend (Spring Boot)

| Method | Endpoint | Description |
|---|---|---|
| GET | `/api/dashboard` | Dashboard metrics and chart data |
| GET | `/api/dashboard/predictions` | Prediction records |
| POST | `/api/train` | Train models on an uploaded dataset |
| GET | `/api/train/latest-model` | Most recently trained model |
| GET | `/api/train/model-comparisons` | Comparison across trained models |
| GET/POST/PUT/DELETE | `/api/employees` | Employee management |
| GET | `/api/analytics` | Analytics data |
| POST | `/api/auth/login` \| `/register` \| `/logout` | Authentication |

Additional controllers cover alerts, benchmarking, capacity planning, forecasting, KPI cards, microservice health monitoring, notifications, optimization, reports, settings, and shift optimization.

Full interactive API docs are available via Swagger at `/swagger-ui.html` once the backend is running.

### Python ML service

| Method | Endpoint | Description |
|---|---|---|
| POST | `/train` | Train the selected models on an uploaded CSV |
| POST | `/predict` | Run predictions on an uploaded CSV |

## Getting started

### Prerequisites

- Java 17+
- Node.js (for the frontend)
- Python 3.10+
- MySQL

### 1. Backend

```bash
cd Backend/workForceApplication
```

Create `src/main/resources/application.properties` (not committed — see **Configuration** below), then:

```bash
./gradlew bootRun
```

Runs on `http://localhost:5233`.

### 2. ML service

```bash
cd workforce-forecasting-python
python -m venv .venv
source .venv/bin/activate      # Windows: .venv\Scripts\activate
pip install -r requirements.txt
uvicorn serviceFast.main:app --reload --host 0.0.0.0 --port 8000
```

Runs on `http://localhost:8000`.

### 3. Frontend

```bash
cd Frontend/workforce-forecaasting-x
npm install
npm run dev
```

Runs on `http://localhost:5173`.

## Configuration

`application.properties` is **not committed** to this repo since it contains local secrets. Create it under `Backend/workForceApplication/src/main/resources/` with your own values, following this shape:

```properties
spring.application.name=workForceApplication
server.port=5233

spring.datasource.url=jdbc:mysql://localhost:3306/workforce
spring.datasource.username=YOUR_DB_USERNAME
spring.datasource.password=YOUR_DB_PASSWORD
spring.datasource.driver-class-name=com.mysql.cj.jdbc.Driver

spring.jpa.hibernate.ddl-auto=update
spring.sql.init.mode=always
spring.sql.init.schema-locations=classpath:schema.sql

fastapi.base-url=http://localhost:8000
python.api.url=http://localhost:8000/train
python.download.url=http://localhost:8000/train/cleaned-dataset
prediction.csv.path=/path/to/workforce-forecasting-python/results/predictions.csv

spring.servlet.multipart.max-file-size=100MB
spring.servlet.multipart.max-request-size=100MB

jwt.secret=YOUR_OWN_RANDOM_BASE64_SECRET
jwt.expiration=86400000
```

Required CSV format for training/prediction:

```
AttendanceDate,Department,WorkforceDemand
2024-01-01,Inbound,45
2024-01-01,Outbound,52
```

## Models compared

| Model | Notes |
|---|---|
| Linear Regression | Baseline — fast and interpretable, weak on non-linear patterns |
| Random Forest | Ensemble of decision trees, tuned via `RandomizedSearchCV` |
| XGBoost | Gradient boosting, generally the strongest of the non-deep-learning models |
| LSTM | Two-layer (128 → 64 units) sequence model with batch norm and dropout, for capturing temporal dependencies the others can't |

Metric direction: RMSE, MAE, and MAPE — lower is better. R² — higher is better (0–1).

## Known limitations

- Training is synchronous — the HTTP request stays open for the full duration (can be 15+ minutes for LSTM on larger datasets). A background job queue would be a better fit.
- JWTs are stored in `localStorage` on the frontend, which is convenient but has known XSS exposure.
- CSV input validation is minimal and mostly assumes a well-formed file.

## Testing

```bash
# Backend
cd Backend/workForceApplication && ./gradlew test

# ML service
cd workforce-forecasting-python && pytest
```

## License

Add your chosen license here.
