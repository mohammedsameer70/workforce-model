-- Workforce Management Database Schema
-- This script creates all tables for the workforce management application

-- Dashboard Entities
CREATE TABLE IF NOT EXISTS kpi_card (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    title VARCHAR(255),
    value VARCHAR(255),
    trend VARCHAR(50),
    trend_direction VARCHAR(50),
    unit VARCHAR(50),
    category VARCHAR(100),
    last_updated DATETIME,
    INDEX idx_category (category)
);

CREATE TABLE IF NOT EXISTS alert (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    title VARCHAR(255),
    message TEXT,
    severity VARCHAR(50),
    type VARCHAR(100),
    is_read BOOLEAN DEFAULT FALSE,
    created_at DATETIME,
    resolved_at DATETIME,
    INDEX idx_is_read (is_read),
    INDEX idx_severity (severity),
    INDEX idx_type (type)
);

CREATE TABLE IF NOT EXISTS microservice_health (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    service_name VARCHAR(255) UNIQUE,
    status VARCHAR(50),
    cpu_usage DOUBLE,
    memory_usage DOUBLE,
    instances INT,
    uptime BIGINT,
    version VARCHAR(50),
    last_checked DATETIME,
    INDEX idx_service_name (service_name)
);

CREATE TABLE IF NOT EXISTS workforce_chart (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    timestamp VARCHAR(50),
    workforce_count DOUBLE,
    demand DOUBLE,
    supply DOUBLE,
    department VARCHAR(100),
    created_at DATETIME,
    INDEX idx_department (department)
);

CREATE TABLE IF NOT EXISTS department_chart (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    department VARCHAR(100),
    value DOUBLE,
    metric VARCHAR(100),
    created_at DATETIME,
    INDEX idx_department (department),
    INDEX idx_metric (metric)
);

CREATE TABLE IF NOT EXISTS trend_chart (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    date VARCHAR(50),
    value DOUBLE,
    trend_type VARCHAR(100),
    created_at DATETIME,
    INDEX idx_trend_type (trend_type)
);

CREATE TABLE IF NOT EXISTS staffing_heatmap (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    department VARCHAR(100),
    time_slot VARCHAR(50),
    day_of_week VARCHAR(20),
    staffing_level DOUBLE,
    demand_level DOUBLE,
    status VARCHAR(50),
    created_at DATETIME,
    INDEX idx_department (department)
);

-- Forecasting Entities
CREATE TABLE IF NOT EXISTS hourly_forecast (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    hour VARCHAR(50),
    predicted_demand DOUBLE,
    confidence_interval DOUBLE,
    department VARCHAR(100),
    forecast_date DATETIME,
    created_at DATETIME,
    INDEX idx_department (department)
);

CREATE TABLE IF NOT EXISTS weekly_forecast (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    day_of_week VARCHAR(20),
    predicted_demand DOUBLE,
    actual_demand DOUBLE,
    variance DOUBLE,
    department VARCHAR(100),
    week_start_date DATETIME,
    created_at DATETIME,
    INDEX idx_department (department)
);

CREATE TABLE IF NOT EXISTS radar_chart (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    metric VARCHAR(100),
    value DOUBLE,
    category VARCHAR(100),
    department VARCHAR(100),
    created_at DATETIME,
    INDEX idx_department (department)
);

-- AI Models Entities
CREATE TABLE IF NOT EXISTS dataset (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(255),
    file_name VARCHAR(255),
    file_path VARCHAR(500),
    file_size BIGINT,
    record_count INT,
    status VARCHAR(50),
    description TEXT,
    uploaded_at DATETIME,
    processed_at DATETIME,
    INDEX idx_status (status),
    INDEX idx_name (name)
);

CREATE TABLE IF NOT EXISTS ai_model (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(255),
    algorithm VARCHAR(100),
    version VARCHAR(50),
    status VARCHAR(50),
    rmse DOUBLE,
    mae DOUBLE,
    mape DOUBLE,
    r_squared DOUBLE,
    training_time BIGINT,
    description TEXT,
    created_at DATETIME,
    last_trained DATETIME,
    dataset_id BIGINT,
    FOREIGN KEY (dataset_id) REFERENCES dataset(id),
    INDEX idx_status (status),
    INDEX idx_algorithm (algorithm),
    INDEX idx_name (name)
);

