package com.shiftscheduler.ui;

import com.shiftscheduler.dao.CutiDAO;
import com.shiftscheduler.dao.KaryawanDAO;
import com.shiftscheduler.model.Karyawan;
import com.shiftscheduler.model.KaryawanCuti;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

/**
 * Panel CRUD untuk manajemen data Cuti/Izin Karyawan.
 * Menggunakan JComboBox untuk memilih karyawan dan JSpinner untuk date picker.
 */
public class CutiPanel extends JPanel {

    private final CutiDAO cutiDAO;
    private final KaryawanDAO karyawanDAO;

    // Form fields
    private JComboBox<KaryawanItem> cbKaryawan;
    private JSpinner spnTanggalMulai;
    private JSpinner spnTanggalSelesai;
    private JTextField txtKeterangan;
    private JButton btnTambah, btnEdit, btnHapus, btnBersihkan;

    // Tabel
    private DefaultTableModel tableModel;
    private JTable table;

    // ID cuti yang sedang diedit
    private int selectedId = 0;

    // Warna tema
    private static final Color BG_COLOR = new Color(38, 42, 52);
    private static final Color CARD_BG = new Color(48, 53, 65);
    private static final Color CARD_BORDER = new Color(60, 65, 78);
    private static final Color ACCENT_BLUE = new Color(59, 130, 246);
    private static final Color ACCENT_RED = new Color(239, 68, 68);
    private static final Color ACCENT_GREEN = new Color(34, 197, 94);

    private static final DateTimeFormatter DATE_FORMAT = DateTimeFormatter.ofPattern("dd/MM/yyyy");

    public CutiPanel() {
        this.cutiDAO = new CutiDAO();
        this.karyawanDAO = new KaryawanDAO();

        setBackground(BG_COLOR);
        setLayout(new BorderLayout(0, 15));
        setBorder(new EmptyBorder(20, 25, 20, 25));

        initComponents();
        refreshData();
    }

