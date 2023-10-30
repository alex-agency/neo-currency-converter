package com.neofinancial;

import com.neofinancial.data.BestConversion;
import com.neofinancial.exceptions.FileGeneratorException;
import com.neofinancial.utils.FileGenerator;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

public class FileGeneratorTest {
    private List<BestConversion> bestConversions;
    private String csvFileName;

    @BeforeEach
    void setUp() {
        bestConversions = new ArrayList<>();
        bestConversions.add(BestConversion.builder()
                    .currencyCode("USD")
                    .currencyName("US Dollar")
                    .currencyAmount(100.0)
                    .conversionPath(Set.of("USD", "EUR", "GBP"))
                .build());
        bestConversions.add(BestConversion.builder()
                    .currencyCode("EUR")
                    .currencyName("Euro")
                    .currencyAmount(200.0)
                    .conversionPath(Set.of("EUR", "GBP"))
                .build());
        csvFileName = "test.csv";
    }

    @Test
    void testGenerateCSV() {
        try {
            FileGenerator.generateCSV(bestConversions, csvFileName);
        } catch (FileGeneratorException e) {
            fail("Exception should not have been thrown: " + e.getMessage());
        }
    }
}
