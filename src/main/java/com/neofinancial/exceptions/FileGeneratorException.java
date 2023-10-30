package com.neofinancial.exceptions;

import java.io.IOException;

public class FileGeneratorException extends IOException {
    public FileGeneratorException(final String message) {
        super(message);
    }

    public FileGeneratorException(final String message, final Throwable cause) {
        super(message, cause);
    }
}
