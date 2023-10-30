package com.neofinancial;

public class Main {
    public static final String DEFAULT_CURRENCY_CODE = "CAD";
    public static final Double DEFAULT_CURRENCY_AMOUNT = 100.0;
    public static final String DEFAULT_DATA_SOURCE = "https://api-coding-challenge.neofinancial.com/currency-conversion?seed=17558";

    public static void main(String[] args) throws Exception {
        final String url = args.length > 0 ? args[0] : DEFAULT_DATA_SOURCE;
        if (url == null || url.isEmpty()) {
            throw new IllegalArgumentException("API url is not specified.");
        }
        final String sourceCurrencyCode = args.length > 1 ? args[1] : DEFAULT_CURRENCY_CODE;
        if (sourceCurrencyCode == null || sourceCurrencyCode.isEmpty()) {
            throw new IllegalArgumentException("Currency is not specified.");
        }
        final Double sourceCurrencyAmount = args.length > 2 ? Double.parseDouble(args[2]) : DEFAULT_CURRENCY_AMOUNT;
        final String targetCurrency = args.length > 3 ? args[3] : "";

        new CurrencyConverter(url).convert(sourceCurrencyCode, sourceCurrencyAmount, targetCurrency);
    }
}
