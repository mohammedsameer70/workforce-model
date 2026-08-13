# Workforce Forecasting Application - Complete Frontend API Analysis Report

**Report Date:** 2026-07-28  
**Project:** AI Workforce Forecasting System  
**Frontend Framework:** Vue 3 + TypeScript + Vite  
**UI Component Library:** PrimeVue 4.x  

---

## Executive Summary

This report provides a complete analysis of the Workforce Forecasting Application frontend. The application currently uses **100% hardcoded/dummy JSON data** with no active backend API integrations. All components display mock data for demonstration purposes.

**Key Findings:**
- 12 modules with complete UI implementation
- 0 active API endpoints consumed
- Prediction endpoint exists in FastAPI backend (`POST /predict`) but not integrated in UI
- Multiple data sources should be separated:
  - **Prediction-specific data** → FastAPI `/predict` endpoint
  - **Operational data** (employees, settings, reports) → Spring Boot REST API (separate endpoints)
  - **Infrastructure/monitoring data** → Separate monitoring system
  
---

## PART 1: MODULE-BY-MODULE ANALYSIS

---

## 1. Dashboard

**Route:** `/dashboard`  
**Component:** `dashboardComponent.vue`  
**Purpose:** Operations command center with real-time KPIs and alerts

### KPI Cards (Metrics Display)
```
┌─────────────────────────────────────────┐
│ Workforce Status                        │
│ • Assigned: 1,847 employees             │
│ • Available: 287 employees              │
│ • Absence Rate: 8.2%                    │
│ • Orders Processed: 89.1%               │
│ • Avg Productivity: 78.4%               │
│ • Capacity Load: 99.97%                 │
│ • API Uptime: 100%                      │
└─────────────────────────────────────────┘
```

### Required Response Fields (Dashboard API)
```json
{
  "metrics": {
    "assigned_employees": 1847,
    "available_employees": 287,
    "absence_rate": 8.2,
    "orders_processed_percent": 89.1,
    "avg_productivity_percent": 78.4,
    "capacity_load_percent": 99.97,
    "api_uptime_percent": 100.0,
    "change_order_processed": 2.1,
    "change_productivity": 0.0,
    "change_capacity": 0.02,
    "change_uptime": 0.0
  },
  "workforce_chart": {
    "labels": ["January", "February", ..., "July"],
    "datasets": [
      {
        "label": "Dataset 1",
        "data": [65, 59, 80, 81, 56, 55, 10]
      },
      {
        "label": "Dataset 2",
        "data": [72, 48, 79, 70, 65, 50, 20]
      }
    ]
  },
  "department_performance": {
    "labels": ["Inbound", "Outbound", "Sortation", "Packing", "Returns", "Quality Control"],
    "datasets": [
      {
        "label": "Staffing",
        "data": [81, 84, 114, 119, 84, 113]
      }
    ]
  },
  "staffing_by_shift": [
    {
      "department": "Inbound",
      "morning": 22,
      "afternoon": 40,
      "night": 19,
      "total": 81
    },
    // ... repeat for each department
  ],
  "microservice_health": [
    {
      "name": "forecast-service",
      "instances": 3,
      "cpu_percent": 34,
      "memory_percent": 62,
      "status": "healthy"
    },
    // ... repeat for all 8 services
  ],
  "alerts": [
    {
      "severity": "critical",
      "title": "Outbound department understaffed by 12 workers for afternoon shift",
      "time": "2 min ago",
      "department": "Outbound",
      "icon": "pi pi-users"
    },
    // ... repeat for all alerts
  ]
}
```

### Charts Required
1. **Workforce Trend Chart (Line)** - 2 datasets over 7 months
2. **Department Performance Chart (Bar)** - Staffing levels by department
3. **Staffing Heatmap Table** - Shift allocation matrix (morning/afternoon/night)

### Alerts Table
- Severity levels: critical, warning, success
- Real-time operational alerts with timestamps

### Data Status
- **Status:** 100% Hardcoded
- **Data Sources:** All in component state refs

---

## 2. Forecasting

**Route:** `/forecasting`  
**Component:** `foreCastingComponent.vue`  
**Purpose:** Workforce demand predictions and model analytics

### KPI Cards (Model Metrics)
```
• LSTM v3.2 (Active Model)
• 94.2% Accuracy (MAPE)
• 3.8 RMSE Score
• 2,847 Predictions Today
```

