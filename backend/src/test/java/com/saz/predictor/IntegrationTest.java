package com.saz.predictor;

import okhttp3.mockwebserver.MockResponse;
import okhttp3.mockwebserver.MockWebServer;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.client.standard.StandardWebSocketClient;
import org.springframework.web.socket.handler.TextWebSocketHandler;

import java.io.IOException;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class IntegrationTest {

    @LocalServerPort
    private int port;

    private static MockWebServer mockBackEnd;

    @BeforeAll
    static void setUp() throws IOException {
        mockBackEnd = new MockWebServer();
        mockBackEnd.start();
    }

    @AfterAll
    static void tearDown() throws IOException {
        mockBackEnd.shutdown();
    }

    @DynamicPropertySource
    static void properties(DynamicPropertyRegistry registry) {
        registry.add("ml.service.url", () -> mockBackEnd.url("/").toString().replaceAll("/$", ""));
    }

    @Test
    void testWebSocketToMlServiceFlow() throws Exception {
        String expectedMlResponse = "{\"note\": \"Re (Prototyp-Antwort)\", \"confidence\": 1.0}";
        mockBackEnd.enqueue(new MockResponse()
                .setBody(expectedMlResponse)
                .addHeader("Content-Type", "application/json"));

        StandardWebSocketClient client = new StandardWebSocketClient();
        CompletableFuture<String> result = new CompletableFuture<>();

        WebSocketSession session = client.execute(new TextWebSocketHandler() {
            @Override
            protected void handleTextMessage(WebSocketSession session, TextMessage message) {
                result.complete(message.getPayload());
            }
        }, "ws://localhost:" + port + "/ws/pitch").get(2, TimeUnit.SECONDS);

        session.sendMessage(new TextMessage("{\"frequency\": 293.66}"));

        String response = result.get(5, TimeUnit.SECONDS);
        assertNotNull(response);
        assertEquals(expectedMlResponse, response);
    }
}
