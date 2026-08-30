import pytest
import numpy as np
from sklearn.linear_model import LinearRegression
from sklearn.ensemble import RandomForestRegressor
import sys
import os

# Add parent directory to path to import modules
sys.path.insert(0, os.path.abspath(os.path.join(os.path.dirname(__file__), '..')))

from models.linear_regression_model import train_linear_regression
from models.random_forest_model import train_random_forest
from models.xgboost_model import train_xgboost


@pytest.mark.unit
def test_train_linear_regression(sample_features):
    """Test Linear Regression training."""
    X_train, y_train = sample_features
    
    model, params = train_linear_regression(X_train, y_train)
    
    # Check that model is trained
    assert model is not None
    assert isinstance(model, LinearRegression)
    
    # Check that hyperparameters are returned
    assert params is not None
    assert isinstance(params, dict)
    assert 'fit_intercept' in params
    assert 'copy_X' in params
    assert 'n_jobs' in params
    assert 'positive' in params
    
    # Check model can predict
    predictions = model.predict(X_train)
    assert predictions is not None
    assert len(predictions) == len(y_train)


@pytest.mark.unit
def test_train_random_forest(sample_features):
    """Test Random Forest training."""
    X_train, y_train = sample_features
    
    model, params = train_random_forest(X_train, y_train)
    
    # Check that model is trained
    assert model is not None
    assert isinstance(model, RandomForestRegressor)
    
    # Check that hyperparameters are returned
    assert params is not None
    assert isinstance(params, dict)
    assert 'n_estimators' in params
    assert 'max_depth' in params
    assert 'min_samples_split' in params
    assert 'min_samples_leaf' in params
    assert 'max_features' in params
    
    # Check model can predict
    predictions = model.predict(X_train)
    assert predictions is not None
    assert len(predictions) == len(y_train)


@pytest.mark.unit
def test_train_xgboost(sample_features):
    """Test XGBoost training."""
    X_train, y_train = sample_features
    
    model, params = train_xgboost(X_train, y_train)
    
    # Check that model is trained
    assert model is not None
    assert hasattr(model, 'predict')
    
    # Check that hyperparameters are returned
    assert params is not None
    assert isinstance(params, dict)
    assert 'n_estimators' in params
    assert 'max_depth' in params
    assert 'learning_rate' in params
    
    # Check model can predict
    predictions = model.predict(X_train)
    assert predictions is not None
    assert len(predictions) == len(y_train)


@pytest.mark.unit
def test_linear_regression_params_default(sample_features):
    """Test that Linear Regression returns expected default parameters."""
    X_train, y_train = sample_features
    
    _, params = train_linear_regression(X_train, y_train)
    
    assert params['fit_intercept'] == True
    assert params['copy_X'] == True
    assert params['n_jobs'] is None
    assert params['positive'] == False


@pytest.mark.unit
def test_random_forest_params_in_range(sample_features):
    """Test that Random Forest hyperparameters are within expected ranges."""
    X_train, y_train = sample_features
    
    _, params = train_random_forest(X_train, y_train)
    
    # Check n_estimators is positive
    assert params['n_estimators'] > 0
    
    # Check max_depth is positive or None
    assert params['max_depth'] is None or params['max_depth'] > 0
    
    # Check min_samples_split is at least 2
    assert params['min_samples_split'] >= 2
    
    # Check min_samples_leaf is at least 1
    assert params['min_samples_leaf'] >= 1


@pytest.mark.unit
def test_xgboost_params_in_range(sample_features):
    """Test that XGBoost hyperparameters are within expected ranges."""
    X_train, y_train = sample_features
    
    _, params = train_xgboost(X_train, y_train)
    
    # Check n_estimators is positive
    assert params['n_estimators'] > 0
    
    # Check max_depth is positive
    assert params['max_depth'] > 0
    
    # Check learning_rate is between 0 and 1
    assert 0 < params['learning_rate'] <= 1
    
    # Check subsample is between 0 and 1
    assert 0 < params['subsample'] <= 1
