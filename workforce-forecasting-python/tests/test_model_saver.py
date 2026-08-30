import pytest
import numpy as np
import pandas as pd
import json
import os
import sys
from unittest.mock import Mock, patch, MagicMock

# Add parent directory to path to import modules
sys.path.insert(0, os.path.abspath(os.path.join(os.path.dirname(__file__), '..')))

from training.model_saver import ModelSaver


@pytest.mark.unit
def test_model_saver_initialization():
    """Test ModelSaver initialization."""
    saver = ModelSaver()
    assert saver is not None


@pytest.mark.unit
def test_convert_numpy():
    """Test numpy to native type conversion."""
    saver = ModelSaver()
    
    # Test with numpy int
    data = {'value': np.int64(42)}
    result = saver.convert_numpy(data)
    assert isinstance(result['value'], int)
    
    # Test with numpy float
    data = {'value': np.float64(3.14)}
    result = saver.convert_numpy(data)
    assert isinstance(result['value'], float)
    
    # Test with nested dict
    data = {'nested': {'value': np.int64(42)}}
    result = saver.convert_numpy(data)
    assert isinstance(result['nested']['value'], int)


@pytest.mark.unit
def test_convert_numpy_with_list():
    """Test numpy to native type conversion with lists."""
    saver = ModelSaver()
    
    # Test with numpy array in list
    data = {'values': [np.int64(1), np.int64(2), np.int64(3)]}
    result = saver.convert_numpy(data)
    assert all(isinstance(v, int) for v in result['values'])


@pytest.mark.unit
def test_save_method(mock_model_dir, monkeypatch):
    """Test the save method that saves model, features, and info."""
    saver = ModelSaver()
    
    # Mock the model
    mock_model = Mock()
    mock_model.predict = Mock(return_value=np.array([1, 2, 3]))
    
    best_model = {
        'Model': 'Random Forest',
        'RMSE': 2.5,
        'MAE': 2.0,
        'MAPE': 5.5,
        'R2': 0.95
    }
    
    trained_models = {
        'Random Forest': {
            'model': mock_model,
            'hyperparameters': {'n_estimators': 100, 'max_depth': 5}
        }
    }
    
    feature_columns = ['feature1', 'feature2', 'feature3']
    
    # Mock joblib.dump
    mock_dump = Mock()
    monkeypatch.setattr('joblib.dump', mock_dump)
    
    # Mock json.dump
    mock_json_dump = Mock()
    monkeypatch.setattr('json.dump', mock_json_dump)
    
    # Mock open
    mock_open = MagicMock()
    mock_open.return_value.__enter__ = Mock()
    mock_open.return_value.__exit__ = Mock()
    mock_open.return_value.write = Mock()
    monkeypatch.setattr('builtins.open', mock_open)
    
    # Mock os.makedirs
    monkeypatch.setattr('os.makedirs', lambda x, exist_ok: True)
    
    # Mock the directory
    monkeypatch.setattr('training.model_saver.os.path.join', lambda *args: '/'.join(args))
    
    try:
        saver.save(best_model, trained_models, feature_columns)
        # Verify dump was called for model
        assert mock_dump.called
        # Verify json.dump was called for feature columns and model info
        assert mock_json_dump.call_count == 2
    except Exception as e:
        pytest.fail(f"save raised an exception: {e}")


@pytest.mark.unit
def test_save_with_lstm_best_model(mock_model_dir, monkeypatch):
    """Test save method when LSTM is selected as best model."""
    saver = ModelSaver()
    
    # Mock the model
    mock_model = Mock()
    mock_model.predict = Mock(return_value=np.array([1, 2, 3]))
    
    best_model = {
        'Model': 'LSTM',
        'RMSE': 2.5,
        'MAE': 2.0,
        'MAPE': 5.5,
        'R2': 0.95
    }
    
    trained_models = {
        'LSTM': {
            'model': mock_model,
            'hyperparameters': {'lstm_units_1': 128}
        },
        'Random Forest': {
            'model': mock_model,
            'hyperparameters': {'n_estimators': 100}
        }
    }
    
    feature_columns = ['feature1', 'feature2', 'feature3']
    
    # Mock joblib.dump
    mock_dump = Mock()
    monkeypatch.setattr('joblib.dump', mock_dump)
    
    # Mock json.dump
    mock_json_dump = Mock()
    monkeypatch.setattr('json.dump', mock_json_dump)
    
    # Mock open
    mock_open = MagicMock()
    mock_open.return_value.__enter__ = Mock()
    mock_open.return_value.__exit__ = Mock()
    mock_open.return_value.write = Mock()
    monkeypatch.setattr('builtins.open', mock_open)
    
    # Mock os.makedirs
    monkeypatch.setattr('os.makedirs', lambda x, exist_ok: True)
    
    # Mock the directory
    monkeypatch.setattr('training.model_saver.os.path.join', lambda *args: '/'.join(args))
    
    try:
        saver.save(best_model, trained_models, feature_columns)
        # Should save Random Forest instead of LSTM
        assert mock_dump.called
    except Exception as e:
        pytest.fail(f"save raised an exception: {e}")


@pytest.mark.unit
def test_save_includes_hyperparameters(mock_model_dir, monkeypatch):
    """Test that hyperparameters are included in saved model info."""
    saver = ModelSaver()
    
    # Mock the model
    mock_model = Mock()
    mock_model.predict = Mock(return_value=np.array([1, 2, 3]))
    
    best_model = {
        'Model': 'Random Forest',
        'RMSE': 2.5,
        'MAE': 2.0,
        'MAPE': 5.5,
        'R2': 0.95
    }
    
    trained_models = {
        'Random Forest': {
            'model': mock_model,
            'hyperparameters': {'n_estimators': 100, 'max_depth': 5}
        }
    }
    
    feature_columns = ['feature1', 'feature2', 'feature3']
    
    # Track what was passed to json.dump
    captured_data = []
    
    def capture_json_dump(data, file, **kwargs):
        captured_data.append(data)
    
    monkeypatch.setattr('json.dump', capture_json_dump)
    
    # Mock open
    mock_open = MagicMock()
    mock_open.return_value.__enter__ = Mock()
    mock_open.return_value.__exit__ = Mock()
    mock_open.return_value.write = Mock()
    monkeypatch.setattr('builtins.open', mock_open)
    
    # Mock os.makedirs
    monkeypatch.setattr('os.makedirs', lambda x, exist_ok: True)
    
    # Mock joblib.dump
    monkeypatch.setattr('joblib.dump', Mock())
    
    # Mock the directory
    monkeypatch.setattr('training.model_saver.os.path.join', lambda *args: '/'.join(args))
    
    try:
        saver.save(best_model, trained_models, feature_columns)
        # Check if hyperparameters were included in model info
        model_info_data = captured_data[1]  # Second call is for model_info.json
        assert 'Hyperparameters' in model_info_data
        assert model_info_data['Hyperparameters'] == {'n_estimators': 100, 'max_depth': 5}
    except Exception as e:
        pytest.fail(f"save raised an exception: {e}")


@pytest.mark.unit
def test_convert_numpy_none_values():
    """Test conversion handles None values."""
    saver = ModelSaver()
    
    data = {'value': None}
    result = saver.convert_numpy(data)
    assert result['value'] is None


@pytest.mark.unit
def test_convert_numpy_string_values():
    """Test conversion handles string values."""
    saver = ModelSaver()
    
    data = {'value': 'test_string'}
    result = saver.convert_numpy(data)
    assert result['value'] == 'test_string'