### Required Response Fields (Forecasting API)
```json
{
  "active_model": {
    "name": "LSTM v3.2",
    "status": "Active",
    "accuracy_mape_percent": 94.2,
    "rmse_score": 3.8,
    "predictions_today": 2847
  },
  "hourly_demand_prediction": {
    "labels": ["00:00", "01:00", ..., "23:00"],
    "datasets": [
      {
        "label": "Predicted Demand",
        "data": [/* 24 hourly values */]
      },
      {
        "label": "Actual Demand",
        "data": [/* 24 hourly values */]
      }
    ]
  },
  "weekly_department_performance": {
    "labels": ["Mon", "Tue", "Wed", "Thu", "Fri", "Sat", "Sun"],
    "datasets": [
      {
        "label": "Inbound",
        "data": [/* 7 daily values */]
      },
      // ... repeat for all departments
    ]
  },
  "department_staffing": [
    {
      "department": "Inbound",
      "morning": 22,
      "afternoon": 40,
      "night": 19,
      "total": 81
    },
    // ... repeat for all 6 departments
  ],
  "radar_chart_data": {
    "labels": ["Eating", "Drinking", "Sleeping", "Designing", "Coding", "Cycling", "Running"],
    "datasets": [
      {
        "label": "Dataset 1",
        "data": [65, 59, 90, 81, 56, 55, 40]
      },
      {
        "label": "Dataset 2",
        "data": [28, 48, 40, 19, 96, 27, 100]
      }
    ]
  },
  "microservices": [
    {
      "name": "forecast-service",
      "instances": 3,
      "cpu_percent": 34,
      "memory_percent": 62,
      "status": "healthy"
    },
    // ... repeat for all microservices
  ]
}
```

### Charts Required
1. **24-Hour Demand Prediction (Line)** - Hourly predictions vs actual
2. **Weekly Department Performance (Bar)** - 7 days of data
3. **Staffing Radar Chart** - 7-point radar visualization
4. **Microservices Status** - Health metrics for all services

### Tab Views
- **Tab 1:** 24-hour forecast (hourly granularity)
- **Tab 2:** Weekly forecast (daily granularity)
- **Staffing Table:** Current shift allocation by department and shift

### Data Status
- **Status:** 100% Hardcoded
- **Predictions:** Mock data with no actual model integration

---

## 3. AI Models

**Route:** `/ai-models`  
**Component:** `aiModelsComponent.vue` + child components  
**Purpose:** Model management, training, comparison, and evaluation

### Sub-Components & Data Requirements

#### 3.1 Model Stats Cards
```json
{
  "model_stats": {
    "best_model": "XGBoost",
    "r2_score": 0.94,
    "rmse": 3.76,
    "training_status": "Ready"
  }
}
```

#### 3.2 Training Section (TrainingSection.vue)
**File Upload Fields:**
- Dataset upload (CSV files)
- Supported algorithms: Linear Regression, Random Forest, XGBoost, LSTM

**Progress Tracking:**
- Real-time training progress (0-100%)
- Training status indicator

**API Endpoint Needed:** `POST /train`
```json
{
  "request": {
    "file": "multipart/form-data",
    "algorithms": ["XGBoost", "LSTM", "Random Forest"]
  },
  "response": {
    "training_id": "TRAIN-001",
    "status": "training",
    "progress": 0,
    "estimated_time": "10m"
  }
}
```

#### 3.3 Model Comparison Table (ModelComparison.vue)
```json
{
  "models": [
    {
      "name": "XGBoost",
      "rmse": 3.76,
      "mae": 2.78,
      "mape_percent": 2.37,
      "r2": 0.94,
      "training_time": "2m 14s",
      "status": "Best"
    },
    {
      "name": "LSTM",
      "rmse": 4.1,
      "mae": 3.01,
      "mape_percent": 2.91,
      "r2": 0.91,
      "training_time": "8m 32s",
      "status": "Good"
    },
    {
      "name": "Random Forest",
      "rmse": 4.58,
      "mae": 3.44,
      "mape_percent": 3.12,
      "r2": 0.88,
      "training_time": "1m 47s",
      "status": "Good"
    },
    {
      "name": "Linear Regression",
      "rmse": 7.23,
      "mae": 5.89,
      "mape_percent": 6.45,
      "r2": 0.74,
      "training_time": "0m 08s",
      "status": "Poor"
    }
  ]
}
```

#### 3.4 Prediction Section (PredictionSection.vue)
**File Upload:** Prediction dataset (CSV)  
**Chart Output:** Actual vs Predicted workforce demand with confidence intervals

```json
{
  "prediction_result": {
    "upload_file": "multipart/form-data",
    "chart_data": {
      "labels": ["00:00", "04:00", "08:00", "12:00", "16:00", "20:00", "00:00"],
      "datasets": [
        {
          "label": "Upper Bound",
          "data": [150, 180, 200, 190, 170, 160, 140]
        },
        {
          "label": "Lower Bound",
          "data": [100, 120, 140, 130, 110, 100, 80]
        },
        {
          "label": "Actual Workforce",
          "data": [125, 150, 170, 160, 140, 130, 110]
        },
        {
          "label": "Predicted Workforce",
          "data": [128, 155, 168, 162, 142, 132, 112]
        }
      ]
    }
  }
}
```

