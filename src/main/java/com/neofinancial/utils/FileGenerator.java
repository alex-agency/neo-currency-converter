package com.neofinancial.utils;

import com.neofinancial.data.BestConversion;
import com.neofinancial.exceptions.FileGeneratorException;

import java.io.FileWriter;
import java.io.IOException;
import java.text.DecimalFormat;
import java.util.List;

public class FileGenerator {

    private FileGenerator() {
    }

    public static void generateCSV(final List<BestConversion> bestConversions, final String csvFileName) throws FileGeneratorException {
        try (FileWriter fileWriter = new FileWriter(csvFileName)) {
            // Add the header to the CSV file
            fileWriter.write("Currency Code,Currency Name,Amount of Currency,Path for Best Conversion Rate\n");

            // Write the best conversion data to the CSV file
            for (final BestConversion conversion : bestConversions) {
                fileWriter.write(conversion.getCurrencyCode() + ","
                        + conversion.getCurrencyName() + ","
                        + new DecimalFormat("#.#####").format(conversion.getCurrencyAmount()) + ","
                        + String.join(" -> ", conversion.getConversionPath()) + "\n");
            }
        } catch (IOException e) {
            throw new FileGeneratorException(e.getMessage());
        }
    }
}
