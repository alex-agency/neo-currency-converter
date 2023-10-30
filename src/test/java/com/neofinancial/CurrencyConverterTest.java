package com.neofinancial;

import com.neofinancial.data.BestConversion;
import com.neofinancial.data.ConversionDTO;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;
import java.text.DecimalFormat;
import java.util.List;
import java.util.Optional;
import java.util.Set;

public class CurrencyConverterTest {

    private CurrencyConverter currencyConverter;

    @BeforeEach
    public void setUp() {
        currencyConverter = new CurrencyConverter("testData.json");
    }

    @Test
    public void testFindBestConversionsWithTestData() {
        final Set<ConversionDTO> testConversions = currencyConverter.getData("testData.json");
        final List<BestConversion> bestConversions = currencyConverter.findBestConversions("USD", 10.0, testConversions);

        assertNotNull(bestConversions);
        assertEquals(17, bestConversions.size());
        BestConversion bestConversion = bestConversions.get(0);
        assertEquals("USD", bestConversion.getCurrencyCode());

        final Optional<BestConversion> conversionToINR =
                bestConversions.stream().filter(c -> c.getCurrencyCode().equals("INR")).findFirst();
        assertTrue(conversionToINR.isPresent());
        assertEquals("744.5979", new DecimalFormat("#.####")
                .format(conversionToINR.get().getCurrencyAmount()));
    }

    @Test
    public void testFindBestConversionsWithCustomData() {
        final ConversionDTO c1 = createConversion(
                "UAH", "Ukraine Hryvna",
                "USD", "US Dollar", 0.028);
        final ConversionDTO c2 = createConversion(
                "USD", "US Dollar",
                "CAD", "Canada Dollar", 1.39);
        final ConversionDTO c3 = createConversion(
                "UAH", "Ukraine Hryvna",
                "CAD", "Canada Dollar", 0.038);

        final Set<ConversionDTO> testConversions = Set.of(c1, c2, c3);
        currencyConverter = new CurrencyConverter("testData.json") {
            Set<ConversionDTO> getData(String url) {
                return testConversions;
            }
        };

        final List<BestConversion> bestConversions = currencyConverter.findBestConversions("UAH", 1000.0, testConversions);

        assertNotNull(bestConversions);
        assertEquals(3, bestConversions.size());
        BestConversion bestConversion = bestConversions.get(0);
        assertEquals("UAH", bestConversion.getCurrencyCode());
        assertEquals("Ukraine Hryvna", bestConversion.getCurrencyName());

        final Optional<BestConversion> conversionToCAD =
                bestConversions.stream().filter(c -> c.getCurrencyCode().equals("CAD")).findFirst();
        assertTrue(conversionToCAD.isPresent());
        assertEquals("38.92", new DecimalFormat("#.####")
                .format(conversionToCAD.get().getCurrencyAmount()));
    }

    private ConversionDTO createConversion(final String fromCurrencyCode, final String fromCurrencyName,
                                           final String toCurrencyCode, final String toCurrencyName,
                                           final Double exchangeRate) {
        final ConversionDTO conversion = new ConversionDTO();
        conversion.setFromCurrencyCode(fromCurrencyCode);
        conversion.setFromCurrencyName(fromCurrencyName);
        conversion.setToCurrencyCode(toCurrencyCode);
        conversion.setToCurrencyName(toCurrencyName);
        conversion.setExchangeRate(exchangeRate);
        return conversion;
    }

}