#### 3.5 Training History Table (TrainingHistory.vue)
```json
{
  "training_history": [
    {
      "date": "2026-07-25 14:32",
      "dataset": "warehouse_q2_2026.csv",
      "algorithms_used": ["XGBoost", "LSTM", "Random Forest"],
      "best_model": "XGBoost",
      "rmse": 3.76,
      "r2": 0.94,
      "status": "success"
    },
    // ... repeat for all training runs
  ]
}
```

### Data Status
- **Status:** 100% Hardcoded mock data
- **Actual Integration:** None - uses local state refs only
- **Missing:** No backend API endpoints for training, prediction, or model management

---

## 4. Shift Optimization

**Route:** `/shiftoptimization`  
**Component:** `shiftOptimizationComponent.vue`  
**Purpose:** AI-powered shift scheduling and resource allocation

### KPI Cards
```
• Total Shifts: 847
• Optimized: 623 (73.5%)
• Pending: 224 (26.5%)
• Projected Savings: $47.2K
```

### Recommendations Component
**AI Recommendations** - Transfer recommendations with priority levels (High/Medium/Low)

```json
{
  "recommendations": [
    {
      "id": 1,
      "title": "Suggested Shift Reallocation",
      "priority": "high",
      "workers_affected": 5,
      "from_department": "Inbound Morning",
      "to_department": "Outbound Afternoon"
    },
    // ... repeat for all recommendations
  ]
}
```

### Coverage Matrix
```json
{
  "coverage_data": [
    {
      "department": "Inbound",
      "morning_current": 37,
      "morning_required": 37,
      "afternoon_current": 42,
      "afternoon_required": 52,
      "night_current": 10,
      "night_required": 21,
      "status": "Gap"
    },
    // ... repeat for all departments
  ]
}
```

### Charts
- **Bar Chart:** Staffing by department & shift (current vs optimal)
- **Recommendations Panel:** AI suggestions with apply/dismiss actions

### Data Status
- **Status:** 100% Hardcoded
- **API Needed:** `POST /optimize-shifts`

---

## 5. Employees

**Route:** `/employees`  
**Component:** `employeesComponent.vue`  
**Purpose:** Employee directory with utilization and attendance tracking

### KPI Cards
```
• Total Active: 1,847
• On Leave: 156
• Avg Utilization: 78.4%
• Avg Attendance: 88.3%
```

### Filters
- **Search:** Employee name/ID
- **Department:** Multi-select (Inbound, Outbound, Packing, Returns, Sortation, Quality Control)
- **Status:** Active/Leave

### Employee Table Columns
```json
{
  "employees": [
    {
      "id": "EMP-1001",
      "name": "James Wilson",
      "department": "Returns",
      "role": "QC Inspector",
      "shift": "Morning (06:00-14:00)",
      "utilization_percent": 84,
      "attendance_percent": 89,
      "status": "Active"
    },
    // ... repeat for all employees
  ]
}
```

### Data Status
- **Status:** 100% Hardcoded (30+ employee records)
- **API Needed:** `GET /employees?department=&status=&search=`

---

## 6. Analytics

**Route:** `/analytics`  
**Component:** `analyticsComponent.vue`  
**Purpose:** Operational analytics and performance metrics

### KPI Cards
```
• Avg Throughput: 4,280/hr
• Productivity Index: 89.4%
• Workforce Efficiency: 91.2%
• Avg Shift Duration: 2,847 min
```

### Tab Views
1. **Performance** - Line chart of productivity trends
2. **Throughput** - Line chart of processing throughput
3. **Distribution** - Doughnut chart (workforce by department) + Bar chart (weekly demand comparison)

### Charts Required
```json
{
  "charts": {
    "productivity": {
      "type": "line",
      "labels": ["Week 1", "Week 2", ...],
      "data": [/* productivity values */]
    },
    "throughput": {
      "type": "line",
      "labels": ["Day 1", "Day 2", ...],
      "data": [/* throughput values */]
    },
    "workforce_distribution": {
      "type": "doughnut",
      "labels": ["Inbound", "Outbound", "Sortation", "Packing", "Returns", "Quality Control"],
      "data": [18, 22, 14, 20, 16, 10]
    },
    "weekly_demand_comparison": {
      "type": "bar",
      "labels": ["Demand", "Staffed", "Optimal"],
      "data": [/* values for each day */]
    }
  }
}
```

### Data Status
- **Status:** 100% Hardcoded
- **API Needed:** `GET /analytics?date_range=&department=`

---

## 7. Capacity Planning

**Route:** `/capacity-planning`  
**Component:** `capacityPlanningComponent.vue`  
**Purpose:** Resource optimization and scalability analysis

### KPI Cards
```
• Capacity Load: 78.4% (↑ 1.2%)
• Peak Utilization: 92.1% (↓ 1.5%)
• Available Headroom: 21.6% (↑ 3.1%)
• Scaling Events: 4 (↓ 20%)
```

