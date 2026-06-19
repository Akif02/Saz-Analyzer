from fastapi import FastAPI
from pydantic import BaseModel

app = FastAPI()

class FrequencyData(BaseModel):
    frequency: float

@app.post("/predict")
async def predict(data: FrequencyData):
    print(f"Empfangene Frequenz im ML-Backend: {data.frequency}")
    # Fixe Dummy-Antwort wie gewünscht
    return {"note": "Re (Prototyp-Antwort)", "confidence": 1.0}

if __name__ == "__main__":
    import uvicorn
    # Startet den Server auf Port 8000
    uvicorn.run(app, host="0.0.0.0", port=8000)
