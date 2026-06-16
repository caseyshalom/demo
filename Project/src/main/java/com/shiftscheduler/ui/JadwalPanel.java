package com.shiftscheduler.ui;

import com.shiftscheduler.dao.JadwalDAO;
import com.shiftscheduler.dao.KaryawanDAO;
import com.shiftscheduler.dao.ShiftDAO;
import com.shiftscheduler.engine.JadwalEngine;
import com.shiftscheduler.model.JadwalHasil;
import com.shiftscheduler.model.Karyawan;
import com.shiftscheduler.model.Shift;
import com.shiftscheduler.service.ExportService;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.io.File;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.List;

/**
 * Panel Jadwal — modul paling kompleks.
 * 
 * Fitur:
 * 1. Generator: Input rentang tanggal → jalankan JadwalEngine via SwingWorker
 * 2. Viewer: Tabel matriks (Karyawan × Tanggal) dengan kode shift
 * 3. Editor: Klik sel → dropdown pilih shift (JComboBox cell editor)
 * 4. Ekspor: CSV dan PDF via ExportService
 */
public class JadwalPanel extends JPanel {

    private final JadwalDAO jadwalDAO;
    private final KaryawanDAO karyawanDAO;
    private final ShiftDAO shiftDAO;
    private final ExportService exportService;

    // Generator controls
    private JSpinner spnTanggalMulai;
    private JSpinner spnTanggalSelesai;
    private JButton btnGenerate;
    private JButton btnExportCSV;
    private JButton btnExportPDF;
    private JProgressBar progressBar;
    private JLabel lblStatus;

    // Viewer tabel matriks
    private DefaultTableModel matrixTableModel;
    private JTable matrixTable;

    // Detail tabel (view list)
    private DefaultTableModel detailTableModel;
    private JTable detailTable;

    // Data cache
    private List<Karyawan> daftarKaryawan;
    private List<Shift> daftarShift;
    private List<JadwalHasil> currentJadwal;

    // Warna tema
    private static final Color BG_COLOR = new Color(38, 42, 52);
    private static final Color CARD_BG = new Color(48, 53, 65);
    private static final Color CARD_BORDER = new Color(60, 65, 78);
    private static final Color ACCENT_BLUE = new Color(59, 130, 246);
    private static final Color ACCENT_GREEN = new Color(34, 197, 94);
    private static final Color ACCENT_PURPLE = new Color(139, 92, 246);

    private static final DateTimeFormatter DATE_FORMAT = DateTimeFormatter.ofPattern("dd/MM/yyyy");
    private static final DateTimeFormatter DATE_SHORT = DateTimeFormatter.ofPattern("dd/MM");

    public JadwalPanel() {
        this.jadwalDAO = new JadwalDAO();
        this.karyawanDAO = new KaryawanDAO();
        this.shiftDAO = new ShiftDAO();
        this.exportService = new ExportService();
        this.currentJadwal = new ArrayList<>();

        setBackground(BG_COLOR);
        setLayout(new BorderLayout(0, 10));
        setBorder(new EmptyBorder(20, 25, 20, 25));

        initComponents();
        refreshData();
    }

