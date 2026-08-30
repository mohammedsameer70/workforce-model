import pytest
import numpy as np
import pandas as pd
import os
import sys
import tempfile
import shutil
from unittest.mock import Mock, patch, MagicMock

# Add parent directory to path to import modules
sys.path.insert(0, os.path.abspath(os.path.join(os.path.dirname(__file__), '..')))

from training.model_trainer import ModelTrainer


@pytest.mark.integration
@pytest.mark.slow
def test_model_trainer_linear_regression_only(sample_data, temp_dir):
    """Test ModelTrainer with only Linear Regression."""
    trainer = ModelTrainer()
    
    # Create a simple LSTM dataframe (will be None for this test)
    lstm_df = None
    
    selected_models = ["Linear Regression"]
    
    # Prepare simple features
    X_train = sample_data[['day_of_week', 'month', 'is_holiday']].values[:80]
    X_test = sample_data[['day_of_week', 'month', 'is_holiday']].values[80:]
    y_train = sample_data['actual_demand'].values[:80]
    y_test = sample_data['actual_demand'].values[80:]
    
    try:
        results, trained_models, X_dashboard, target_scaler = trainer.train_models(
            selected_models, X_train, X_test, y_train, y_test, lstm_df
        )
        
        # Verify results
        assert len(results) == 1
        assert results[0]['Model'] == 'Linear Regression'
        assert 'RMSE' in results[0]
        assert 'MAE' in results[0]
        assert 'MAPE' in results[0]
        assert 'R2' in results[0]
        assert 'Hyperparameters' in results[0]
        
        # Verify trained models
        assert 'Linear Regression' in trained_models
        assert 'model' in trained_models['Linear Regression']
        assert 'hyperparameters' in trained_models['Linear Regression']
        
    except Exception as e:
        pytest.fail(f"ModelTrainer.train_models raised an exception: {e}")


@pytest.mark.integration
@pytest.mark.slow
def test_model_trainer_multiple_models(sample_data, temp_dir):
    """Test ModelTrainer with multiple models."""
    trainer = ModelTrainer()
    
    lstm_df = None
    
    selected_models = ["Linear Regression", "Random Forest"]
    
    # Prepare simple features
    X_train = sample_data[['day_of_week', 'month', 'is_holiday']].values[:80]
    X_test = sample_data[['day_of_week', 'month', 'is_holiday']].values[80:]
    y_train = sample_data['actual_demand'].values[:80]
    y_test = sample_data['actual_demand'].values[80:]
    
    try:
        results, trained_models, X_dashboard, target_scaler = trainer.train_models(
            selected_models, X_train, X_test, y_train, y_test, lstm_df
        )
        
        # Verify results for both models
        assert len(results) == 2
        model_names = [r['Model'] for r in results]
        assert 'Linear Regression' in model_names
        assert 'Random Forest' in model_names
        
        # Verify all results have required fields
        for result in results:
            assert 'RMSE' in result
            assert 'MAE' in result
            assert 'MAPE' in result
            assert 'R2' in result
            assert 'Hyperparameters' in result
        
        # Verify trained models
        assert 'Linear Regression' in trained_models
        assert 'Random Forest' in trained_models
        
        # Verify hyperparameters are stored
        assert 'hyperparameters' in trained_models['Linear Regression']
        assert 'hyperparameters' in trained_models['Random Forest']
        
    except Exception as e:
        pytest.fail(f"ModelTrainer.train_models raised an exception: {e}")


@pytest.mark.integration
@pytest.mark.slow
def test_model_trainer_hyperparameters_persistence(sample_data, temp_dir):
    """Test that hyperparameters are correctly passed through the training pipeline."""
    trainer = ModelTrainer()
    
    lstm_df = None
    
    selected_models = ["Random Forest"]
    
    # Prepare simple features
    X_train = sample_data[['day_of_week', 'month', 'is_holiday']].values[:80]
    X_test = sample_data[['day_of_week', 'month', 'is_holiday']].values[80:]
    y_train = sample_data['actual_demand'].values[:80]
    y_test = sample_data['actual_demand'].values[80:]
    
    try:
        results, trained_models, X_dashboard, target_scaler = trainer.train_models(
            selected_models, X_train, X_test, y_train, y_test, lstm_df
        )
        
        # Verify hyperparameters are in results
        assert 'Hyperparameters' in results[0]
        assert results[0]['Hyperparameters'] is not None
        
        # Verify hyperparameters are in trained_models
        assert 'hyperparameters' in trained_models['Random Forest']
        assert trained_models['Random Forest']['hyperparameters'] is not None
        
        # Verify hyperparameters are a dict
        assert isinstance(trained_models['Random Forest']['hyperparameters'], dict)
        
        # Verify expected hyperparameter keys for Random Forest
        params = trained_models['Random Forest']['hyperparameters']
        assert 'n_estimators' in params
        assert 'max_depth' in params
        
    except Exception as e:
        pytest.fail(f"ModelTrainer.train_models raised an exception: {e}")


@pytest.mark.integration
@pytest.mark.slow
def test_model_trainer_empty_selection(sample_data, temp_dir):
    """Test ModelTrainer with no models selected."""
    trainer = ModelTrainer()
    
    lstm_df = None
    selected_models = []
    
    # Prepare simple features
    X_train = sample_data[['day_of_week', 'month', 'is_holiday']].values[:80]
    X_test = sample_data[['day_of_week', 'month', 'is_holiday']].values[80:]
    y_train = sample_data['actual_demand'].values[:80]
    y_test = sample_data['actual_demand'].values[80:]
    
    try:
        results, trained_models, X_dashboard, target_scaler = trainer.train_models(
            selected_models, X_train, X_test, y_train, y_test, lstm_df
        )
        
        # Should return empty results
        assert len(results) == 0
        assert len(trained_models) == 0
        
    except Exception as e:
        pytest.fail(f"ModelTrainer.train_models raised an exception: {e}")


@pytest.mark.integration
@pytest.mark.slow
def test_end_to_end_training_workflow(sample_data, temp_dir):
    """Test end-to-end workflow from training to results."""
    trainer = ModelTrainer()
    
    lstm_df = None
    selected_models = ["Linear Regression"]
    
    # Prepare simple features
    X_train = sample_data[['day_of_week', 'month', 'is_holiday']].values[:80]
    X_test = sample_data[['day_of_week', 'month', 'is_holiday']].values[80:]
    y_train = sample_data['actual_demand'].values[:80]
    y_test = sample_data['actual_demand'].values[80:]
    
    try:
        results, trained_models, X_dashboard, target_scaler = trainer.train_models(
            selected_models, X_train, X_test, y_train, y_test, lstm_df
        )
        
        # Verify workflow completed
        assert len(results) > 0
        assert 'Linear Regression' in trained_models
        assert 'hyperparameters' in trained_models['Linear Regression']
        
    except Exception as e:
        pytest.fail(f"End-to-end workflow raised an exception: {e}")
