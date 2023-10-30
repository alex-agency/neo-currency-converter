package com.neofinancial.data;

import lombok.Builder;
import lombok.Getter;

import java.util.Set;

@Getter
@Builder
public class BestConversion {
    private String currencyCode;
    private String currencyName;
    private Double currencyAmount;
    private Set<String> conversionPath;
}
