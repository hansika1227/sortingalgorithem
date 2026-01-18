package ui;

import model.CSVData;

import javax.swing.*;
import java.awt.*;
import java.io.File;
import java.util.List;

public class MainUI extends JFrame {

    private JButton uploadBtn;
    private JButton sortBtn;
    private JComboBox<String> columnBox;
    private JTextArea outputArea;

    private CSVData csvData;

    public MainUI() {
        setTitle("Sorting Algorithm Performance Evaluator");
        setSize(700, 450);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout());

        csvData = new CSVData();

        // ================= TOP PANEL =================
        JPanel topPanel = new JPanel(new FlowLayout());

        uploadBtn = new JButton("Upload CSV");
        sortBtn = new JButton("Sort");
        columnBox = new JComboBox<>();

        topPanel.add(uploadBtn);
        topPanel.add(new JLabel("Select Numeric Column:"));
        topPanel.add(columnBox);
        topPanel.add(sortBtn);

        // ================= OUTPUT AREA =================
        outputArea = new JTextArea();
        outputArea.setEditable(false);

        add(topPanel, BorderLayout.NORTH);
        add(new JScrollPane(outputArea), BorderLayout.CENTER);

        // ================= BUTTON ACTIONS =================
        uploadBtn.addActionListener(e -> uploadCSV());
        sortBtn.addActionListener(e -> handleSort());

        setVisible(true);
    }

    // ================= CSV UPLOAD =================
    private void uploadCSV() {
        JFileChooser chooser = new JFileChooser();
        int result = chooser.showOpenDialog(this);

        if (result == JFileChooser.APPROVE_OPTION) {
            File file = chooser.getSelectedFile();

            // CSV validation
            if (!file.getName().endsWith(".csv")) {
                JOptionPane.showMessageDialog(this,
                        "Please select a CSV file",
                        "Error",
                        JOptionPane.ERROR_MESSAGE);
                return;
            }

            try {
                csvData.loadCSV(file);

                columnBox.removeAllItems();
                List<String> numericColumns = csvData.getNumericColumns();

                for (String col : numericColumns) {
                    columnBox.addItem(col);
                }

                outputArea.setText("CSV Loaded Successfully!\n\n");
                outputArea.append("Numeric Columns Found:\n");

                for (String col : numericColumns) {
                    outputArea.append("- " + col + "\n");
                }

            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this,
                        "Error reading CSV file",
                        "Error",
                        JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    // ================= SORT BUTTON =================
    private void handleSort() {
        String selectedColumn = (String) columnBox.getSelectedItem();

        if (selectedColumn == null) {
            JOptionPane.showMessageDialog(this,
                    "Please select a column first",
                    "Error",
                    JOptionPane.ERROR_MESSAGE);
            return;
        }

        double[] data = csvData.getColumnData(selectedColumn);

        outputArea.append("\n\nSelected Column: " + selectedColumn + "\n");
        outputArea.append("Sample Data (first 10 values):\n");

        for (int i = 0; i < Math.min(10, data.length); i++) {
            outputArea.append(data[i] + "\n");
        }

        outputArea.append("\nData successfully passed to backend.\n");
    }
}