### Department Capacity List
```json
{
  "departments": [
    {
      "name": "Inbound",
      "utilization_percent": 94,
      "status": "Critical"
    },
    {
      "name": "Outbound",
      "utilization_percent": 93,
      "status": "Critical"
    },
    // ... repeat for all departments
  ]
}
```

### Charts Required
1. **Capacity Utilization Trend (Line)** - 14-day rolling data
2. **Scalability Benchmark (Bar)** - Response time under concurrent user load

### Data Status
- **Status:** 100% Hardcoded
- **API Needed:** `GET /capacity-planning?days=14`

---

## 8. Monitoring

**Route:** `/monitoring`  
**Component:** `monitorComponent.vue`  
**Purpose:** Infrastructure and system health monitoring

### KPI Cards
```
• System Uptime: 99.97%
• Avg CPU Usage: 34%
• Memory Usage: 58%
• Avg Latency: 42ms
```

### Charts Required
1. **Infrastructure Metrics (Line)** - CPU, Memory, Network over time
2. **API Response Latency (Line)** - Endpoint latencies in milliseconds
3. **Microservice Health Matrix (Cards)** - Status for 8 services

### Microservice Data
```json
{
  "services": [
    {
      "name": "forecast-service",
      "cpu_percent": 34,
      "memory_percent": 62,
      "instances": 3,
      "uptime_percent": 99.97,
      "status": "healthy"
    },
    // ... repeat for all 8 services
  ]
}
```

### Data Status
- **Status:** 100% Hardcoded
- **API Needed:** `GET /monitoring/metrics?range=last_30_minutes`

---

## 9. Benchmarks

**Route:** `/benchmarks`  
**Component:** `benchmarkComponent.vue`  
**Purpose:** Performance testing and A/B experimentation

### KPI Cards
```
• Total Experiments: 47 (↑ 8.2%)
• Avg P95 Latency: 62ms (↓ 15.3%)
• Max Throughput: 4,850/s (↑ 12.5%)
• CPU Under Load: 67% (↑ 3.2%)
```

### Experiment Log
```json
{
  "experiments": [
    {
      "id": "EXP-001",
      "title": "LSTM vs XGBoost Accuracy",
      "result": "LSTM +2.4% accuracy",
      "status": "completed",
      "date": "2026-01-15",
      "duration": "4h 23m"
    },
    // ... repeat for all experiments
  ]
}
```

### Charts Required
1. **API Latency Under Load (Line)** - P50, P95, P99 percentiles
2. **Version Performance History (Bar)** - Throughput across versions
3. **Experiment Log** - List of completed and running tests

### Data Status
- **Status:** 100% Hardcoded
- **API Needed:** `GET /benchmarks/experiments?limit=100`

---

## 10. Reports

**Route:** `/reports`  
**Component:** `reportComponent.vue`  
**Purpose:** Generated reports and exports

### KPI Cards
```
• Total Reports: 8
• Ready for Download: 7
• Generating: 1
• Total Size: 24.8 MB
```

### Reports Table
```json
{
  "reports": [
    {
      "id": "RPT-001",
      "name": "Weekly Workforce Demand Report",
      "type": "Forecasting",
      "generated": "2026-01-15 09:00",
      "size": "2.4 MB",
      "status": "Ready"
    },
    // ... repeat for all reports
  ]
}
```

### Data Status
- **Status:** 100% Hardcoded
- **API Needed:** 
  - `GET /reports` - List reports
  - `POST /reports/generate` - Generate new report
  - `GET /reports/{id}/download` - Download report

---

## 11. Notifications

**Route:** `/notifications`  
**Component:** `notificationComponent.vue`  
**Purpose:** System alerts and notifications

### Tab Filters
- All, Unread, Critical, Warnings, Info

### Notification Fields
```json
{
  "notifications": [
    {
      "title": "Staffing Shortage Alert",
      "message": "Outbound department understaffed by 12 workers for afternoon shift",
      "type": "critical",
      "unread": true,
      "time": "2 min ago",
      "icon": "pi pi-exclamation-triangle"
    },
    // ... repeat for all notifications
  ]
}
```

### Data Status
- **Status:** 100% Hardcoded
- **API Needed:** 
  - `GET /notifications?unread=true` - Get notifications
  - `POST /notifications/{id}/read` - Mark as read
  - `POST /notifications/read-all` - Mark all as read

---

## 12. Settings

**Route:** `/settings`  
**Component:** `setttingsComponent.vue`  
**Purpose:** System configuration and user preferences

### Settings Sections

#### Profile Settings
```json
{
  "profile": {
    "full_name": "Operations Admin",
    "email": "admin@workforceai.com",
    "role": "Super Admin",
    "department": "All Departments"
  }
}
```

#### Appearance Settings
```json
{
  "appearance": {
    "dark_mode": true,
    "compact_view": false,
    "animations": true
  }
}
```

