# Saz Note Predictor (Prototype)

Proof of Concept für die Echtzeit-Notenerkennung einer Saz. Das Ziel dieses Repositories ist die Evaluierung der Kommunikations-Pipeline zwischen Frontend, Backend und dem Machine-Learning-Service.

## Architektur

Das Projekt ist in drei Schichten aufgeteilt:

- **frontend/**: Simples HTML/JS-Setup. Verbindet sich per WebSocket mit dem Backend und sendet simulierte Frequenzdaten.
- **backend/**: Spring Boot Anwendung (Port 8081). Leitet eingehende WebSocket-Nachrichten asynchron per HTTP POST an den ML-Service weiter.
- **ml/**: Python FastAPI Service (Port 8000). Stellt den `/predict` Endpunkt zur Verfügung (aktuell mit statischen Mock-Antworten).

## Setup & Ausführen

Voraussetzungen: Python 3.x und Java/Maven.

1. **ML Service starten**
   ```sh
   cd ml
   python -m pip install -r requirements.txt
   python main.py
   ```

2. **Java Backend starten**
   ```sh
   cd backend
   ./mvnw.cmd spring-boot:run
   ```

3. **Frontend testen**
   Die Datei `frontend/index.html` im Browser öffnen und den Start-Button klicken.

## Tests

- **ML Tests (Unit):** 
  ```sh
  cd ml
  python -m pip install -r requirements-test.txt
  python -m pytest test_main.py
  ```
- **Java Tests (Unit & Integration):** 
  ```sh
  cd backend
  ./mvnw.cmd test
  ```
- **E2E Pipeline Test:** 
  Beide Server (Java & Python) müssen dafür lokal laufen.
  ```sh
  cd system_test
  python -m pip install -r requirements.txt
  python -m pytest test_pipeline.py
  ```