    private void initComponents() {
        // === HEADER ===
        JLabel titleLabel = new JLabel("📅 Generator & Viewer Jadwal");
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 24));
        titleLabel.setForeground(Color.WHITE);
        add(titleLabel, BorderLayout.NORTH);

        JPanel mainPanel = new JPanel(new BorderLayout(0, 10));
        mainPanel.setOpaque(false);

        // --- Generator Panel (top) ---
        mainPanel.add(createGeneratorPanel(), BorderLayout.NORTH);

        // --- Tabbed Pane: Matriks + Detail ---
        JTabbedPane tabbedPane = new JTabbedPane();
        tabbedPane.setFont(new Font("Segoe UI", Font.BOLD, 13));

        tabbedPane.addTab("📊 Tampilan Matriks", createMatrixPanel());
        tabbedPane.addTab("📋 Tampilan Detail", createDetailPanel());

        mainPanel.add(tabbedPane, BorderLayout.CENTER);

        add(mainPanel, BorderLayout.CENTER);
    }

    /**
     * Panel generator: input tanggal, tombol generate, progress bar, dan tombol ekspor.
     */
    private JPanel createGeneratorPanel() {
        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBackground(CARD_BG);
        panel.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(CARD_BORDER, 1),
            new EmptyBorder(12, 20, 12, 20)
        ));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(4, 8, 4, 8);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        // Baris 1: Input tanggal + tombol
        gbc.gridy = 0;

        gbc.gridx = 0; gbc.weightx = 0;
        panel.add(createLabel("Tanggal Mulai"), gbc);
        gbc.gridx = 1; gbc.weightx = 0.3;
        spnTanggalMulai = createDateSpinner();
        panel.add(spnTanggalMulai, gbc);

        gbc.gridx = 2; gbc.weightx = 0;
        panel.add(createLabel("Tanggal Selesai"), gbc);
        gbc.gridx = 3; gbc.weightx = 0.3;
        spnTanggalSelesai = createDateSpinner();
        // Default: set tanggal selesai +6 hari (1 minggu)
        Calendar cal = Calendar.getInstance();
        cal.add(Calendar.DAY_OF_MONTH, 6);
        spnTanggalSelesai.setValue(cal.getTime());
        panel.add(spnTanggalSelesai, gbc);

        gbc.gridx = 4; gbc.weightx = 0;
        btnGenerate = createStyledButton("⚡ Generate Jadwal", ACCENT_BLUE);
        panel.add(btnGenerate, gbc);

        gbc.gridx = 5;
        btnExportCSV = createStyledButton("📄 Ekspor CSV", ACCENT_GREEN);
        btnExportCSV.setEnabled(false);
        panel.add(btnExportCSV, gbc);

        gbc.gridx = 6;
        btnExportPDF = createStyledButton("📑 Ekspor PDF", ACCENT_PURPLE);
        btnExportPDF.setEnabled(false);
        panel.add(btnExportPDF, gbc);

        // Baris 2: Progress bar + status
        gbc.gridy = 1;
        gbc.gridx = 0; gbc.gridwidth = 5; gbc.weightx = 1;
        progressBar = new JProgressBar(0, 100);
        progressBar.setStringPainted(true);
        progressBar.setPreferredSize(new Dimension(0, 22));
        progressBar.setVisible(false);
        panel.add(progressBar, gbc);

        gbc.gridx = 5; gbc.gridwidth = 2; gbc.weightx = 0;
        lblStatus = new JLabel("Siap");
        lblStatus.setFont(new Font("Segoe UI", Font.ITALIC, 11));
        lblStatus.setForeground(new Color(160, 165, 175));
        panel.add(lblStatus, gbc);

        // Event listeners
        btnGenerate.addActionListener(e -> generateJadwal());
        btnExportCSV.addActionListener(e -> exportCSV());
        btnExportPDF.addActionListener(e -> exportPDF());

        return panel;
    }

    /**
     * Tabel matriks: Baris = Karyawan, Kolom = Tanggal, Sel = Kode Shift
     */
    private JPanel createMatrixPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setOpaque(false);

        matrixTableModel = new DefaultTableModel() {
            @Override
            public boolean isCellEditable(int row, int column) {
                // Kolom 0 (Nama) tidak bisa diedit, kolom tanggal bisa diedit
                return column > 0;
            }
        };

        matrixTable = new JTable(matrixTableModel);
        matrixTable.setRowHeight(30);
        matrixTable.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        matrixTable.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 11));
        matrixTable.setGridColor(CARD_BORDER);
        matrixTable.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);

        // Custom renderer untuk warna sel berdasarkan shift
        matrixTable.setDefaultRenderer(Object.class, new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable table, Object value,
                    boolean isSelected, boolean hasFocus, int row, int column) {
                Component c = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
                setHorizontalAlignment(column == 0 ? SwingConstants.LEFT : SwingConstants.CENTER);

                if (!isSelected && column > 0 && value != null) {
                    String val = value.toString();
                    switch (val) {
                        case "P" -> c.setBackground(new Color(255, 243, 205)); // kuning
                        case "S" -> c.setBackground(new Color(209, 236, 241)); // biru
                        case "M" -> c.setBackground(new Color(215, 204, 232)); // ungu
                        default -> c.setBackground(CARD_BG);
                    }
                    c.setForeground(new Color(30, 30, 30));
                } else if (!isSelected) {
                    c.setBackground(CARD_BG);
                    c.setForeground(Color.WHITE);
                }

                return c;
            }
        });

        JScrollPane scrollPane = new JScrollPane(matrixTable,
            JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED,
            JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
        scrollPane.setBorder(BorderFactory.createLineBorder(CARD_BORDER, 1));
        scrollPane.getViewport().setBackground(CARD_BG);
        panel.add(scrollPane, BorderLayout.CENTER);

        // Tombol simpan perubahan
        JPanel bottomPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        bottomPanel.setOpaque(false);
        JButton btnSimpan = createStyledButton("💾 Simpan Perubahan Manual", ACCENT_BLUE);
        btnSimpan.addActionListener(e -> simpanPerubahanManual());
        bottomPanel.add(btnSimpan);
        panel.add(bottomPanel, BorderLayout.SOUTH);

        return panel;
    }

    /**
     * Tabel detail: list view semua jadwal dengan kolom lengkap.
     */
    private JPanel createDetailPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setOpaque(false);

        String[] columns = {"No.", "Tanggal", "Nama Karyawan", "Posisi", "Kode Shift", "Jam Mulai", "Jam Selesai"};
        detailTableModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        detailTable = new JTable(detailTableModel);
        detailTable.setRowHeight(30);
        detailTable.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        detailTable.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 13));
        detailTable.setSelectionBackground(ACCENT_BLUE);
        detailTable.setSelectionForeground(Color.WHITE);
        detailTable.setGridColor(CARD_BORDER);

        detailTable.getColumnModel().getColumn(0).setMaxWidth(50);

        DefaultTableCellRenderer centerRenderer = new DefaultTableCellRenderer();
        centerRenderer.setHorizontalAlignment(SwingConstants.CENTER);
        detailTable.getColumnModel().getColumn(0).setCellRenderer(centerRenderer);
        detailTable.getColumnModel().getColumn(1).setCellRenderer(centerRenderer);
        detailTable.getColumnModel().getColumn(4).setCellRenderer(centerRenderer);
        detailTable.getColumnModel().getColumn(5).setCellRenderer(centerRenderer);
        detailTable.getColumnModel().getColumn(6).setCellRenderer(centerRenderer);

        JScrollPane scrollPane = new JScrollPane(detailTable);
        scrollPane.setBorder(BorderFactory.createLineBorder(CARD_BORDER, 1));
        scrollPane.getViewport().setBackground(CARD_BG);
        panel.add(scrollPane, BorderLayout.CENTER);

        return panel;
    }

    // ==================== GENERATE JADWAL ====================

    /**
     * Menjalankan algoritma backtracking via SwingWorker (asinkron).
     */
    private void generateJadwal() {
        LocalDate mulai = getDateFromSpinner(spnTanggalMulai);
        LocalDate selesai = getDateFromSpinner(spnTanggalSelesai);

        if (selesai.isBefore(mulai)) {
            JOptionPane.showMessageDialog(this, "Tanggal selesai harus setelah tanggal mulai!",
                "Validasi", JOptionPane.WARNING_MESSAGE);
            return;
        }

        // Konfirmasi jika sudah ada jadwal di rentang ini
        try {
            List<JadwalHasil> existing = jadwalDAO.getByDateRange(mulai, selesai);
            if (!existing.isEmpty()) {
                int confirm = JOptionPane.showConfirmDialog(this,
                    "Sudah ada " + existing.size() + " jadwal di rentang tanggal ini.\n" +
                    "Jadwal lama akan dihapus dan diganti dengan jadwal baru.\nLanjutkan?",
                    "Konfirmasi", JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);
                if (confirm != JOptionPane.YES_OPTION) return;

                jadwalDAO.deleteByDateRange(mulai, selesai);
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Error mengecek jadwal existing: " + e.getMessage(),
                "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        // Load data karyawan dan shift
        try {
            daftarKaryawan = karyawanDAO.getAll();
            daftarShift = shiftDAO.getAll();
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Gagal memuat data: " + e.getMessage(),
                "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        if (daftarKaryawan.isEmpty() || daftarShift.isEmpty()) {
            JOptionPane.showMessageDialog(this,
                "Data karyawan atau shift kosong! Tambahkan data terlebih dahulu.",
                "Validasi", JOptionPane.WARNING_MESSAGE);
            return;
        }

        // Setup UI state
        btnGenerate.setEnabled(false);
        progressBar.setVisible(true);
        progressBar.setValue(0);
        lblStatus.setText("Menjalankan algoritma backtracking...");

        // Jalankan engine di background thread via SwingWorker
        SwingWorker<List<JadwalHasil>, String> worker = new SwingWorker<>() {

            @Override
            protected List<JadwalHasil> doInBackground() {
                JadwalEngine engine = new JadwalEngine(daftarKaryawan, daftarShift);
                engine.setCallback(new JadwalEngine.EngineCallback() {
                    @Override
                    public void onProgress(int persen, String pesan) {
                        // Update UI dari background thread via publish
                        publish(persen + "|" + pesan);
                    }

                    @Override
                    public void onComplete(List<JadwalHasil> hasil) {
                        // Handled in done()
                    }

                    @Override
                    public void onFailed(String alasan) {
                        publish("-1|" + alasan);
                    }
                });

                return engine.generate(mulai, selesai);
            }

            @Override
            protected void process(List<String> chunks) {
                for (String chunk : chunks) {
                    String[] parts = chunk.split("\\|", 2);
                    int persen = Integer.parseInt(parts[0]);
                    String pesan = parts.length > 1 ? parts[1] : "";

                    if (persen >= 0) {
                        progressBar.setValue(persen);
                        lblStatus.setText(pesan);
                    } else {
                        lblStatus.setText("GAGAL: " + pesan);
                        lblStatus.setForeground(new Color(239, 68, 68));
                    }
                }
            }

            @Override
            protected void done() {
                btnGenerate.setEnabled(true);

                try {
                    List<JadwalHasil> hasil = get();

                    if (hasil.isEmpty()) {
                        progressBar.setValue(0);
                        lblStatus.setText("Penjadwalan gagal — tidak ditemukan solusi valid.");
                        lblStatus.setForeground(new Color(239, 68, 68));
                        JOptionPane.showMessageDialog(JadwalPanel.this,
                            "Algoritma tidak menemukan solusi yang valid.\n" +
                            "Periksa apakah jumlah karyawan mencukupi untuk mengisi semua kuota shift,\n" +
                            "atau apakah terlalu banyak karyawan yang cuti.",
                            "Penjadwalan Gagal", JOptionPane.WARNING_MESSAGE);
                    } else {
                        // Simpan ke database
                        int inserted = jadwalDAO.insertBatch(hasil);
                        progressBar.setValue(100);
                        lblStatus.setText("Berhasil! " + inserted + " jadwal tersimpan ke database.");
                        lblStatus.setForeground(ACCENT_GREEN);

                        // Refresh tampilan
                        loadJadwal(mulai, selesai);

                        btnExportCSV.setEnabled(true);
                        btnExportPDF.setEnabled(true);

                        JOptionPane.showMessageDialog(JadwalPanel.this,
                            "Penjadwalan berhasil!\nTotal " + inserted + " jadwal telah dibuat dan disimpan.",
                            "Sukses", JOptionPane.INFORMATION_MESSAGE);
                    }
                } catch (Exception e) {
                    progressBar.setValue(0);
                    lblStatus.setText("Error: " + e.getMessage());
                    lblStatus.setForeground(new Color(239, 68, 68));
                    JOptionPane.showMessageDialog(JadwalPanel.this,
                        "Error saat penjadwalan: " + e.getMessage(),
                        "Error", JOptionPane.ERROR_MESSAGE);
                }
            }
        };

        worker.execute();
    }

    // ==================== LOAD & DISPLAY JADWAL ====================

    /**
     * Memuat jadwal dari database dan menampilkan di kedua tabel.
     */
    private void loadJadwal(LocalDate mulai, LocalDate selesai) {
        try {
            currentJadwal = jadwalDAO.getByDateRange(mulai, selesai);
            if (daftarKaryawan == null) daftarKaryawan = karyawanDAO.getAll();
            if (daftarShift == null) daftarShift = shiftDAO.getAll();

            updateMatrixTable(mulai, selesai);
            updateDetailTable();
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Gagal memuat jadwal: " + e.getMessage(),
                "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    /**
     * Update tampilan tabel matriks.
     */
    private void updateMatrixTable(LocalDate mulai, LocalDate selesai) {
        // Bangun daftar tanggal
        List<LocalDate> daftarTanggal = new ArrayList<>();
        LocalDate curr = mulai;
        while (!curr.isAfter(selesai)) {
            daftarTanggal.add(curr);
            curr = curr.plusDays(1);
        }

        // Bangun kolom: "Karyawan" + tanggal-tanggal
        Vector<String> columnNames = new Vector<>();
        columnNames.add("Karyawan");
        for (LocalDate tgl : daftarTanggal) {
            columnNames.add(tgl.format(DATE_SHORT));
        }

        // Bangun map lookup: (idKaryawan, tanggal) → kodeShift
        Map<String, String> jadwalMap = new HashMap<>();
        for (JadwalHasil j : currentJadwal) {
            jadwalMap.put(j.getIdKaryawan() + "-" + j.getTanggal(), j.getKodeShift());
        }

        // Bangun data rows
        Vector<Vector<Object>> data = new Vector<>();
        for (Karyawan k : daftarKaryawan) {
            Vector<Object> row = new Vector<>();
            row.add(k.getNama());
            for (LocalDate tgl : daftarTanggal) {
                String kode = jadwalMap.getOrDefault(k.getId() + "-" + tgl, "-");
                row.add(kode);
            }
            data.add(row);
        }

        matrixTableModel.setDataVector(data, columnNames);

        // Set column widths
        if (matrixTable.getColumnCount() > 0) {
            matrixTable.getColumnModel().getColumn(0).setPreferredWidth(150);
            for (int i = 1; i < matrixTable.getColumnCount(); i++) {
                matrixTable.getColumnModel().getColumn(i).setPreferredWidth(60);

                // Set JComboBox editor untuk kolom tanggal
                JComboBox<String> comboBox = new JComboBox<>();
                comboBox.addItem("-");
                for (Shift s : daftarShift) {
                    comboBox.addItem(s.getKodeShift());
                }
                matrixTable.getColumnModel().getColumn(i).setCellEditor(new DefaultCellEditor(comboBox));
            }
        }
    }

    /**
     * Update tampilan tabel detail (list view).
     */
    private void updateDetailTable() {
        detailTableModel.setRowCount(0);
        int no = 1;
        for (JadwalHasil j : currentJadwal) {
            detailTableModel.addRow(new Object[]{
                no++,
                j.getTanggal().format(DATE_FORMAT),
                j.getNamaKaryawan(),
                j.getPosisiKaryawan(),
                j.getKodeShift(),
                j.getJamMulai(),
                j.getJamSelesai()
            });
        }
    }

    // ==================== EDIT MANUAL ====================

    /**
     * Menyimpan perubahan yang dilakukan secara manual di tabel matriks.
     */
    private void simpanPerubahanManual() {
        if (daftarKaryawan == null || daftarShift == null || matrixTable.getColumnCount() <= 1) {
            JOptionPane.showMessageDialog(this, "Belum ada jadwal yang ditampilkan.",
                "Info", JOptionPane.INFORMATION_MESSAGE);
            return;
        }

        // Stop editing jika masih aktif
        if (matrixTable.isEditing()) {
            matrixTable.getCellEditor().stopCellEditing();
        }

        LocalDate mulai = getDateFromSpinner(spnTanggalMulai);

        // Build map kodeShift → idShift
        Map<String, Integer> shiftMap = new HashMap<>();
        for (Shift s : daftarShift) {
            shiftMap.put(s.getKodeShift(), s.getId());
        }

        List<JadwalHasil> newJadwal = new ArrayList<>();

        for (int row = 0; row < matrixTableModel.getRowCount(); row++) {
            Karyawan k = daftarKaryawan.get(row);
            for (int col = 1; col < matrixTableModel.getColumnCount(); col++) {
                String kode = (String) matrixTableModel.getValueAt(row, col);
                LocalDate tgl = mulai.plusDays(col - 1);

                if (kode != null && !"-".equals(kode) && shiftMap.containsKey(kode)) {
                    newJadwal.add(new JadwalHasil(tgl, k.getId(), shiftMap.get(kode)));
                }
            }
        }

        try {
            // Delete existing dan insert ulang
            LocalDate selesai = getDateFromSpinner(spnTanggalSelesai);
            jadwalDAO.deleteByDateRange(mulai, selesai);
            int inserted = jadwalDAO.insertBatch(newJadwal);

            JOptionPane.showMessageDialog(this,
                "Perubahan berhasil disimpan! (" + inserted + " jadwal)",
                "Sukses", JOptionPane.INFORMATION_MESSAGE);

            loadJadwal(mulai, selesai);
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Gagal menyimpan perubahan: " + e.getMessage(),
                "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    // ==================== EXPORT ====================

    private void exportCSV() {
        if (currentJadwal.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Tidak ada jadwal untuk diekspor.",
                "Info", JOptionPane.INFORMATION_MESSAGE);
            return;
        }

        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setDialogTitle("Simpan File CSV");
        fileChooser.setSelectedFile(new File("jadwal_shift.csv"));
        fileChooser.setFileFilter(new javax.swing.filechooser.FileNameExtensionFilter("CSV Files", "csv"));

        if (fileChooser.showSaveDialog(this) == JFileChooser.APPROVE_OPTION) {
            File file = fileChooser.getSelectedFile();
            if (!file.getName().endsWith(".csv")) {
                file = new File(file.getAbsolutePath() + ".csv");
            }

            try {
                exportService.exportCSV(currentJadwal, file);
                JOptionPane.showMessageDialog(this,
                    "File CSV berhasil disimpan ke:\n" + file.getAbsolutePath(),
                    "Ekspor Berhasil", JOptionPane.INFORMATION_MESSAGE);
            } catch (Exception e) {
                JOptionPane.showMessageDialog(this, "Gagal mengekspor CSV: " + e.getMessage(),
                    "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void exportPDF() {
        if (currentJadwal.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Tidak ada jadwal untuk diekspor.",
                "Info", JOptionPane.INFORMATION_MESSAGE);
            return;
        }

        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setDialogTitle("Simpan File PDF");
        fileChooser.setSelectedFile(new File("jadwal_shift.pdf"));
        fileChooser.setFileFilter(new javax.swing.filechooser.FileNameExtensionFilter("PDF Files", "pdf"));

        if (fileChooser.showSaveDialog(this) == JFileChooser.APPROVE_OPTION) {
            File file = fileChooser.getSelectedFile();
            if (!file.getName().endsWith(".pdf")) {
                file = new File(file.getAbsolutePath() + ".pdf");
            }

            try {
                exportService.exportPDF(currentJadwal, file, daftarKaryawan, daftarShift);
                JOptionPane.showMessageDialog(this,
                    "File PDF berhasil disimpan ke:\n" + file.getAbsolutePath(),
                    "Ekspor Berhasil", JOptionPane.INFORMATION_MESSAGE);
            } catch (Exception e) {
                JOptionPane.showMessageDialog(this, "Gagal mengekspor PDF: " + e.getMessage(),
                    "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    // ==================== REFRESH ====================

    /**
     * Refresh data — dipanggil saat panel ditampilkan.
     */
    public void refreshData() {
        lblStatus.setForeground(new Color(160, 165, 175));
        try {
            daftarKaryawan = karyawanDAO.getAll();
            daftarShift = shiftDAO.getAll();
        } catch (SQLException e) {
            // Silently handle
        }
    }

    // ==================== HELPERS ====================

    private JSpinner createDateSpinner() {
        SpinnerDateModel model = new SpinnerDateModel();
        JSpinner spinner = new JSpinner(model);
        JSpinner.DateEditor editor = new JSpinner.DateEditor(spinner, "dd/MM/yyyy");
        spinner.setEditor(editor);
        spinner.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        return spinner;
    }

    private LocalDate getDateFromSpinner(JSpinner spinner) {
        Date date = (Date) spinner.getValue();
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        return LocalDate.of(cal.get(Calendar.YEAR), cal.get(Calendar.MONTH) + 1, cal.get(Calendar.DAY_OF_MONTH));
    }

    private JLabel createLabel(String text) {
        JLabel label = new JLabel(text);
        label.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        label.setForeground(new Color(200, 200, 210));
        return label;
    }

    private JButton createStyledButton(String text, Color bgColor) {
        JButton btn = new JButton(text);
        btn.setFont(new Font("Segoe UI", Font.BOLD, 12));
        btn.setBackground(bgColor);
        btn.setForeground(Color.WHITE);
        btn.setFocusPainted(false);
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btn.setBorder(new EmptyBorder(8, 16, 8, 16));
        return btn;
    }
}