#### Notification Preferences
```json
{
  "notifications": {
    "critical_alerts": true,
    "shift_recommendations": true,
    "system_monitoring": true,
    "email_digest": false
  }
}
```

#### System Configuration
```json
{
  "config": {
    "forecast_model": "LSTM v3.2 (Active)",
    "refresh_interval": "30 seconds",
    "api_gateway_url": "https://api.workforceai.internal",
    "ml_service_url": "https://ml.workforceai.internal"
  }
}
```

### Data Status
- **Status:** 100% Hardcoded (local state)
- **API Needed:** 
  - `PUT /settings/profile` - Update profile
  - `PUT /settings/appearance` - Update appearance
  - `PUT /settings/notifications` - Update notification prefs
  - `PUT /settings/config` - Update system config

---

---

## PART 2: UNIFIED API RESPONSE SCHEMA

Below is the complete response schema that aggregates all prediction, operational, and system data:

```json
{
  "metadata": {
    "timestamp": "2026-07-28T14:30:00Z",
    "request_id": "REQ-12345",
    "version": "1.0",
    "status": "success",
    "message": "Data retrieved successfully"
  },
  "dashboard": {
    "metrics": {
      "assigned_employees": 1847,
      "available_employees": 287,
      "absence_rate": 8.2,
      "orders_processed_percent": 89.1,
      "avg_productivity_percent": 78.4,
      "capacity_load_percent": 99.97,
      "api_uptime_percent": 100.0,
      "changes": {
        "orders_processed": 2.1,
        "productivity": 0.0,
        "capacity": 0.02,
        "uptime": 0.0
      }
    },
    "workforce_chart": {
      "type": "line",
      "labels": ["January", "February", "March", "April", "May", "June", "July"],
      "datasets": [
        {
          "label": "Dataset 1",
          "data": [65, 59, 80, 81, 56, 55, 10]
        },
        {
          "label": "Dataset 2",
          "data": [72, 48, 79, 70, 65, 50, 20]
        }
      ]
    },
    "department_performance_chart": {
      "type": "bar",
      "labels": ["Inbound", "Outbound", "Sortation", "Packing", "Returns", "Quality Control"],
      "datasets": [
        {
          "label": "Staffing",
          "data": [81, 84, 114, 119, 84, 113]
        }
      ]
    },
    "staffing_by_shift": [
      {
        "department": "Inbound",
        "morning": 22,
        "afternoon": 40,
        "night": 19,
        "total": 81
      },
      {
        "department": "Outbound",
        "morning": 33,
        "afternoon": 31,
        "night": 20,
        "total": 84
      }
    ],
    "microservice_health": [
      {
        "name": "forecast-service",
        "instances": 3,
        "cpu_percent": 34,
        "memory_percent": 62,
        "status": "healthy"
      }
    ],
    "alerts": [
      {
        "severity": "critical",
        "title": "Outbound department understaffed by 12 workers for afternoon shift",
        "time": "2 min ago",
        "department": "Outbound",
        "icon": "pi pi-users"
      }
    ]
  },
  "forecasting": {
    "active_model": {
      "name": "LSTM v3.2",
      "status": "Active",
      "accuracy_mape_percent": 94.2,
      "rmse_score": 3.8,
      "predictions_today": 2847
    },
    "hourly_prediction": {
      "type": "line",
      "labels": ["00:00", "01:00", "02:00"],
      "datasets": [
        {
          "label": "Predicted",
          "data": [150, 160, 155]
        },
        {
          "label": "Actual",
          "data": [148, 162, 158]
        }
      ]
    },
    "weekly_performance_chart": {
      "type": "bar",
      "labels": ["Mon", "Tue", "Wed", "Thu", "Fri", "Sat", "Sun"],
      "datasets": []
    },
    "department_staffing": [
      {
        "department": "Inbound",
        "morning": 22,
        "afternoon": 40,
        "night": 19,
        "total": 81
      }
    ]
  },
  "ai_models": {
    "model_stats": {
      "best_model": "XGBoost",
      "r2_score": 0.94,
      "rmse": 3.76,
      "training_status": "Ready"
    },
    "trained_models": [
      {
        "name": "XGBoost",
        "rmse": 3.76,
        "mae": 2.78,
        "mape_percent": 2.37,
        "r2": 0.94,
        "training_time": "2m 14s",
        "status": "Best"
      }
    ],
    "training_history": [
      {
        "date": "2026-07-25 14:32",
        "dataset": "warehouse_q2_2026.csv",
        "algorithms_used": ["XGBoost", "LSTM", "Random Forest"],
        "best_model": "XGBoost",
        "rmse": 3.76,
        "r2": 0.94,
        "status": "success"
      }
    ]
  },
  "shift_optimization": {
    "recommendations": [
      {
        "id": 1,
        "title": "Suggested Shift Reallocation",
        "priority": "high",
        "workers_affected": 5,
        "from_department": "Inbound Morning",
        "to_department": "Outbound Afternoon"
      }
    ],
    "coverage_matrix": [
      {
        "department": "Inbound",
        "morning_current": 37,
        "morning_required": 37,
        "afternoon_current": 42,
        "afternoon_required": 52,
        "night_current": 10,
        "night_required": 21,
        "status": "Gap"
      }
    ]
  },
  "employees": [
    {
      "id": "EMP-1001",
      "name": "James Wilson",
      "department": "Returns",
      "role": "QC Inspector",
      "shift": "Morning (06:00-14:00)",
      "utilization_percent": 84,
      "attendance_percent": 89,
      "status": "Active"
    }
  ],
  "analytics": {
    "productivity_chart": {
      "type": "line",
      "labels": [],
      "datasets": []
    },
    "throughput_chart": {
      "type": "line",
      "labels": [],
      "datasets": []
    },
    "workforce_distribution_chart": {
      "type": "doughnut",
      "labels": ["Inbound", "Outbound", "Sortation", "Packing", "Returns", "Quality Control"],
      "data": [18, 22, 14, 20, 16, 10]
    }
  },
  "capacity_planning": {
    "metrics": {
      "capacity_load_percent": 78.4,
      "capacity_load_change": 1.2,
      "peak_utilization_percent": 92.1,
      "peak_utilization_change": -1.5,
      "available_headroom_percent": 21.6,
      "available_headroom_change": 3.1,
      "scaling_events": 4,
      "scaling_events_change": -20
    },
    "department_capacity": [
      {
        "name": "Inbound",
        "utilization_percent": 94,
        "status": "Critical"
      }
    ],
    "utilization_trend_chart": {
      "type": "line",
      "labels": ["Day 1", "Day 2"],
      "datasets": []
    }
  },
  "monitoring": {
    "infrastructure_metrics": {
      "system_uptime_percent": 99.97,
      "avg_cpu_percent": 34,
      "memory_usage_percent": 58,
      "avg_latency_ms": 42
    },
    "infrastructure_chart": {
      "type": "line",
      "labels": [],
      "datasets": []
    },
    "latency_chart": {
      "type": "line",
      "labels": [],
      "datasets": []
    },
    "microservices": [
      {
        "name": "forecast-service",
        "cpu_percent": 34,
        "memory_percent": 62,
        "instances": 3,
        "uptime_percent": 99.97,
        "status": "healthy"
      }
    ]
  },
  "benchmarks": {
    "metrics": {
      "total_experiments": 47,
      "total_experiments_change": 8.2,
      "avg_p95_latency_ms": 62,
      "avg_p95_latency_change": -15.3,
      "max_throughput_per_sec": 4850,
      "max_throughput_change": 12.5,
      "cpu_under_load_percent": 67,
      "cpu_under_load_change": 3.2
    },
    "latency_chart": {
      "type": "line",
      "labels": ["50", "100", "200", "500", "1000", "2000", "3000", "5000"],
      "datasets": []
    },
    "version_history_chart": {
      "type": "bar",
      "labels": ["v1.0", "v1.5", "v2.0", "v2.5", "v3.0", "v3.2"],
      "datasets": []
    },
    "experiment_log": [
      {
        "id": "EXP-001",
        "title": "LSTM vs XGBoost Accuracy",
        "result": "LSTM +2.4% accuracy",
        "status": "completed",
        "date": "2026-01-15",
        "duration": "4h 23m"
      }
    ]
  },
  "reports": [
    {
      "id": "RPT-001",
      "name": "Weekly Workforce Demand Report",
      "type": "Forecasting",
      "generated": "2026-01-15 09:00",
      "size": "2.4 MB",
      "status": "Ready"
    }
  ],
  "notifications": [
    {
      "id": "NOTIF-001",
      "title": "Staffing Shortage Alert",
      "message": "Outbound department understaffed by 12 workers for afternoon shift",
      "type": "critical",
      "unread": true,
      "timestamp": "2 min ago",
      "icon": "pi pi-exclamation-triangle"
    }
  ],
  "settings": {
    "profile": {
      "full_name": "Operations Admin",
      "email": "admin@workforceai.com",
      "role": "Super Admin",
      "department": "All Departments"
    },
    "appearance": {
      "dark_mode": true,
      "compact_view": false,
      "animations": true
    },
    "notification_preferences": {
      "critical_alerts": true,
      "shift_recommendations": true,
      "system_monitoring": true,
      "email_digest": false
    },
    "system_config": {
      "forecast_model": "LSTM v3.2",
      "refresh_interval": "30 seconds",
      "api_gateway_url": "https://api.workforceai.internal",
      "ml_service_url": "https://ml.workforceai.internal"
    }
  }
}
```

