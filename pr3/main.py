from flask import Flask, request, jsonify
from temperature_predictor import TemperaturePredictor

app = Flask(__name__)

predictor = TemperaturePredictor(file_path="resources/weather_data.csv", seed=1)

mae = predictor.train()
print(f"Mean absolute error: {mae:.2f}°C")

@app.route('/temperature/predict', methods=['GET'])
def predict_temperature():
    date_str = request.args.get('date')

    if not date_str:
        return jsonify({"error": "The date parameter is required"}), 400

    try:
        temperature = predictor.predict_temperature(date_str)
        return jsonify({
            "date": date_str,
            "temperature": round(float(temperature), 2),
            "unit": "°C",
            "mean_absolute_error": round(float(mae), 2)
        })
    except ValueError as e:
        return jsonify({"error": str(e)}), 400
    except Exception:
        return jsonify({"error": "Server error"}), 500

@app.route('/health', methods=['GET'])
def health():
    try:
        predictor.predict_temperature("2023-01-01T10:10")
        return jsonify({"health": True})
    except Exception:
        return jsonify({"health": False}), 500

if __name__ == '__main__':
    app.run(debug=True)