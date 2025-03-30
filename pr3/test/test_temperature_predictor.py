import pandas as pd
from unittest.mock import patch, MagicMock
from temperature_predictor import TemperaturePredictor

def test_train():
    predictor = TemperaturePredictor(file_path="dummy.csv")
    
    mock_data = pd.DataFrame({
        "timestamp": ["20250329T1200", "20250329T1300", "20250329T1400", "20250329T1500"],
        "temperature": [15.5, 16.2, 16.8, 17.0]
    })
    
    with patch("pandas.read_csv", return_value=mock_data):
        mae = predictor.train()

    assert mae >= 0

def test_predict_temperature():
    predictor = TemperaturePredictor(file_path="dummy.csv")
    
    predictor.scaler.fit([[2025, 3, 29, 12]])
    predictor.scaler.transform = MagicMock(return_value=[[0, 0, 0, 0]])
    predictor.model.predict = MagicMock(return_value=[15.5])
    
    predicted_temp = predictor.predict_temperature("2025-03-30T12:00")
    
    assert isinstance(predicted_temp, float)
    assert predicted_temp == 15.5