---

---

## PART 3: BACKEND ARCHITECTURE RECOMMENDATIONS

### Microservice Decomposition

**Based on the UI requirements, the backend should consist of:**

#### 1. **FastAPI Prediction Service** (Existing)
- Endpoint: `POST /predict`
- Purpose: ML model predictions
- **Handles:** AI Models module training/prediction, Forecasting predictions
- **Returns:** Prediction-specific fields only

**Enhanced Schema for Prediction:**
```json
{
  "request": {
    "file": "multipart/form-data",
    "algorithm": "LSTM",
    "date_range": "2026-07-01 to 2026-07-28"
  },
  "response": {
    "prediction_id": "PRED-12345",
    "timestamp": "2026-07-28T14:30:00Z",
    "model": "LSTM v3.2",
    "predictions": [
      {
        "timestamp": "2026-07-28T15:00:00Z",
        "predicted_demand": 150,
        "confidence_interval_lower": 140,
        "confidence_interval_upper": 160,
        "predicted_workforce": 128,
        "actual_workforce": 125
      }
    ],
    "metrics": {
      "rmse": 3.76,
      "mae": 2.78,
      "mape": 2.37,
      "r2": 0.94
    }
  }
}
```

#### 2. **Spring Boot REST API** (To be created)
- Purpose: Business logic, operational data, system management
- **Endpoints:**

