package com.neofinancial.exceptions;

public class CurrencyConverterException extends Exception {
    public CurrencyConverterException(final String message) {
        super(message);
    }

    public CurrencyConverterException(final String message, final Throwable cause) {
        super(message, cause);
    }
}