    private void initComponents() {
        JLabel titleLabel = new JLabel("📋 Manajemen Cuti / Izin");
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 24));
        titleLabel.setForeground(Color.WHITE);
        add(titleLabel, BorderLayout.NORTH);

        JPanel mainPanel = new JPanel(new BorderLayout(0, 15));
        mainPanel.setOpaque(false);
        mainPanel.add(createFormPanel(), BorderLayout.NORTH);
        mainPanel.add(createTablePanel(), BorderLayout.CENTER);
        add(mainPanel, BorderLayout.CENTER);
    }

    private JPanel createFormPanel() {
        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBackground(CARD_BG);
        panel.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(CARD_BORDER, 1),
            new EmptyBorder(15, 20, 15, 20)
        ));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(6, 8, 6, 8);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        // Baris 1: Karyawan + Tanggal
        gbc.gridy = 0;

        gbc.gridx = 0; gbc.weightx = 0;
        panel.add(createLabel("Karyawan"), gbc);
        gbc.gridx = 1; gbc.weightx = 1;
        cbKaryawan = new JComboBox<>();
        cbKaryawan.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        loadKaryawanComboBox();
        panel.add(cbKaryawan, gbc);

        gbc.gridx = 2; gbc.weightx = 0;
        panel.add(createLabel("Tanggal Mulai"), gbc);
        gbc.gridx = 3; gbc.weightx = 0.5;
        spnTanggalMulai = createDateSpinner();
        panel.add(spnTanggalMulai, gbc);

        gbc.gridx = 4; gbc.weightx = 0;
        panel.add(createLabel("Tanggal Selesai"), gbc);
        gbc.gridx = 5; gbc.weightx = 0.5;
        spnTanggalSelesai = createDateSpinner();
        panel.add(spnTanggalSelesai, gbc);

        // Baris 2: Keterangan
        gbc.gridy = 1;
        gbc.gridx = 0; gbc.weightx = 0;
        panel.add(createLabel("Keterangan"), gbc);
        gbc.gridx = 1; gbc.gridwidth = 5; gbc.weightx = 1;
        txtKeterangan = new JTextField();
        txtKeterangan.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        panel.add(txtKeterangan, gbc);

        // Baris 3: Tombol
        gbc.gridy = 2;
        gbc.gridx = 0; gbc.gridwidth = 6;
        gbc.fill = GridBagConstraints.NONE;
        gbc.anchor = GridBagConstraints.EAST;

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 8, 0));
        buttonPanel.setOpaque(false);

        btnTambah = createStyledButton("➕ Tambah", ACCENT_GREEN);
        btnEdit = createStyledButton("✏️ Simpan Edit", ACCENT_BLUE);
        btnHapus = createStyledButton("🗑️ Hapus", ACCENT_RED);
        btnBersihkan = createStyledButton("🔄 Bersihkan", CARD_BORDER);

        btnEdit.setEnabled(false);
        btnHapus.setEnabled(false);

        buttonPanel.add(btnTambah);
        buttonPanel.add(btnEdit);
        buttonPanel.add(btnHapus);
        buttonPanel.add(btnBersihkan);
        panel.add(buttonPanel, gbc);

        // Event listeners
        btnTambah.addActionListener(e -> tambahCuti());
        btnEdit.addActionListener(e -> editCuti());
        btnHapus.addActionListener(e -> hapusCuti());
        btnBersihkan.addActionListener(e -> bersihkanForm());

        return panel;
    }

    private JPanel createTablePanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setOpaque(false);

        String[] columns = {"ID", "Karyawan", "Tanggal Mulai", "Tanggal Selesai", "Keterangan"};
        tableModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        table = new JTable(tableModel);
        table.setRowHeight(32);
        table.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        table.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 13));
        table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        table.setSelectionBackground(ACCENT_BLUE);
        table.setSelectionForeground(Color.WHITE);
        table.setGridColor(CARD_BORDER);

        table.getColumnModel().getColumn(0).setMaxWidth(60);

        DefaultTableCellRenderer centerRenderer = new DefaultTableCellRenderer();
        centerRenderer.setHorizontalAlignment(SwingConstants.CENTER);
        table.getColumnModel().getColumn(0).setCellRenderer(centerRenderer);
        table.getColumnModel().getColumn(2).setCellRenderer(centerRenderer);
        table.getColumnModel().getColumn(3).setCellRenderer(centerRenderer);

        // Klik baris → isi form
        table.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting() && table.getSelectedRow() >= 0) {
                int row = table.getSelectedRow();
                selectedId = (int) tableModel.getValueAt(row, 0);

                // Cari karyawan di combo box
                String namaKaryawan = (String) tableModel.getValueAt(row, 1);
                for (int i = 0; i < cbKaryawan.getItemCount(); i++) {
                    if (cbKaryawan.getItemAt(i).toString().equals(namaKaryawan)) {
                        cbKaryawan.setSelectedIndex(i);
                        break;
                    }
                }

                setDateSpinner(spnTanggalMulai, (String) tableModel.getValueAt(row, 2));
                setDateSpinner(spnTanggalSelesai, (String) tableModel.getValueAt(row, 3));
                txtKeterangan.setText((String) tableModel.getValueAt(row, 4));

                btnEdit.setEnabled(true);
                btnHapus.setEnabled(true);
                btnTambah.setEnabled(false);
            }
        });

        JScrollPane scrollPane = new JScrollPane(table);
        scrollPane.setBorder(BorderFactory.createLineBorder(CARD_BORDER, 1));
        scrollPane.getViewport().setBackground(CARD_BG);
        panel.add(scrollPane, BorderLayout.CENTER);

        return panel;
    }

    // ==================== CRUD ====================

    private void tambahCuti() {
        if (!validateForm()) return;

        KaryawanItem selected = (KaryawanItem) cbKaryawan.getSelectedItem();
        if (selected == null) return;

        KaryawanCuti cuti = new KaryawanCuti(
            selected.getId(),
            getDateFromSpinner(spnTanggalMulai),
            getDateFromSpinner(spnTanggalSelesai),
            txtKeterangan.getText().trim()
        );

        try {
            if (cutiDAO.insert(cuti)) {
                JOptionPane.showMessageDialog(this, "Data cuti berhasil ditambahkan!",
                    "Sukses", JOptionPane.INFORMATION_MESSAGE);
                bersihkanForm();
                refreshData();
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Gagal menambahkan data cuti: " + e.getMessage(),
                "Error Database", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void editCuti() {
        if (!validateForm() || selectedId == 0) return;

        KaryawanItem selected = (KaryawanItem) cbKaryawan.getSelectedItem();
        if (selected == null) return;

        KaryawanCuti cuti = new KaryawanCuti(
            selectedId,
            selected.getId(),
            getDateFromSpinner(spnTanggalMulai),
            getDateFromSpinner(spnTanggalSelesai),
            txtKeterangan.getText().trim()
        );

        try {
            if (cutiDAO.update(cuti)) {
                JOptionPane.showMessageDialog(this, "Data cuti berhasil diperbarui!",
                    "Sukses", JOptionPane.INFORMATION_MESSAGE);
                bersihkanForm();
                refreshData();
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Gagal memperbarui data cuti: " + e.getMessage(),
                "Error Database", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void hapusCuti() {
        if (selectedId == 0) return;

        int confirm = JOptionPane.showConfirmDialog(this,
            "Apakah Anda yakin ingin menghapus data cuti ini?",
            "Konfirmasi Hapus", JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);

        if (confirm == JOptionPane.YES_OPTION) {
            try {
                if (cutiDAO.delete(selectedId)) {
                    JOptionPane.showMessageDialog(this, "Data cuti berhasil dihapus!",
                        "Sukses", JOptionPane.INFORMATION_MESSAGE);
                    bersihkanForm();
                    refreshData();
                }
            } catch (SQLException e) {
                JOptionPane.showMessageDialog(this, "Gagal menghapus data cuti: " + e.getMessage(),
                    "Error Database", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private boolean validateForm() {
        if (cbKaryawan.getSelectedItem() == null) {
            JOptionPane.showMessageDialog(this, "Pilih karyawan terlebih dahulu!",
                "Validasi", JOptionPane.WARNING_MESSAGE);
            return false;
        }

        LocalDate mulai = getDateFromSpinner(spnTanggalMulai);
        LocalDate selesai = getDateFromSpinner(spnTanggalSelesai);

        if (selesai.isBefore(mulai)) {
            JOptionPane.showMessageDialog(this, "Tanggal selesai tidak boleh sebelum tanggal mulai!",
                "Validasi", JOptionPane.WARNING_MESSAGE);
            return false;
        }

        return true;
    }

    private void bersihkanForm() {
        selectedId = 0;
        if (cbKaryawan.getItemCount() > 0) cbKaryawan.setSelectedIndex(0);
        txtKeterangan.setText("");
        table.clearSelection();
        btnTambah.setEnabled(true);
        btnEdit.setEnabled(false);
        btnHapus.setEnabled(false);
    }

    public void refreshData() {
        loadKaryawanComboBox();
        try {
            tableModel.setRowCount(0);
            List<KaryawanCuti> list = cutiDAO.getAll();
            for (KaryawanCuti c : list) {
                tableModel.addRow(new Object[]{
                    c.getId(),
                    c.getNamaKaryawan(),
                    c.getTanggalMulai().format(DATE_FORMAT),
                    c.getTanggalSelesai().format(DATE_FORMAT),
                    c.getKeterangan()
                });
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Gagal memuat data cuti: " + e.getMessage(),
                "Error Database", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void loadKaryawanComboBox() {
        try {
            Object selectedItem = cbKaryawan.getSelectedItem();
            cbKaryawan.removeAllItems();
            List<Karyawan> karyawanList = karyawanDAO.getAll();
            for (Karyawan k : karyawanList) {
                cbKaryawan.addItem(new KaryawanItem(k.getId(), k.getNama()));
            }
            // Restore selection if possible
            if (selectedItem != null) {
                cbKaryawan.setSelectedItem(selectedItem);
            }
        } catch (SQLException e) {
            // Silently handle — data will be empty
        }
    }

    // ==================== HELPER ====================

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

    private void setDateSpinner(JSpinner spinner, String dateStr) {
        try {
            LocalDate date = LocalDate.parse(dateStr, DATE_FORMAT);
            Calendar cal = Calendar.getInstance();
            cal.set(date.getYear(), date.getMonthValue() - 1, date.getDayOfMonth());
            spinner.setValue(cal.getTime());
        } catch (Exception ignored) {
        }
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

    /**
     * Helper class untuk menampilkan karyawan di JComboBox
     * dengan menampilkan nama tapi menyimpan ID.
     */
    private static class KaryawanItem {
        private final int id;
        private final String nama;

        public KaryawanItem(int id, String nama) {
            this.id = id;
            this.nama = nama;
        }

        public int getId() {
            return id;
        }

        @Override
        public String toString() {
            return nama;
        }
    }
}
