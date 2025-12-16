package org.example;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.io.BufferedReader;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;



public class MainApp extends JFrame {

    public static class TeamStats {
        int matchDay;
        int points;

        public TeamStats(int matchDay, int points) {
            this.matchDay = matchDay;
            this.points = points;
        }
        // Getter method untuk JFreeChart/Dialog
        public int getMatchDay() { return matchDay; }
        public int getPoints() { return points; }
    }

    JTable table;
    DefaultTableModel model;

    private Map<String, List<TeamStats>> consolidatedData = new HashMap<>();

    private final Map<Integer, String> CSV_FILES = Map.of(
            13, "premier_league_table_13.csv",
            14, "premier_league_table_14.csv",
            15, "premier_league_table_15.csv"
    );

    public
    MainApp() {
        // ... (Kode Constructor lainnya TIDAK DIUBAH) ...
        setTitle("Premier League Klasemen - Match 13 s.d. 15");
        setSize(1000, 600);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        loadAllData();

        JPanel panelTop = new JPanel();
        panelTop.setLayout(new FlowLayout(FlowLayout.CENTER, 10, 10));

        JButton btn13 = new JButton("Match ke 13");
        JButton btn14 = new JButton("Match ke 14");
        JButton btn15 = new JButton("Match ke 15");
        JButton btnChart = new JButton("Lihat Performa Tim Terpilih");
        btnChart.setEnabled(false);

        panelTop.add(btn13);
        panelTop.add(btn14);
        panelTop.add(btn15);
        panelTop.add(new JSeparator(SwingConstants.VERTICAL));
        panelTop.add(btnChart);

        add(panelTop, BorderLayout.NORTH);

        model = new DefaultTableModel();
        table = new JTable(model);

        table.getTableHeader().setBackground(new Color(0, 77, 153));
        table.getTableHeader().setForeground(new Color(0,22,22));
        table.getTableHeader().setFont(new Font("SansSerif", Font.BOLD, 14));
        table.setRowHeight(30);

        JScrollPane scrollPane = new JScrollPane(table);
        add(scrollPane, BorderLayout.CENTER);

        btn13.addActionListener(e -> loadTableView(13));
        btn14.addActionListener(e -> loadTableView(14));
        btn15.addActionListener(e -> loadTableView(15));

        table.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting() && table.getSelectedRow() != -1) {
                btnChart.setEnabled(true);
            } else {
                btnChart.setEnabled(false);
            }
        });

        btnChart.addActionListener(e -> {
            int selectedRow = table.getSelectedRow();
            if (selectedRow != -1) {
                // Kolom ke-1 di tabel adalah Nama Tim (setelah Posisi)
                // Posisi = kolom 0, Nama Tim = kolom 1
                String teamName = (String) model.getValueAt(selectedRow, 1);
                showTeamChart(teamName);
            }
        });

        loadTableView(15);
    }

    // PERBAIKAN UTAMA: Penanganan Index dan Error
    void loadAllData() {
        consolidatedData.clear();
        for (Map.Entry<Integer, String> entry : CSV_FILES.entrySet()) {
            int matchDay = entry.getKey();
            String fileName = entry.getValue();

            try (BufferedReader br = new BufferedReader(new FileReader(fileName))) {
                String line;
                br.readLine(); // Lewati header
                int MIN_COLUMNS = 9; // Tim,Played,Won,Drawn,Lost,Goals For,Goals Against,Goal Difference,Points

                while ((line = br.readLine()) != null) {
                    // Pastikan line tidak kosong
                    if (line.trim().isEmpty()) continue;

                    String[] data = line.split(",");

                    if (data.length < MIN_COLUMNS) {
                        System.err.println("Baris data tidak lengkap di file " + fileName + ": " + line);
                        continue;
                    }

                    try {
                        // Kolom Nama Tim ada di Indeks 0
                        String team = data[0].trim();

                        // Kolom Poin ada di Indeks 8 (Total 9 kolom, 0-8)
                        int points = Integer.parseInt(data[8].trim());

                        consolidatedData.computeIfAbsent(team, k -> new ArrayList<>()).add(new TeamStats(matchDay, points));

                    } catch (NumberFormatException e) {
                        System.err.println("Gagal parsing Poin di file " + fileName + " pada baris: " + line);
                    }
                }
            } catch (Exception ex) {
                System.err.println("Gagal memuat data histori dari: " + fileName + ". Error: " + ex.getMessage());
            }
        }
    }

    // ... (loadTableView TIDAK PERLU DIUBAH SIGNIFIKAN) ...
    void loadTableView(int matchDay) {
        String fileName = CSV_FILES.get(matchDay);

        model.setRowCount(0);
        model.setColumnCount(0);

        try (BufferedReader br = new BufferedReader(new FileReader(fileName))) {

            String line;
            boolean isHeader = true;

            model.addColumn("Posisi");

            while ((line = br.readLine()) != null) {
                if (line.trim().isEmpty()) continue;
                String[] data = line.split(",");

                if (isHeader) {
                    for (String col : data) {
                        model.addColumn(col.trim());
                    }
                    isHeader = false;
                } else {
                    Object[] rowData = new Object[data.length + 1];
                    rowData[0] = model.getRowCount() + 1;

                    for (int i = 0; i < data.length; i++) {
                        rowData[i+1] = data[i].trim();
                    }
                    model.addRow(rowData);
                }
            }
            setTitle("Premier League Klasemen - Match ke " + matchDay);

        } catch (Exception ex) {
            JOptionPane.showMessageDialog(
                    this,
                    "Gagal membaca file: " + fileName,
                    "Error",
                    JOptionPane.ERROR_MESSAGE
            );
        }
    }

    private void showTeamChart(String teamName) {
        List<TeamStats> stats = consolidatedData.get(teamName);
        if (stats == null || stats.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Data performa tim " + teamName + " tidak tersedia.", "Data Tidak Ditemukan", JOptionPane.WARNING_MESSAGE);
            return;
        }

        // JANGAN LUPA MENGURUTKAN DATA BERDASARKAN Match Day (penting untuk line chart)
        stats.sort((s1, s2) -> Integer.compare(s1.getMatchDay(), s2.getMatchDay()));

        new TeamChartDialog(this, teamName, stats).setVisible(true);
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            try {
                UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
            } catch (Exception e) {
                e.printStackTrace();
            }
            new MainApp().setVisible(true);
        });
    }
}