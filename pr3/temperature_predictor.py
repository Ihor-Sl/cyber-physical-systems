import pandas as pd
from sklearn.ensemble import RandomForestRegressor
from sklearn.model_selection import train_test_split
from sklearn.preprocessing import StandardScaler
from sklearn.metrics import mean_absolute_error
from datetime import datetime

class TemperaturePredictor:
    DATE_FORMAT = "%Y%m%dT%H%M"
    INPUT_DATE_FORMAT = "%Y-%m-%dT%H:%M"

    def __init__(self, file_path, seed=1):
        self.file_path = file_path
        self.seed = seed
        self.scaler = StandardScaler()
        self.model = RandomForestRegressor(random_state=seed)

    def train(self):
        df = pd.read_csv(self.file_path, skiprows=10, names=["timestamp", "temperature"])
        
        df["timestamp"] = df["timestamp"].apply(lambda ts: datetime.strptime(ts, self.DATE_FORMAT))
        df["year"] = df["timestamp"].dt.year
        df["month"] = df["timestamp"].dt.month
        df["day"] = df["timestamp"].dt.day
        df["hour"] = df["timestamp"].dt.hour
        df.drop(columns=["timestamp"], inplace=True)
        
        X = df[["year", "month", "day", "hour"]]
        y = df["temperature"]
        
        X_train, X_test, y_train, y_test = train_test_split(
            X, y, test_size=0.2, random_state=self.seed
        )
        
        self.scaler.fit(X_train)
        X_train_scaled = self.scaler.transform(X_train)
        X_test_scaled = self.scaler.transform(X_test)
        
        self.model.fit(X_train_scaled, y_train)
        
        predictions = self.model.predict(X_test_scaled)

        return mean_absolute_error(y_test, predictions)

    def predict_temperature(self, date_str):
        dt = datetime.strptime(date_str, self.INPUT_DATE_FORMAT)
        input_data = pd.DataFrame([[dt.year, dt.month, dt.day, dt.hour]], 
                                columns=["year", "month", "day", "hour"])
        input_scaled = self.scaler.transform(input_data)
        predicted_temp = self.model.predict(input_scaled)[0]
        return predicted_temp