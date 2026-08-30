import pytest
import numpy as np
import pandas as pd
import os
import tempfile
import shutil
from pathlib import Path


@pytest.fixture
def sample_data():
    """Create sample training data for testing."""
    np.random.seed(42)
    n_samples = 100
    
    data = {
        'attendance_date': pd.date_range(start='2024-01-01', periods=n_samples, freq='D'),
        'department': np.random.choice(['Physiotherapy', 'Occupational Therapy', 'Speech Therapy'], n_samples),
        'actual_demand': np.random.randint(5, 30, n_samples),
        'day_of_week': np.random.randint(0, 7, n_samples),
        'month': np.random.randint(1, 13, n_samples),
        'is_holiday': np.random.choice([0, 1], n_samples, p=[0.9, 0.1]),
    }
    
    df = pd.DataFrame(data)
    return df


@pytest.fixture
def sample_features(sample_data):
    """Create sample feature matrix and target."""
    df = sample_data.copy()
    
    # Simple feature engineering
    features = ['day_of_week', 'month', 'is_holiday']
    X = df[features].values
    y = df['actual_demand'].values
    
    return X, y


@pytest.fixture
def temp_dir():
    """Create a temporary directory for test outputs."""
    temp_dir = tempfile.mkdtemp()
    yield temp_dir
    # Cleanup after test
    shutil.rmtree(temp_dir, ignore_errors=True)


@pytest.fixture
def mock_model_dir(temp_dir):
    """Create a mock saved_models directory."""
    model_dir = os.path.join(temp_dir, 'saved_models')
    os.makedirs(model_dir, exist_ok=True)
    return model_dir


@pytest.fixture
def mock_results_dir(temp_dir):
    """Create a mock results directory."""
    results_dir = os.path.join(temp_dir, 'results')
    os.makedirs(results_dir, exist_ok=True)
    return results_dir
