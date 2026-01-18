package ui;

import model.CSVData;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.border.TitledBorder;
import java.awt.*;
import java.io.File;
import java.util.List;

public class MainUI extends JFrame {

    private JButton uploadBtn;
    private JButton sortBtn;
    private JComboBox<String> columnBox;
    private JTextArea outputArea;

    private CSVData csvData;

    // Modern UI Colors
    private final Color primaryColor = new Color(17, 69, 4, 255); // Blue
    private final Color secondaryColor = new Color(23, 128, 66); // Green
    private final Color bgColor = new Color(209, 255, 214, 168); // Light Gray
    private final Color textColor = new Color(3, 64, 14); // Dark Blue/Gray

    public MainUI() {
        // GUI Design & Layout Development (Story Point: 8)
        setTitle("Sorting Algorithm Performance Evaluator");
        setSize(850, 600);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        getContentPane().setBackground(bgColor);
        setLayout(new BorderLayout(10, 10));

        csvData = new CSVData();

        // ================= TOP PANEL (Input Section) =================
        JPanel topPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 15));
        topPanel.setBackground(Color.WHITE);
        topPanel.setBorder(BorderFactory.createMatteBorder(0, 0, 2, 0, new Color(200, 200, 200)));

        uploadBtn = new JButton("Upload CSV");
        styleButton(uploadBtn, primaryColor);

        sortBtn = new JButton("Run Performance Test");
        styleButton(sortBtn, secondaryColor);

        columnBox = new JComboBox<>();
        columnBox.setPreferredSize(new Dimension(180, 30));
        columnBox.setFont(new Font("Segoe UI", Font.PLAIN, 14));

        topPanel.add(uploadBtn);
        JLabel label = new JLabel("Select Column:");
        label.setFont(new Font("Segoe UI", Font.BOLD, 14));
        topPanel.add(label);
        topPanel.add(columnBox);
        topPanel.add(sortBtn);

        // ================= CENTER PANEL (Output Area) =================
        JPanel centerPanel = new JPanel(new BorderLayout());
        centerPanel.setBackground(bgColor);
        centerPanel.setBorder(new EmptyBorder(15, 15, 15, 15));

        outputArea = new JTextArea();
        outputArea.setEditable(false);
        outputArea.setFont(new Font("Consolas", Font.PLAIN, 14));
        outputArea.setBackground(Color.WHITE);
        outputArea.setForeground(textColor);
        outputArea.setMargin(new Insets(15, 15, 15, 15));

        JScrollPane scrollPane = new JScrollPane(outputArea);
        scrollPane.setBorder(BorderFactory.createTitledBorder(
                BorderFactory.createLineBorder(new Color(189, 195, 199)),
                "Evaluation Results & Analytics",
                TitledBorder.LEFT,
                TitledBorder.TOP,
                new Font("Segoe UI", Font.BOLD, 14),
                primaryColor
        ));

        centerPanel.add(scrollPane, BorderLayout.CENTER);

        add(topPanel, BorderLayout.NORTH);
        add(centerPanel, BorderLayout.CENTER);

        // ================= BUTTON ACTIONS =================
        uploadBtn.addActionListener(e -> uploadCSV());
        sortBtn.addActionListener(e -> handleSort());

        setLocationRelativeTo(null);
        setVisible(true);
    }

    // Helper Method to Style Buttons
    private void styleButton(JButton btn, Color bg) {
        btn.setBackground(bg);
        btn.setForeground(Color.WHITE);
        btn.setFocusPainted(false);
        btn.setFont(new Font("Segoe UI", Font.BOLD, 13));
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btn.setBorder(new EmptyBorder(8, 15, 8, 15));
    }

    private void uploadCSV() {
        JFileChooser chooser = new JFileChooser();
        int result = chooser.showOpenDialog(this);

        if (result == JFileChooser.APPROVE_OPTION) {
            File file = chooser.getSelectedFile();
            if (!file.getName().endsWith(".csv")) {
                showError("Please select a valid CSV file!");
                return;
            }

            try {
                csvData.loadCSV(file);
                columnBox.removeAllItems();
                List<String> numericColumns = csvData.getNumericColumns();

                for (String col : numericColumns) {
                    columnBox.addItem(col);
                }

                outputArea.setText("✅ FILE LOADED: " + file.getName() + "\n");
                outputArea.append("----------------------------------------------------------\n");
                outputArea.append("System found " + numericColumns.size() + " numeric columns ready for sorting.\n");

            } catch (Exception ex) {
                showError("Error processing CSV file.");
            }
        }
    }

    private void handleSort() {
        String selectedColumn = (String) columnBox.getSelectedItem();
        if (selectedColumn == null) {
            showError("Please select a numeric column first!");
            return;
        }

        double[] originalData = csvData.getColumnData(selectedColumn);


        outputArea.setText("");

        outputArea.append("╔══════════════════════════════════════════════════════════╗\n");
        outputArea.append("               PERFORMANCE ANALYSIS REPORT\n");
        outputArea.append("╚══════════════════════════════════════════════════════════╝\n");
        outputArea.append(String.format(" ● Target Column : %s\n", selectedColumn.toUpperCase()));
        outputArea.append(String.format(" ● Dataset Size   : %d items\n", originalData.length));
        outputArea.append("────────────────────────────────────────────────────────────\n\n");

        java.util.Map<String, Long> performanceResults = new java.util.LinkedHashMap<>();

        try {
            // Execution with Progress Simulation (Logic from Member 02)
            runTest("Insertion Sort", () -> sort.SortingAlgorithms.insertionSort(originalData.clone()), performanceResults);
            runTest("Shell Sort", () -> sort.SortingAlgorithms.shellSort(originalData.clone()), performanceResults);
            runTest("Merge Sort", () -> sort.SortingAlgorithms.mergeSort(originalData.clone()), performanceResults);
            runTest("Quick Sort", () -> {
                double[] d = originalData.clone();
                sort.SortingAlgorithms.quickSort(d, 0, d.length - 1);
            }, performanceResults);
            runTest("Heap Sort", () -> sort.SortingAlgorithms.heapSort(originalData.clone()), performanceResults);

            // Display Results in a Table Format
            outputArea.append(String.format("   %-22s │  %-15s\n", "SORTING ALGORITHM", "TIME TAKEN"));
            outputArea.append(" ╼╼╼╼╼╼╼╼╼╼╼╼╼╼╼╼╼╼╼╼╼╼╼╼╼╼╼╼╼╼╼╼╼╼╼╼╼╼╼╼╼╼╼╼╼╼╼╼╼╼╼╼╼╼╼╼╼╼╼╼\n");

            String bestAlgo = "";
            long minTime = Long.MAX_VALUE;

            for (String algo : performanceResults.keySet()) {
                long timeTaken = performanceResults.get(algo);
                double timeInMs = timeTaken / 1_000_000.0;

                outputArea.append(String.format("   %-22s │  %10.4f ms\n", algo, timeInMs));

                if (timeTaken < minTime) {
                    minTime = timeTaken;
                    bestAlgo = algo;
                }
            }

            outputArea.append(" ────────────────────────────────────────────────────────────\n");
            outputArea.append(String.format(" ● BEST SORTING ALGORITHM: %s\n", bestAlgo.toUpperCase()));
            outputArea.append(" ────────────────────────────────────────────────────────────\n\n");

            // Sorted Data Preview Section
            outputArea.append("  SORTED PREVIEW (Ascending):\n   ");
            double[] previewData = originalData.clone();
            sort.SortingAlgorithms.quickSort(previewData, 0, previewData.length - 1);

            for (int i = 0; i < Math.min(8, previewData.length); i++) {
                outputArea.append(String.format("[%.2f] ", previewData[i]));
            }
            outputArea.append("...\n");
            outputArea.append("\n  Evaluation Complete.\n");

        } catch (Exception e) {
            showError("An error occurred during evaluation.");
        }
    }

    private void runTest(String name, Runnable algoTask, java.util.Map<String, Long> results) {
        long start = System.nanoTime();
        algoTask.run();
        results.put(name, System.nanoTime() - start);
    }

    private void showError(String msg) {
        JOptionPane.showMessageDialog(this, msg, "Application Notice", JOptionPane.ERROR_MESSAGE);
    }
}