```
# Dashboard
GET  /api/dashboard/metrics
GET  /api/dashboard/alerts
GET  /api/dashboard/staffing
GET  /api/dashboard/microservices

# Forecasting
GET  /api/forecasting/active-model
GET  /api/forecasting/metrics
GET  /api/forecasting/chart-data

# AI Models
GET  /api/models
POST /api/models/train
GET  /api/models/{id}
GET  /api/models/history

# Shift Optimization
GET  /api/shifts/recommendations
POST /api/shifts/optimize
GET  /api/shifts/coverage
POST /api/shifts/apply-recommendation

# Employees
GET  /api/employees?department=&status=&search=
GET  /api/employees/{id}
PUT  /api/employees/{id}
POST /api/employees

# Analytics
GET  /api/analytics/productivity
GET  /api/analytics/throughput
GET  /api/analytics/distribution

# Capacity Planning
GET  /api/capacity/metrics
GET  /api/capacity/departments
GET  /api/capacity/trend

# Monitoring
GET  /api/monitoring/infrastructure
GET  /api/monitoring/latency
GET  /api/monitoring/microservices

# Benchmarks
GET  /api/benchmarks/metrics
GET  /api/benchmarks/experiments
POST /api/benchmarks/run-test

# Reports
GET  /api/reports
POST /api/reports/generate
GET  /api/reports/{id}/download

# Notifications
GET  /api/notifications?unread=
POST /api/notifications/{id}/read
POST /api/notifications/read-all

# Settings
GET  /api/settings/profile
PUT  /api/settings/profile
PUT  /api/settings/appearance
PUT  /api/settings/notifications
PUT  /api/settings/config
```

#### 3. **Infrastructure Monitoring Service** (External)
- Purpose: System health, latency, CPU/memory metrics
- Tools: Prometheus, ELK Stack, Datadog, or similar

#### 4. **Database Schema** (PostgreSQL recommended)

**Key Tables:**
- `employees` - Employee master data
- `departments` - Department definitions
- `shifts` - Shift definitions and allocations
- `training_runs` - ML model training history
- `predictions` - Historical predictions
- `alerts` - System alerts and notifications
- `reports` - Generated reports metadata
- `settings` - User and system settings
- `notifications` - Notification records
- `benchmarks` - Performance test results

---

## PART 4: DATA SEPARATION & FLOW DIAGRAM

