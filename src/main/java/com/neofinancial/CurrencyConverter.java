package com.neofinancial;

import com.fasterxml.jackson.core.type.TypeReference;
import com.neofinancial.data.ConversionDTO;
import com.neofinancial.data.BestConversion;
import com.neofinancial.exceptions.CurrencyConverterException;
import com.neofinancial.utils.HttpClient;
import com.neofinancial.utils.FileGenerator;
import lombok.AllArgsConstructor;

import java.io.IOException;
import java.text.DecimalFormat;
import java.util.*;

@AllArgsConstructor
public class CurrencyConverter {
    private final String url;

    public void convert(final String sourceCurrency, final Double sourceCurrencyAmount, final String targetCurrency) throws CurrencyConverterException {
        try {
            final Set<ConversionDTO> allConversions = getData(url);

            final List<BestConversion> bestConversions = findBestConversions(sourceCurrency, sourceCurrencyAmount, allConversions);

            FileGenerator.generateCSV(bestConversions, "currency_conversions.csv");

            if (!targetCurrency.isEmpty()) {
                final Optional<BestConversion> targetConversion = bestConversions.stream()
                        .filter(c -> c.getCurrencyCode().equals(targetCurrency)).findFirst();
                if (targetConversion.isPresent()) {
                    final BestConversion conversion = targetConversion.get();
                    System.out.printf("Source: %s %s %n",
                            100, sourceCurrency);
                    System.out.printf("Target: %s %s %n",
                            new DecimalFormat("#.#####").format(conversion.getCurrencyAmount()), conversion.getCurrencyCode());
                    System.out.printf("Conversion: %s %n",
                            String.join(" -> ", conversion.getConversionPath()));
                } else {
                    System.out.printf("Conversion not found for %s -> %s currencies %n", sourceCurrency, targetCurrency);
                }
            }
        } catch (Exception e) {
            throw new CurrencyConverterException(e.getMessage());
        }
    }

    List<BestConversion> findBestConversions(final String sourceCurrency, final Double sourceCurrencyAmount, final Set<ConversionDTO> allConversions) {
        final Map<String, Double> bestConversionRates = new HashMap<>();
        bestConversionRates.put(sourceCurrency, sourceCurrencyAmount);

        final Map<String, Set<String>> bestConversionPaths = new HashMap<>();
        bestConversionPaths.put(sourceCurrency, new LinkedHashSet<>());

        final Map<String, String> currenciesName = new HashMap<>();

        // Create a priority queue to store currencies and their conversion rates
        final PriorityQueue<String> pq = new PriorityQueue<>((a, b) ->
                Double.compare(bestConversionRates.get(b), bestConversionRates.get(a)));
        pq.add(sourceCurrency);

        // Start breadth-first search for best conversions
        while ( !pq.isEmpty()) {
            final String currentCurrency = pq.poll();

            // Iterate through all available conversion rates
            for (final ConversionDTO conversion : allConversions) {
                if (conversion.getFromCurrencyCode().equals(currentCurrency)) {
                    currenciesName.put(conversion.getFromCurrencyCode(), conversion.getFromCurrencyName());
                    double newAmount = bestConversionRates.get(currentCurrency) * conversion.getExchangeRate();

                    // Check if the new rate is better
                    if ( !bestConversionRates.containsKey(conversion.getToCurrencyCode())
                            || newAmount > bestConversionRates.get(conversion.getToCurrencyCode())) {
                        // Update best rates, paths, and currency names
                        bestConversionRates.put(conversion.getToCurrencyCode(), newAmount);
                        final Set<String> newPath = new LinkedHashSet<>(bestConversionPaths.get(currentCurrency));
                        newPath.add(currentCurrency);
                        bestConversionPaths.put(conversion.getToCurrencyCode(), newPath);
                        currenciesName.put(conversion.getToCurrencyCode(), conversion.getToCurrencyName());
                        // Add the target currency to the priority queue for further exploration
                        pq.add(conversion.getToCurrencyCode());
                    }
                }
            }
        }

        // Build and sort the list of BestConversion objects
        final List<BestConversion> bestConversions = new ArrayList<>();
        for (Map.Entry<String, Double> entry : bestConversionRates.entrySet()) {
            final Set<String> bestPath = new LinkedHashSet<>(bestConversionPaths.get(entry.getKey()));
            bestPath.add(entry.getKey());

            final BestConversion bestConversion = BestConversion.builder()
                    .currencyCode(entry.getKey())
                    .currencyName(currenciesName.get(entry.getKey()))
                    .currencyAmount(entry.getValue())
                    .conversionPath(bestPath)
                    .build();
            bestConversions.add(bestConversion);
        }
        bestConversions.sort(new BestConversionComparator());
        return bestConversions;
    }

    Set<ConversionDTO> getData(final String url) {
        try {
            final TypeReference<Set<ConversionDTO>> typeReference = new TypeReference<>() {};
            if (url.endsWith(".json")) {
                return HttpClient.getDataFromResourceFile(url, typeReference);
            }
            return HttpClient.makeGetRequest(url, typeReference);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private static class BestConversionComparator implements Comparator<BestConversion> {
        @Override
        public int compare(BestConversion a, BestConversion b) {
            int sizeA = a.getConversionPath().size();
            int sizeB = b.getConversionPath().size();
            return Integer.compare(sizeA, sizeB);
        }
    }

}
