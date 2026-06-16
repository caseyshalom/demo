package com.shiftscheduler.ui;

import com.shiftscheduler.dao.CutiDAO;
import com.shiftscheduler.dao.JadwalDAO;
import com.shiftscheduler.dao.KaryawanDAO;
import com.shiftscheduler.dao.ShiftDAO;
import com.shiftscheduler.model.JadwalHasil;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.List;

/**
 * Panel Dashboard — layar beranda yang menampilkan metrik operasional
 * dan tabel jadwal hari ini (read-only).
 */
public class DashboardPanel extends JPanel {

    // DAO instances
    private final KaryawanDAO karyawanDAO;
    private final ShiftDAO shiftDAO;
    private final CutiDAO cutiDAO;
    private final JadwalDAO jadwalDAO;

    // Komponen metrik
    private JLabel lblKaryawanAktif;
    private JLabel lblKuotaShift;
    private JLabel lblKaryawanCuti;

    // Tabel jadwal hari ini
    private DefaultTableModel tableModel;
    private JTable table;

    // Warna tema
    private static final Color BG_COLOR = new Color(38, 42, 52);
    private static final Color CARD_BG = new Color(48, 53, 65);
    private static final Color CARD_BORDER = new Color(60, 65, 78);
    private static final Color ACCENT_BLUE = new Color(59, 130, 246);
    private static final Color ACCENT_GREEN = new Color(34, 197, 94);
    private static final Color ACCENT_ORANGE = new Color(249, 115, 22);

    public DashboardPanel() {
        this.karyawanDAO = new KaryawanDAO();
        this.shiftDAO = new ShiftDAO();
        this.cutiDAO = new CutiDAO();
        this.jadwalDAO = new JadwalDAO();

        setBackground(BG_COLOR);
        setLayout(new BorderLayout(0, 15));
        setBorder(new EmptyBorder(20, 25, 20, 25));

        initComponents();
        refreshData();
    }

