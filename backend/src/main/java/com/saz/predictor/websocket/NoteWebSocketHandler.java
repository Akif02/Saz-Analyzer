package com.saz.predictor.websocket;

import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;

/**
 * WebSocket-Handler, der eingehende Nachrichten mit Tonhöhen-/Frequenzdaten verarbeitet.
 * Er fungiert als Brücke (Proxy), leitet die empfangenen Daten vom Frontend 
 * per asynchronem HTTP-POST an den Python Machine-Learning-Service weiter
 * und schickt dessen Antwort über den WebSocket-Tunnel direkt zurück an den Client.
 */
@Component
public class NoteWebSocketHandler extends TextWebSocketHandler {

    private final WebClient webClient;

    /**
     * Konstruiert einen neuen NoteWebSocketHandler.
     *
     * @param webClientBuilder der Builder zur Erstellung der WebClient-Instanz
     * @param mlServiceUrl     die Basis-URL des Python ML-Services (wird aus application.properties injiziert)
     */
    public NoteWebSocketHandler(WebClient.Builder webClientBuilder, @org.springframework.beans.factory.annotation.Value("${ml.service.url:http://localhost:8000}") String mlServiceUrl) {
        this.webClient = webClientBuilder.baseUrl(mlServiceUrl).build();
    }

    /**
     * Behandelt eine eingehende Textnachricht von einem WebSocket-Client.
     * Leitet den JSON-Payload (Frequenzdaten) an das ML-Backend weiter und sendet
     * die Antwort (Vorhersage) bei Erfolg oder einen Fehler bei Misserfolg an die Session zurück.
     *
     * @param session die aktuelle WebSocket-Sitzung mit dem Client
     * @param message die eingehende Textnachricht (z.B. {"frequency": 293.66})
     * @throws Exception wenn ein Fehler während der WebSocket-Kommunikation auftritt
     */
    @Override
    protected void handleTextMessage(WebSocketSession session, TextMessage message) throws Exception {
        String payload = message.getPayload();
        System.out.println("Backend (Java) empfängt von Frontend: " + payload);

        // Weiterleiten an Python ML Service via WebClient
        webClient.post()
                .uri("/predict")
                .header("Content-Type", "application/json")
                .bodyValue(payload)
                .retrieve()
                .bodyToMono(String.class)
                .subscribe(
                        response -> {
                            try {
                                System.out.println("Backend (Java) empfängt von ML (Python): " + response);
                                if (session.isOpen()) {
                                    session.sendMessage(new TextMessage(response));
                                }
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        },
                        error -> {
                            System.err.println("Fehler beim Kommunizieren mit ML Service!");
                            error.printStackTrace();
                            try {
                                if (session.isOpen()) {
                                    session.sendMessage(new TextMessage("{\"error\": \"ML Service (Python) nicht erreichbar!\"}"));
                                }
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }
                );
    }
}
