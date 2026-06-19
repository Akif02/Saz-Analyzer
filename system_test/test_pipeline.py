import asyncio
import json
import websockets
import pytest

@pytest.mark.asyncio
async def test_end_to_end_pipeline():
    """
    Simuliert das Frontend und testet die gesamte End-to-End Pipeline.
    Voraussetzung: 
    - Java Backend läuft auf localhost:8081
    - Python ML Backend läuft auf localhost:8000
    """
    uri = "ws://localhost:8081/ws/pitch"
    
    try:
        async with websockets.connect(uri) as websocket:
            # Sende Frequenz
            await websocket.send(json.dumps({"frequency": 293.66}))
            
            # Erwarte Antwort
            response = await asyncio.wait_for(websocket.recv(), timeout=5.0)
            
            data = json.loads(response)
            assert data.get("note") == "Re (Prototyp-Antwort)"
            assert data.get("confidence") == 1.0
            
    except ConnectionRefusedError:
        pytest.fail("Verbindung zum Backend fehlgeschlagen. Ist das Java-Backend auf Port 8081 gestartet?")
