import pytest
import numpy as np
import pandas as pd
import sys
import os

# Add parent directory to path to import modules
sys.path.insert(0, os.path.abspath(os.path.join(os.path.dirname(__file__), '..')))

from evaluation.evaluation import evaluate_model, save_results


@pytest.mark.unit
def test_evaluate_model_basic():
    """Test basic evaluation functionality."""
    y_true = np.array([10, 20, 30, 40, 50])
    predictions = np.array([12, 18, 32, 38, 48])
    
    result = evaluate_model(y_true, predictions, "Test Model")
    
    # Check result structure
    assert isinstance(result, dict)
    assert 'Model' in result
    assert 'RMSE' in result
    assert 'MAE' in result
    assert 'MAPE' in result
    assert 'R2' in result
    
    # Check model name
    assert result['Model'] == "Test Model"
    
    # Check metrics are numeric
    assert isinstance(result['RMSE'], (int, float))
    assert isinstance(result['MAE'], (int, float))
    assert isinstance(result['MAPE'], (int, float))
    assert isinstance(result['R2'], (int, float))


@pytest.mark.unit
def test_evaluate_model_with_hyperparameters():
    """Test evaluation with hyperparameters."""
    y_true = np.array([10, 20, 30, 40, 50])
    predictions = np.array([12, 18, 32, 38, 48])
    hyperparams = {'n_estimators': 100, 'max_depth': 5}
    
    result = evaluate_model(y_true, predictions, "Test Model", hyperparams)
    
    # Check hyperparameters are included
    assert 'Hyperparameters' in result
    assert result['Hyperparameters'] == str(hyperparams)


@pytest.mark.unit
def test_evaluate_model_perfect_predictions():
    """Test evaluation with perfect predictions."""
    y_true = np.array([10, 20, 30, 40, 50])
    predictions = np.array([10, 20, 30, 40, 50])
    
    result = evaluate_model(y_true, predictions, "Perfect Model")
    
    # Perfect predictions should have zero error
    assert result['RMSE'] == 0.0
    assert result['MAE'] == 0.0
    assert result['MAPE'] == 0.0
    assert result['R2'] == 1.0


@pytest.mark.unit
def test_evaluate_model_metrics_range():
    """Test that metrics are within reasonable ranges."""
    y_true = np.array([10, 20, 30, 40, 50])
    predictions = np.array([15, 25, 35, 45, 55])
    
    result = evaluate_model(y_true, predictions, "Test Model")
    
    # RMSE should be positive
    assert result['RMSE'] >= 0
    
    # MAE should be positive
    assert result['MAE'] >= 0
    
    # MAPE should be positive
    assert result['MAPE'] >= 0
    
    # R2 should be between -inf and 1
    assert result['R2'] <= 1.0


@pytest.mark.unit
def test_evaluate_model_handles_arrays():
    """Test that evaluation handles both numpy arrays and lists."""
    y_true_list = [10, 20, 30, 40, 50]
    predictions_list = [12, 18, 32, 38, 48]
    y_true_array = np.array(y_true_list)
    predictions_array = np.array(predictions_list)
    
    result_list = evaluate_model(y_true_list, predictions_list, "Test Model")
    result_array = evaluate_model(y_true_array, predictions_array, "Test Model")
    
    # Results should be similar
    assert result_list['RMSE'] == pytest.approx(result_array['RMSE'])
    assert result_list['MAE'] == pytest.approx(result_array['MAE'])


@pytest.mark.unit
def test_save_results(mock_results_dir, monkeypatch):
    """Test saving results to CSV."""
    # Mock the results directory to use the fixture
    monkeypatch.setattr('evaluation.evaluation.os.makedirs', lambda x, exist_ok: True)
    
    # Mock the CSV file path to use the temp directory
    def mock_to_csv(self, path, **kwargs):
        # Don't actually write to disk
        pass
    
    monkeypatch.setattr('pandas.DataFrame.to_csv', mock_to_csv)
    
    results = [
        {
            'Model': 'Linear Regression',
            'RMSE': 2.5,
            'MAE': 2.0,
            'MAPE': 5.5,
            'R2': 0.95,
            'Hyperparameters': "{'fit_intercept': True}"
        },
        {
            'Model': 'Random Forest',
            'RMSE': 2.0,
            'MAE': 1.5,
            'MAPE': 4.0,
            'R2': 0.97,
            'Hyperparameters': "{'n_estimators': 100}"
        }
    ]
    
    try:
        save_results(results)
        assert True
    except Exception as e:
        pytest.fail(f"save_results raised an exception: {e}")


@pytest.mark.unit
def test_evaluate_model_empty_hyperparameters():
    """Test evaluation with empty hyperparameters."""
    y_true = np.array([10, 20, 30, 40, 50])
    predictions = np.array([12, 18, 32, 38, 48])
    
    result = evaluate_model(y_true, predictions, "Test Model", None)
    
    # Should not include Hyperparameters field when None
    assert 'Hyperparameters' not in result or result['Hyperparameters'] is None


@pytest.mark.unit
def test_evaluate_model_complex_hyperparameters():
    """Test evaluation with complex nested hyperparameters."""
    y_true = np.array([10, 20, 30, 40, 50])
    predictions = np.array([12, 18, 32, 38, 48])
    hyperparams = {
        'n_estimators': 100,
        'max_depth': 5,
        'learning_rate': 0.1,
        'subsample': 0.8,
        'nested': {'param1': 1, 'param2': 2}
    }
    
    result = evaluate_model(y_true, predictions, "Test Model", hyperparams)
    
    # Check hyperparameters are included as string
    assert 'Hyperparameters' in result
    assert isinstance(result['Hyperparameters'], str)
