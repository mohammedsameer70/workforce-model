# Workforce Forecasting System - User Manual (Appendix)

## Quick Start

### System Access
- **Frontend**: http://localhost:5173
- **Backend**: http://localhost:8080
- **Python ML Service**: http://localhost:8000
- **Default Login**: admin / admin123

### Starting Services
1. **Backend**: `cd Backend/workForceApplication && ./gradlew bootRun`
2. **Python ML**: `cd workforce-forecasting-python && uvicorn serviceFast.main:app --reload --port 8000`
3. **Frontend**: `cd Frontend/workforce-forecaasting-x && npm run dev`

---

## Main Features

### Dashboard
- **KPI Cards**: Active workforce, forecast accuracy, model status, performance metrics
- **Workforce Chart**: Historical vs predicted demand over time
- **Operational Alerts**: Real-time system notifications (Critical/Warning/Success)
- **Department Performance**: Bar chart comparing department performance
- **Staffing Heatmap**: Shift allocation by department (Morning/Afternoon/Night)

### Training Models
**Supported Algorithms**: Linear Regression, Random Forest, XGBoost, LSTM

**Steps**:
1. Settings → AI Models → Upload Dataset (CSV)
2. Select algorithms (XGBoost recommended)
3. Click "Train Models" (5-15 minutes)
4. Review metrics (RMSE, MAE, MAPE, R²)
5. System auto-selects best model

**Required CSV Columns**: AttendanceDate, Department, WorkforceDemand

### Making Predictions
1. Forecasting → Predictions → Upload Data (CSV)
2. Select trained model
3. Click "Predict" (1-2 minutes)
4. Download results (Date, Department, Actual, Predicted)

### Analytics
- **Performance**: Model accuracy trends, prediction vs actual
- **Department**: Demand comparison, peak periods, staffing efficiency
- **Time-based**: Daily/weekly/monthly patterns
- **Export**: PDF, CSV, Excel formats

### Employee Management
- **Add/Edit/Delete**: Employee records with personal info, department, position
- **Bulk Import**: CSV import for multiple employees
- **Search**: Quick employee lookup

---

## Common Tasks

| Task | Navigation | Time |
|------|------------|------|
| Train Model | Settings → AI Models | 5-15 min |
| Generate Predictions | Forecasting → Predictions | 1-2 min |
| Check Performance | Dashboard | 1 min |
| Add Employee | Employees → Add Employee | 2 min |
| Export Report | Analytics → Export Report | 1 min |

---

## Troubleshooting

| Issue | Solution |
|-------|----------|
| Login Failed | Check credentials, ensure backend running (port 8080) |
| Training Failed | Check CSV format, ensure Python service running (port 8000) |
| Predictions Not Loading | Verify model trained, check CSV format |
| Dashboard Not Updating | Wait 30s auto-refresh or refresh page (F5) |
| Slow Performance | Check internet, close tabs, restart services |

---

## Quick Reference

### Default Ports
- Frontend: 5173
- Backend: 8080
- Python ML: 8000
- Database: 5432

### Required CSV Format
```csv
AttendanceDate,Department,WorkforceDemand
2024-01-01,Inbound,45
2024-01-01,Outbound,52
```

### Model Performance Metrics
- **RMSE**: Lower is better
- **MAE**: Lower is better
- **MAPE**: Lower percentage is better
- **R²**: Higher is better (0-1 scale)

---

**System Version**: 2.0  
**Last Updated**: August 2026
