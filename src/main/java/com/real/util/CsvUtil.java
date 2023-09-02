package com.real.util;

import com.opencsv.CSVParser;
import com.real.matcher.Matcher;
import com.real.model.CsvMetadata;
import lombok.NonNull;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

/**
 * Utility class for handling CSV records
 */
public final class CsvUtil {

    private CsvUtil() {
    }

    /**
     * Parse CSV record
     *
     * @param data - CSV data
     * @return String[] - parsed CSV records as String array
     * @exception IOException - CSV data reading exception
     */
    public static String[] parse(@NonNull final String data) throws IOException {
        CSVParser parser = new CSVParser();
        return parser.parseLine(data);
    }

    /**
     * Building map of CSV headers and header index
     *
     * @param csvStream - CSV stream
     * @return Map<CsvMetadata, Integer> - map of header name as KEY and position index as VALUE
     */
    public static Map<CsvMetadata, Integer> tableColumnsIndexMap(@NonNull final Matcher.CsvStream csvStream) {
        Map<CsvMetadata, Integer> movieColumnsIndexMap = new HashMap<>();
        String[] columns = csvStream.getHeaderRow().split(",");
        for (int i = 0; i < columns.length; i++) {
            movieColumnsIndexMap.put(CsvMetadata.valueOf(columns[i].toUpperCase()), i);
        }

        return movieColumnsIndexMap;
    }
}
