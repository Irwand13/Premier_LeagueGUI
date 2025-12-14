import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.io.BufferedReader;
import java.io.FileReader;

public class MainApp extends JFrame {

    JTable table;
    DefaultTableModel model;

    public MainApp() {
        setTitle("Premier League Klasemen");
        setSize(900, 500);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        // ===== PANEL BUTTON =====
        JPanel panelTop = new JPanel();

        JButton btn2013 = new JButton("Match ke 13");
        JButton btn2014 = new JButton("Match ke 14");
        JButton btn2015 = new JButton("Match ke 15");

        panelTop.add(btn2013);
        panelTop.add(btn2014);
        panelTop.add(btn2015);

        add(panelTop, BorderLayout.NORTH);

        // ===== TABLE =====
        model = new DefaultTableModel();
        table = new JTable(model);

        JScrollPane scrollPane = new JScrollPane(table);
        add(scrollPane, BorderLayout.CENTER);

        btn2013.addActionListener(e ->
                loadCSV("premier_league_table_13.csv"));

        btn2014.addActionListener(e ->
                loadCSV("premier_league_table_14.csv"));

        btn2015.addActionListener(e ->
                loadCSV("premier_league_table_15.csv"));
    }

    void loadCSV(String fileName) {
        model.setRowCount(0);
        model.setColumnCount(0);

        try (BufferedReader br = new BufferedReader(new FileReader(fileName))) {

            String line;
            boolean isHeader = true;

            while ((line = br.readLine()) != null) {
                String[] data = line.split(",");

                if (isHeader) {
                    for (String col : data) {
                        model.addColumn(col);
                    }
                    isHeader = false;
                } else {
                    model.addRow(data);
                }
            }

        } catch (Exception ex) {
            JOptionPane.showMessageDialog(
                    this,
                    "Gagal membaca file: " + fileName,
                    "Error",
                    JOptionPane.ERROR_MESSAGE
            );
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            new MainApp().setVisible(true);
        });
    }
}
