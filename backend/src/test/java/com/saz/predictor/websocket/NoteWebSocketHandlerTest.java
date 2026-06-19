package com.saz.predictor.websocket;

import okhttp3.mockwebserver.MockResponse;
import okhttp3.mockwebserver.MockWebServer;
import okhttp3.mockwebserver.RecordedRequest;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;

import java.io.IOException;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

class NoteWebSocketHandlerTest {

    private MockWebServer mockWebServer;
    private NoteWebSocketHandler handler;
    private WebSocketSession session;

    @BeforeEach
    void setUp() throws IOException {
        mockWebServer = new MockWebServer();
        mockWebServer.start();

        WebClient.Builder webClientBuilder = WebClient.builder();
        String mlUrl = mockWebServer.url("/").toString().replaceAll("/$", "");
        handler = new NoteWebSocketHandler(webClientBuilder, mlUrl);
        
        session = mock(WebSocketSession.class);
        when(session.isOpen()).thenReturn(true);
    }

    @AfterEach
    void tearDown() throws IOException {
        mockWebServer.shutdown();
    }

    @Test
    void testHandleTextMessage() throws Exception {
        String expectedResponse = "{\"note\": \"Re (Prototyp-Antwort)\", \"confidence\": 1.0}";
        mockWebServer.enqueue(new MockResponse()
                .setBody(expectedResponse)
                .addHeader("Content-Type", "application/json"));

        TextMessage message = new TextMessage("{\"frequency\": 293.66}");
        handler.handleTextMessage(session, message);

        // Verify outgoing request to Python
        RecordedRequest request = mockWebServer.takeRequest();
        assertEquals("POST", request.getMethod());
        assertEquals("/predict", request.getPath());
        assertEquals("{\"frequency\": 293.66}", request.getBody().readUtf8());

        // Verify WebSocket response to client
        ArgumentCaptor<TextMessage> messageCaptor = ArgumentCaptor.forClass(TextMessage.class);
        verify(session, timeout(1000).times(1)).sendMessage(messageCaptor.capture());
        
        assertEquals(expectedResponse, messageCaptor.getValue().getPayload());
    }
}
