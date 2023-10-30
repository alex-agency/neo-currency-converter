package com.neofinancial.exceptions;

import java.io.IOException;

public class HttpClientException extends IOException {
    public HttpClientException(final String message) {
        super(message);
    }

    public HttpClientException(final String message, final Throwable cause) {
        super(message, cause);
    }
}