CREATE TABLE IF NOT EXISTS training_progress (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    status VARCHAR(50),
    current_epoch INT,
    total_epochs INT,
    current_loss DOUBLE,
    accuracy DOUBLE,
    message TEXT,
    start_time DATETIME,
    end_time DATETIME,
    model_id BIGINT,
    FOREIGN KEY (model_id) REFERENCES ai_model(id),
    INDEX idx_status (status),
    INDEX idx_model_id (model_id)
);

CREATE TABLE IF NOT EXISTS model_comparison (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    model_name VARCHAR(255),
    algorithm VARCHAR(100),
    rmse DOUBLE,
    mae DOUBLE,
    mape DOUBLE,
    r_squared DOUBLE,
    training_time BIGINT,
    status VARCHAR(50),
    created_at DATETIME
);

CREATE TABLE IF NOT EXISTS data_comparison (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    category VARCHAR(100),
    actual_value DOUBLE,
    predicted_value DOUBLE,
    difference DOUBLE,
    data_source VARCHAR(100),
    created_at DATETIME,
    INDEX idx_data_source (data_source)
);

-- Shift Optimization Entities
CREATE TABLE IF NOT EXISTS shift_staffing (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    shift VARCHAR(50),
    department VARCHAR(100),
    required_staff INT,
    current_staff INT,
    gap INT,
    utilization DOUBLE,
    date DATETIME,
    created_at DATETIME,
    INDEX idx_department (department),
    INDEX idx_shift (shift)
);

CREATE TABLE IF NOT EXISTS ai_recommendation (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    title VARCHAR(255),
    description TEXT,
    type VARCHAR(100),
    priority VARCHAR(50),
    department VARCHAR(100),
    shift VARCHAR(50),
    recommended_staff INT,
    current_staff INT,
    status VARCHAR(50),
    created_at DATETIME,
    applied_at DATETIME,
    INDEX idx_status (status),
    INDEX idx_department (department),
    INDEX idx_priority (priority)
);

CREATE TABLE IF NOT EXISTS shift_coverage_matrix (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    department VARCHAR(100),
    shift VARCHAR(50),
    day_of_week VARCHAR(20),
    coverage_percentage DOUBLE,
    staff_count INT,
    demand_count INT,
    status VARCHAR(50),
    created_at DATETIME,
    INDEX idx_department (department)
);

-- Employee Entities
CREATE TABLE IF NOT EXISTS employee (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(255),
    email VARCHAR(255) UNIQUE,
    department VARCHAR(100),
    role VARCHAR(100),
    shift VARCHAR(50),
    utilization DOUBLE,
    attendance VARCHAR(50),
    status VARCHAR(50),
    join_date DATE,
    last_updated DATETIME,
    INDEX idx_department (department),
    INDEX idx_status (status),
    INDEX idx_shift (shift),
    INDEX idx_email (email)
);

CREATE TABLE IF NOT EXISTS employee_attendance (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    employee_id BIGINT,
    date DATE,
    status VARCHAR(50),
    check_in_time VARCHAR(50),
    check_out_time VARCHAR(50),
    hours_worked DOUBLE,
    created_at DATETIME,
    FOREIGN KEY (employee_id) REFERENCES employee(id),
    INDEX idx_employee_id (employee_id),
    INDEX idx_status (status)
);

-- Analytics Entities
CREATE TABLE IF NOT EXISTS performance_metric (
  id BIGINT AUTO_INCREMENT PRIMARY KEY,
  metric_name VARCHAR(255) NOT NULL,
  value DOUBLE,
  category VARCHAR(100),
  department VARCHAR(100),
  period VARCHAR(60),
  metric_timestamp DATETIME,
  created_at DATETIME DEFAULT CURRENT_TIMESTAMP,

  INDEX idx_category (category),
  INDEX idx_department (department)
);
CREATE TABLE IF NOT EXISTS throughput_metric (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    process_name VARCHAR(255),
    throughput DOUBLE,
    unit VARCHAR(50),
    department VARCHAR(100),
    timestamp DATETIME,
    created_at DATETIME,
    INDEX idx_department (department)
);

CREATE TABLE IF NOT EXISTS distribution_metric (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    category VARCHAR(100),
    value DOUBLE,
    percentage DOUBLE,
    metric_type VARCHAR(100),
    department VARCHAR(100),
    created_at DATETIME,
    INDEX idx_metric_type (metric_type),
    INDEX idx_department (department)
);

