package model;

import java.io.*;
import java.util.*;

public class CSVData {

    private List<String> headers;
    private List<List<String>> rows;

    public CSVData() {
        headers = new ArrayList<>();
        rows = new ArrayList<>();
    }

    // load the CSV file
    public void loadCSV(File file) throws IOException {
        BufferedReader br = new BufferedReader(new FileReader(file));
        String line;

        // Header line
        line = br.readLine();
        if (line == null) {
            throw new IOException("Empty CSV file");
        }
        headers = Arrays.asList(line.split(","));

        // Data rows
        while ((line = br.readLine()) != null) {
            rows.add(Arrays.asList(line.split(",")));
        }
        br.close();
    }

    // identify Numeric columns
    public List<String> getNumericColumns() {
        List<String> numericCols = new ArrayList<>();

        for (int col = 0; col < headers.size(); col++) {
            boolean isNumeric = true;

            for (List<String> row : rows) {
                try {
                    Double.parseDouble(row.get(col));
                } catch (Exception e) {
                    isNumeric = false;
                    break;
                }
            }

            if (isNumeric) {
                numericCols.add(headers.get(col));
            }
        }
        return numericCols;
    }

    // Selected column data → double[]
    public double[] getColumnData(String columnName) {
        int index = headers.indexOf(columnName);
        double[] data = new double[rows.size()];

        for (int i = 0; i < rows.size(); i++) {
            data[i] = Double.parseDouble(rows.get(i).get(index));
        }
        return data;
    }
}
