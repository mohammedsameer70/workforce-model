# Python ML Service Tests

This directory contains unit and integration tests for the Workforce Forecasting Python ML service.

## Test Structure

- `conftest.py` - Shared fixtures and test configuration
- `test_models.py` - Unit tests for model training functions (Linear Regression, Random Forest, XGBoost)
- `test_evaluation.py` - Unit tests for evaluation metrics and result saving
- `test_model_saver.py` - Unit tests for model saving and loading functionality
- `test_integration.py` - Integration tests for the complete training pipeline

## Running Tests

### Install Test Dependencies

```bash
pip install -r requirements.txt
```

### Run All Tests

```bash
pytest
```

### Run Specific Test File

```bash
pytest tests/test_models.py
```

### Run Specific Test

```bash
pytest tests/test_models.py::test_train_linear_regression
```

### Run with Coverage Report

```bash
pytest --cov=. --cov-report=html
```

Coverage report will be generated in `htmlcov/index.html`.

### Run by Marker

```bash
# Run only unit tests
pytest -m unit

# Run only integration tests
pytest -m integration

# Exclude slow tests
pytest -m "not slow"
```

## Test Categories

### Unit Tests (`@pytest.mark.unit`)
- Test individual functions and methods in isolation
- Use mocks to avoid external dependencies
- Fast execution

### Integration Tests (`@pytest.mark.integration`)
- Test the interaction between multiple components
- Test the complete training pipeline
- Slower execution due to model training

### Slow Tests (`@pytest.mark.slow`)
- Tests that involve actual model training
- May take several seconds to complete
- Marked to allow skipping during quick development cycles

## Fixtures

- `sample_data` - Generates sample training data for testing
- `sample_features` - Creates feature matrix and target from sample data
- `temp_dir` - Creates a temporary directory for test outputs (auto-cleanup)
- `mock_model_dir` - Creates a mock saved_models directory
- `mock_results_dir` - Creates a mock results directory

## Coverage Goals

- Model training functions: >80%
- Evaluation functions: >90%
- Model saving/loading: >80%
- Integration pipeline: >70%

## Adding New Tests

1. Create a new test file in the `tests/` directory
2. Import necessary modules from the parent directory
3. Use appropriate fixtures from `conftest.py`
4. Mark tests with appropriate markers (`@pytest.mark.unit`, `@pytest.mark.integration`, etc.)
5. Follow the naming convention: `test_<function_name>`

## Continuous Integration

These tests are designed to run in CI/CD pipelines. Use the following command for CI:

```bash
pytest -v --tb=short --cov=. --cov-report=term-missing -m "not slow"
```

This runs all tests except slow ones with coverage reporting.