    private void initComponents() {
        // === HEADER ===
        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setOpaque(false);

        JLabel titleLabel = new JLabel("Dashboard");
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 24));
        titleLabel.setForeground(Color.WHITE);

        JLabel subtitleLabel = new JLabel("Ringkasan operasional hari ini — " + LocalDate.now());
        subtitleLabel.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        subtitleLabel.setForeground(new Color(160, 165, 175));

        JPanel titleGroup = new JPanel();
        titleGroup.setLayout(new BoxLayout(titleGroup, BoxLayout.Y_AXIS));
        titleGroup.setOpaque(false);
        titleGroup.add(titleLabel);
        titleGroup.add(Box.createVerticalStrut(4));
        titleGroup.add(subtitleLabel);

        headerPanel.add(titleGroup, BorderLayout.WEST);
        add(headerPanel, BorderLayout.NORTH);

        // === KONTEN UTAMA ===
        JPanel mainContent = new JPanel(new BorderLayout(0, 15));
        mainContent.setOpaque(false);

        // --- Kartu Metrik ---
        JPanel metricsPanel = new JPanel(new GridLayout(1, 3, 15, 0));
        metricsPanel.setOpaque(false);
        metricsPanel.setMaximumSize(new Dimension(Integer.MAX_VALUE, 100));

        lblKaryawanAktif = new JLabel("0");
        metricsPanel.add(createMetricCard("Karyawan Aktif", lblKaryawanAktif, "👤", ACCENT_BLUE));

        lblKuotaShift = new JLabel("0");
        metricsPanel.add(createMetricCard("Total Kuota Shift", lblKuotaShift, "🕐", ACCENT_GREEN));

        lblKaryawanCuti = new JLabel("0");
        metricsPanel.add(createMetricCard("Karyawan Cuti Hari Ini", lblKaryawanCuti, "📋", ACCENT_ORANGE));

        mainContent.add(metricsPanel, BorderLayout.NORTH);

        // --- Tabel Jadwal Hari Ini ---
        JPanel tablePanel = createTablePanel();
        mainContent.add(tablePanel, BorderLayout.CENTER);

        add(mainContent, BorderLayout.CENTER);
    }

    /**
     * Membuat kartu metrik dengan ikon, label, dan nilai.
     */
    private JPanel createMetricCard(String title, JLabel valueLabel, String icon, Color accentColor) {
        JPanel card = new JPanel(new BorderLayout(10, 0));
        card.setBackground(CARD_BG);
        card.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(CARD_BORDER, 1),
            new EmptyBorder(18, 20, 18, 20)
        ));

        // Ikon
        JLabel iconLabel = new JLabel(icon);
        iconLabel.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 28));
        card.add(iconLabel, BorderLayout.WEST);

        // Teks
        JPanel textPanel = new JPanel();
        textPanel.setLayout(new BoxLayout(textPanel, BoxLayout.Y_AXIS));
        textPanel.setOpaque(false);

        JLabel titleLbl = new JLabel(title);
        titleLbl.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        titleLbl.setForeground(new Color(160, 165, 175));

        valueLabel.setFont(new Font("Segoe UI", Font.BOLD, 28));
        valueLabel.setForeground(accentColor);

        textPanel.add(titleLbl);
        textPanel.add(Box.createVerticalStrut(4));
        textPanel.add(valueLabel);

        card.add(textPanel, BorderLayout.CENTER);

        return card;
    }

    /**
     * Membuat panel tabel jadwal hari ini.
     */
    private JPanel createTablePanel() {
        JPanel panel = new JPanel(new BorderLayout(0, 10));
        panel.setOpaque(false);

        JLabel tableTitle = new JLabel("📅 Jadwal Hari Ini");
        tableTitle.setFont(new Font("Segoe UI", Font.BOLD, 16));
        tableTitle.setForeground(Color.WHITE);
        panel.add(tableTitle, BorderLayout.NORTH);

        // Tabel
        String[] columns = {"No.", "Nama Karyawan", "Posisi", "Kode Shift", "Jam Mulai", "Jam Selesai"};
        tableModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false; // Read-only
            }
        };

        table = new JTable(tableModel);
        table.setRowHeight(32);
        table.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        table.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 13));
        table.setSelectionBackground(ACCENT_BLUE);
        table.setSelectionForeground(Color.WHITE);
        table.setGridColor(CARD_BORDER);

        // Atur lebar kolom
        table.getColumnModel().getColumn(0).setMaxWidth(50);
        table.getColumnModel().getColumn(3).setMaxWidth(100);
        table.getColumnModel().getColumn(4).setMaxWidth(100);
        table.getColumnModel().getColumn(5).setMaxWidth(100);

        // Center alignment untuk beberapa kolom
        DefaultTableCellRenderer centerRenderer = new DefaultTableCellRenderer();
        centerRenderer.setHorizontalAlignment(SwingConstants.CENTER);
        table.getColumnModel().getColumn(0).setCellRenderer(centerRenderer);
        table.getColumnModel().getColumn(3).setCellRenderer(centerRenderer);
        table.getColumnModel().getColumn(4).setCellRenderer(centerRenderer);
        table.getColumnModel().getColumn(5).setCellRenderer(centerRenderer);

        JScrollPane scrollPane = new JScrollPane(table);
        scrollPane.setBorder(BorderFactory.createLineBorder(CARD_BORDER, 1));
        scrollPane.getViewport().setBackground(CARD_BG);

        panel.add(scrollPane, BorderLayout.CENTER);

        return panel;
    }

    /**
     * Memuat ulang semua data dari database dan mengupdate tampilan.
     */
    public void refreshData() {
        try {
            // Update metrik
            int jumlahKaryawan = karyawanDAO.countAll();
            int totalKuota = shiftDAO.getTotalKuota();
            int jumlahCuti = cutiDAO.countCutiHariIni();

            lblKaryawanAktif.setText(String.valueOf(jumlahKaryawan));
            lblKuotaShift.setText(String.valueOf(totalKuota));
            lblKaryawanCuti.setText(String.valueOf(jumlahCuti));

            // Update tabel jadwal hari ini
            tableModel.setRowCount(0);
            List<JadwalHasil> jadwalHariIni = jadwalDAO.getByDate(LocalDate.now());

            int no = 1;
            for (JadwalHasil j : jadwalHariIni) {
                tableModel.addRow(new Object[]{
                    no++,
                    j.getNamaKaryawan(),
                    j.getPosisiKaryawan(),
                    j.getKodeShift(),
                    j.getJamMulai(),
                    j.getJamSelesai()
                });
            }

            if (jadwalHariIni.isEmpty()) {
                tableModel.addRow(new Object[]{"", "Belum ada jadwal untuk hari ini", "", "", "", ""});
            }

        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this,
                "Gagal memuat data dashboard: " + e.getMessage(),
                "Error Database", JOptionPane.ERROR_MESSAGE);
        }
    }
}
