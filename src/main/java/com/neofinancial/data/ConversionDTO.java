package com.neofinancial.data;

import lombok.Data;
import lombok.ToString;

@Data
@ToString
public class ConversionDTO {
    private String fromCurrencyCode;
    private String toCurrencyCode;
    private Double exchangeRate;
    private String fromCurrencyName;
    private String toCurrencyName;
}
