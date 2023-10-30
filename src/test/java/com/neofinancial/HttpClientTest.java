package com.neofinancial;

import com.fasterxml.jackson.core.type.TypeReference;
import com.neofinancial.utils.HttpClient;
import lombok.Data;
import okhttp3.mockwebserver.MockResponse;
import okhttp3.mockwebserver.MockWebServer;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.IOException;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class HttpClientTest {
    private MockWebServer server;

    @BeforeEach
    public void setUp() throws IOException {
        server = new MockWebServer();
        server.start();
    }

    @AfterEach
    public void tearDown() throws IOException {
        server.shutdown();
    }

    @Test
    public void testMakeGetRequest_Success() throws IOException {
        server.enqueue(new MockResponse().setBody("{\"result\": \"success\"}"));

        final String url = server.url("/api/success").toString();
        final TypeReference<Response> responseType = new TypeReference<>() {};
        final Response response = HttpClient.makeGetRequest(url, responseType);

        assertEquals("success", response.getResult());
    }

    @Test
    public void testMakeGetRequest_Failure() {
        server.enqueue(new MockResponse().setResponseCode(404));

        final String url = server.url("/api/failure").toString();
        final TypeReference<Response> responseType = new TypeReference<>() {};
        assertThrows(IOException.class, () -> {
            HttpClient.makeGetRequest(url, responseType);
        });
    }

    @Data
    static class Response {
        private String result;
    }
}
