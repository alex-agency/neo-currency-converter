package com.neofinancial.utils;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.neofinancial.CurrencyConverter;
import com.neofinancial.exceptions.HttpClientException;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

import java.io.IOException;
import java.io.InputStream;
import java.util.Objects;
import java.util.concurrent.TimeUnit;

public class HttpClient {
    private HttpClient() {
    }

    private OkHttpClient client;

    private OkHttpClient getClient() {
        if (client == null) {
            client = new OkHttpClient().newBuilder()
                    .writeTimeout(1, TimeUnit.MINUTES)
                    .connectTimeout(1, TimeUnit.MINUTES)
                    .readTimeout(1, TimeUnit.MINUTES)
                    .build();
        }
        return client;
    }

    public static <T> T makeGetRequest(final String url, final TypeReference<T> responseType) throws HttpClientException {
        final Request request = new Request.Builder()
                .url(url)
                .build();

        try (Response response = new HttpClient().getClient().newCall(request).execute()) {
            if (response.isSuccessful()) {
                final String jsonResponse = Objects.requireNonNull(response.body()).string();
                return new ObjectMapper().readValue(jsonResponse, responseType);
            } else {
                throw new HttpClientException("Request failed with code: " + response.code());
            }
        } catch (IOException e) {
            throw new HttpClientException(e.getMessage());
        }
    }

    public static <T> T getDataFromResourceFile(final String resourceFile, final TypeReference<T> responseType) throws HttpClientException {
        try {
            final InputStream is = CurrencyConverter.class.getClassLoader().getResourceAsStream(resourceFile);
            final byte[] bytes = Objects.requireNonNull(is).readAllBytes();

            return new ObjectMapper().readValue(bytes, responseType);
        } catch (IOException e) {
            throw new HttpClientException(e.getMessage());
        }
    }
}