CREATE TABLE IF NOT EXISTS department_analytics (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    department VARCHAR(100),
    efficiency DOUBLE,
    productivity DOUBLE,
    utilization DOUBLE,
    quality_score DOUBLE,
    period_start DATETIME,
    period_end DATETIME,
    created_at DATETIME,
    INDEX idx_department (department),
    INDEX idx_department_period (department, period_start)
);

-- Capacity Planning Entities
CREATE TABLE IF NOT EXISTS capacity_utilization (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    department VARCHAR(100),
    utilization_rate DOUBLE,
    available_capacity DOUBLE,
    used_capacity DOUBLE,
    date VARCHAR(50),
    created_at DATETIME,
    INDEX idx_department (department)
);

CREATE TABLE IF NOT EXISTS department_capacity (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    department VARCHAR(100),
    current_capacity DOUBLE,
    maximum_capacity DOUBLE,
    utilization_percentage DOUBLE,
    status VARCHAR(50),
    created_at DATETIME,
    INDEX idx_department (department)
);

CREATE TABLE IF NOT EXISTS benchmark (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    metric_name VARCHAR(255),
    benchmark_value DOUBLE,
    current_value DOUBLE,
    variance DOUBLE,
    department VARCHAR(100),
    category VARCHAR(100),
    created_at DATETIME,
    INDEX idx_department (department),
    INDEX idx_category (category)
);

-- Monitor Entities
CREATE TABLE IF NOT EXISTS service_health (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    service_name VARCHAR(255) UNIQUE,
    status VARCHAR(50),
    cpu_usage DOUBLE,
    memory_usage DOUBLE,
    instances INT,
    uptime BIGINT,
    version VARCHAR(50),
    endpoint VARCHAR(255),
    last_checked DATETIME,
    INDEX idx_service_name (service_name)
);

CREATE TABLE IF NOT EXISTS monitoring_metric (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    metric_name VARCHAR(255),
    value DOUBLE,
    unit VARCHAR(50),
    service_name VARCHAR(255),
    category VARCHAR(100),
    timestamp DATETIME,
    created_at DATETIME,
    INDEX idx_service_name (service_name),
    INDEX idx_category (category)
);

-- Benchmark Entities
CREATE TABLE IF NOT EXISTS latency_metric (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    endpoint VARCHAR(255),
    p50 DOUBLE,
    p95 DOUBLE,
    p99 DOUBLE,
    load_level VARCHAR(50),
    timestamp DATETIME,
    created_at DATETIME,
    INDEX idx_endpoint (endpoint),
    INDEX idx_load_level (load_level)
);

CREATE TABLE IF NOT EXISTS version_performance (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    version VARCHAR(50),
    service_name VARCHAR(255),
    performance_score DOUBLE,
    response_time DOUBLE,
    error_rate DOUBLE,
    status VARCHAR(50),
    created_at DATETIME,
    INDEX idx_service_name (service_name),
    INDEX idx_version (version)
);

CREATE TABLE IF NOT EXISTS experiment_log (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    experiment_name VARCHAR(255),
    description TEXT,
    status VARCHAR(50),
    result DOUBLE,
    metrics TEXT,
    start_time DATETIME,
    end_time DATETIME,
    created_at DATETIME,
    INDEX idx_status (status)
);

-- Reports Entity
CREATE TABLE IF NOT EXISTS report (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(255),
    type VARCHAR(100),
    description TEXT,
    generated_at DATETIME,
    file_size BIGINT,
    file_path VARCHAR(500),
    status VARCHAR(50),
    generated_by VARCHAR(255),
    INDEX idx_type (type),
    INDEX idx_status (status)
);

-- Notifications Entity
CREATE TABLE IF NOT EXISTS notification (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    title VARCHAR(255),
    message TEXT,
    type VARCHAR(100),
    priority VARCHAR(50),
    is_read BOOLEAN DEFAULT FALSE,
    icon VARCHAR(100),
    created_at DATETIME,
    read_at DATETIME,
    INDEX idx_is_read (is_read),
    INDEX idx_type (type),
    INDEX idx_priority (priority)
);

-- Settings Entity
CREATE TABLE IF NOT EXISTS app_settings (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    setting_key VARCHAR(255) UNIQUE,
    setting_value TEXT,
    category VARCHAR(100),
    description TEXT,
    data_type VARCHAR(50),
    last_updated DATETIME,
    INDEX idx_category (category),
    INDEX idx_setting_key (setting_key)
);