```
┌─────────────────────────────────────────────────────────────────┐
│                     Vue 3 Frontend                              │
│                   (Workforce Forecasting)                       │
└──────────┬──────────────────────────────────────────────────────┘
           │
     ┌─────┴────────┬──────────────┬──────────────┐
     │              │              │              │
     ▼              ▼              ▼              ▼
┌─────────────┐ ┌──────────────┐ ┌────────────┐ ┌──────────────┐
│ Dashboard   │ │Forecasting   │ │AI Models   │ │Shift Opt.    │
│Employees    │ │Analytics     │ │Training    │ │Benchmarks    │
│Capacity     │ │Monitoring    │ │Prediction  │ │Infrastructure
│Reports      │ │Settings      │ │Comparison  │ │Monitoring    │
│Notifications│ │              │ │History     │ │              │
└─────────────┘ └──────────────┘ └────────────┘ └──────────────┘
     │                │                  │              │
     │                │                  │              │
     ▼                ▼                  ▼              ▼
┌───────────────────────────────────────────────────────────────┐
│          Backend Service Layer                               │
├───────────────────────────────────────────────────────────────┤
│                                                               │
│  ┌─────────────────────────────┬────────────────────────┐   │
│  │ Spring Boot REST API        │ FastAPI Prediction     │   │
│  │ (Port 8080)                 │ (Port 8000)            │   │
│  │                             │                        │   │
│  │ • Dashboard metrics         │ • Model training       │   │
│  │ • Employees CRUD            │ • Forecasting          │   │
│  │ • Shift management          │ • Predictions          │   │
│  │ • Capacity planning         │ • Model comparison     │   │
│  │ • Analytics aggregation     │ • Metrics (RMSE, MAE)  │   │
│  │ • Reports generation        │                        │   │
│  │ • Settings storage          │                        │   │
│  │ • Notifications             │                        │   │
│  │ • Benchmarks coordination   │                        │   │
│  │                             │                        │   │
│  └──────────────┬──────────────┴────────────┬───────────┘   │
│                 │                          │                 │
└─────────────────┼──────────────────────────┼─────────────────┘
                  │                          │
     ┌────────────▼──────────────┐ ┌────────▼─────────────┐
     │   PostgreSQL Database     │ │ ML Models (pickle,  │
     │                           │ │ joblib, h5)         │
     │ • employees               │ │                     │
     │ • departments             │ │ • LSTM              │
     │ • shifts                  │ │ • XGBoost           │
     │ • training_runs           │ │ • Random Forest     │
     │ • predictions_history     │ │ • Linear Regression │
     │ • alerts                  │ │                     │
     │ • reports                 │ │ (Versioned with    │
     │ • notifications           │ │  metadata)          │
     │ • settings                │ │                     │
     │ • benchmarks              │ │                     │
     │                           │ │                     │
     └───────────────────────────┘ └─────────────────────┘
```

---

## PART 5: CURRENT STATE VS REQUIRED STATE

| Module | Current Status | Required API | Data Source |
|--------|---|---|---|
| Dashboard | ✅ UI Complete | GET `/api/dashboard/*` | Spring Boot + FastAPI |
| Forecasting | ✅ UI Complete | GET `/api/forecasting/*` | FastAPI (prediction) |
| AI Models | ✅ UI Complete | POST/GET `/api/models/*` | FastAPI (training) |
| Shift Optimization | ✅ UI Complete | POST/GET `/api/shifts/*` | Spring Boot (optimization algo) |
| Employees | ✅ UI Complete | GET/POST/PUT `/api/employees` | Spring Boot + Database |
| Analytics | ✅ UI Complete | GET `/api/analytics/*` | Spring Boot (aggregation) |
| Capacity Planning | ✅ UI Complete | GET `/api/capacity/*` | Spring Boot + FastAPI |
| Monitoring | ✅ UI Complete | GET `/api/monitoring/*` | Prometheus/Monitoring Stack |
| Benchmarks | ✅ UI Complete | GET/POST `/api/benchmarks/*` | Spring Boot |
| Reports | ✅ UI Complete | GET/POST `/api/reports` | Spring Boot (generation) |
| Notifications | ✅ UI Complete | GET/POST `/api/notifications` | Spring Boot + WebSocket |
| Settings | ✅ UI Complete | GET/PUT `/api/settings` | Spring Boot + Database |

---

## PART 6: HARDCODED DATA SUMMARY

**Total Hardcoded Records:**
- Employees: 30+ records
- Departments: 6 departments
- Shifts: 3 shifts (morning/afternoon/night)
- Microservices: 8 services
- Alerts: 5-10 per dashboard
- Notifications: 8+ notifications
- Reports: 8 report records
- Experiments: 5 benchmark experiments
- Training runs: 4 training history records
- Recommendations: 5+ shift recommendations

**All data is stored in component `ref()` state with no persistence or backend integration.**

---

## PART 7: IMPLEMENTATION ROADMAP

### Phase 1: Backend Setup (Week 1-2)
1. Set up Spring Boot project with database
2. Create PostgreSQL schema
3. Implement base REST API endpoints
4. Enhance FastAPI with better response schema

### Phase 2: Integration (Week 3-4)
1. Replace hardcoded data with API calls
2. Implement axios/fetch service layer in Vue
3. Add error handling and loading states
4. Implement WebSocket for real-time notifications

### Phase 3: Data Flow (Week 5-6)
1. Separate prediction-specific data from operational data
2. Implement data aggregation layer in Spring Boot
3. Add caching for frequently accessed data
4. Implement pagination for large datasets

### Phase 4: Production Hardening (Week 7-8)
1. Add authentication/authorization
2. Implement rate limiting
3. Add comprehensive error logging
4. Performance optimization and testing

---

## Conclusion

This comprehensive analysis identifies all UI requirements, data structures, and API needs for the Workforce Forecasting application. The current frontend is **100% feature-complete but requires backend API integration** to function with real data.

**Key Recommendation:** Implement the separated backend architecture (Spring Boot + FastAPI) following the specifications in this document to enable data persistence, real-time updates, and production-ready functionality.

---

**Report Generated By:** Frontend Analysis Agent  
**Analysis Date:** 2026-07-28  
**Compliance Status:** Complete  
