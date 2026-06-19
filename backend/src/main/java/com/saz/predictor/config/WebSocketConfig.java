package com.saz.predictor.config;

import com.saz.predictor.websocket.NoteWebSocketHandler;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.socket.config.annotation.EnableWebSocket;
import org.springframework.web.socket.config.annotation.WebSocketConfigurer;
import org.springframework.web.socket.config.annotation.WebSocketHandlerRegistry;

/**
 * Konfigurationsklasse für die WebSocket-Unterstützung der Anwendung.
 * Registriert die notwendigen WebSocket-Handler und Endpunkte.
 */
@Configuration
@EnableWebSocket
public class WebSocketConfig implements WebSocketConfigurer {

    private final NoteWebSocketHandler noteWebSocketHandler;

    /**
     * Erstellt eine neue WebSocketConfig mit dem angegebenen Handler.
     *
     * @param noteWebSocketHandler der Handler, der für die Verarbeitung der WebSocket-Nachrichten zuständig ist
     */
    public WebSocketConfig(NoteWebSocketHandler noteWebSocketHandler) {
        this.noteWebSocketHandler = noteWebSocketHandler;
    }

    /**
     * Registriert die WebSocket-Handler im System.
     * Mappt den {@link NoteWebSocketHandler} auf den Endpunkt "/ws/pitch"
     * und erlaubt vorerst Cross-Origin-Anfragen (CORS) von allen Domains ("*").
     *
     * @param registry die WebSocket-Handler Registry von Spring
     */
    @Override
    public void registerWebSocketHandlers(WebSocketHandlerRegistry registry) {
        registry.addHandler(noteWebSocketHandler, "/ws/pitch").setAllowedOrigins("*");
    }
}
