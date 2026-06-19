import pytest
from fastapi.testclient import TestClient
from main import app

client = TestClient(app)

def test_predict_endpoint():
    response = client.post("/predict", json={"frequency": 293.66})
    assert response.status_code == 200
    data = response.json()
    assert "note" in data
    assert data["note"] == "Re (Prototyp-Antwort)"
    assert data["confidence"] == 1.0
