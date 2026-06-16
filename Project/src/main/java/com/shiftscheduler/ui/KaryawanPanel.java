package com.shiftscheduler.ui;

import com.shiftscheduler.dao.KaryawanDAO;
import com.shiftscheduler.model.Karyawan;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.sql.SQLException;
import java.util.List;

/**
 * Panel CRUD untuk manajemen data Karyawan.
 * Menampilkan tabel karyawan dan form input untuk operasi CRUD.
 */
public class KaryawanPanel extends JPanel {

    private final KaryawanDAO karyawanDAO;

    // Form fields
    private JTextField txtNama;
    private JComboBox<String> cbPosisi;
    private JSpinner spnBatasShift;
    private JButton btnTambah, btnEdit, btnHapus, btnBersihkan;

    // Tabel
    private DefaultTableModel tableModel;
    private JTable table;

    // ID karyawan yang sedang diedit (0 = mode tambah)
    private int selectedId = 0;

    // Warna tema
    private static final Color BG_COLOR = new Color(38, 42, 52);
    private static final Color CARD_BG = new Color(48, 53, 65);
    private static final Color CARD_BORDER = new Color(60, 65, 78);
    private static final Color ACCENT_BLUE = new Color(59, 130, 246);
    private static final Color ACCENT_RED = new Color(239, 68, 68);
    private static final Color ACCENT_GREEN = new Color(34, 197, 94);

    public KaryawanPanel() {
        this.karyawanDAO = new KaryawanDAO();

        setBackground(BG_COLOR);
        setLayout(new BorderLayout(0, 15));
        setBorder(new EmptyBorder(20, 25, 20, 25));

        initComponents();
        refreshData();
    }

    private void initComponents() {
        // === HEADER ===
        JLabel titleLabel = new JLabel("👤 Manajemen Karyawan");
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 24));
        titleLabel.setForeground(Color.WHITE);
        add(titleLabel, BorderLayout.NORTH);

        // === SPLIT: Form (top) + Table (center) ===
        JPanel mainPanel = new JPanel(new BorderLayout(0, 15));
        mainPanel.setOpaque(false);

        // --- Form Input ---
        JPanel formPanel = createFormPanel();
        mainPanel.add(formPanel, BorderLayout.NORTH);

        // --- Tabel ---
        JPanel tablePanel = createTablePanel();
        mainPanel.add(tablePanel, BorderLayout.CENTER);

        add(mainPanel, BorderLayout.CENTER);
    }

    /**
     * Membuat panel form input untuk CRUD karyawan.
     */
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

        // Baris 1: Nama
        gbc.gridy = 0;
        gbc.gridx = 0;
        gbc.weightx = 0;
        panel.add(createLabel("Nama"), gbc);

        gbc.gridx = 1;
        gbc.weightx = 1;
        txtNama = new JTextField(20);
        txtNama.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        panel.add(txtNama, gbc);

        // Baris 1: Posisi
        gbc.gridx = 2;
        gbc.weightx = 0;
        panel.add(createLabel("Posisi"), gbc);

        gbc.gridx = 3;
        gbc.weightx = 0.5;
        cbPosisi = new JComboBox<>(new String[]{"Kasir", "Gudang", "Supervisor", "Manager", "Admin"});
        cbPosisi.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        panel.add(cbPosisi, gbc);

        // Baris 1: Batas Shift Mingguan
        gbc.gridx = 4;
        gbc.weightx = 0;
        panel.add(createLabel("Batas Shift/Minggu"), gbc);

        gbc.gridx = 5;
        gbc.weightx = 0.3;
        spnBatasShift = new JSpinner(new SpinnerNumberModel(5, 1, 7, 1));
        spnBatasShift.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        panel.add(spnBatasShift, gbc);

        // Baris 2: Tombol aksi
        gbc.gridy = 1;
        gbc.gridx = 0;
        gbc.gridwidth = 6;
        gbc.weightx = 1;
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

        // === EVENT LISTENERS ===
        btnTambah.addActionListener(e -> tambahKaryawan());
        btnEdit.addActionListener(e -> editKaryawan());
        btnHapus.addActionListener(e -> hapusKaryawan());
        btnBersihkan.addActionListener(e -> bersihkanForm());

        return panel;
    }

    /**
     * Membuat panel tabel karyawan.
     */
    private JPanel createTablePanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setOpaque(false);

        String[] columns = {"ID", "Nama", "Posisi", "Batas Shift/Minggu"};
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

        // Atur lebar kolom
        table.getColumnModel().getColumn(0).setMaxWidth(60);
        table.getColumnModel().getColumn(3).setMaxWidth(150);

        // Center alignment
        DefaultTableCellRenderer centerRenderer = new DefaultTableCellRenderer();
        centerRenderer.setHorizontalAlignment(SwingConstants.CENTER);
        table.getColumnModel().getColumn(0).setCellRenderer(centerRenderer);
        table.getColumnModel().getColumn(3).setCellRenderer(centerRenderer);

        // Event: Klik baris tabel → isi form untuk edit
        table.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting() && table.getSelectedRow() >= 0) {
                int row = table.getSelectedRow();
                selectedId = (int) tableModel.getValueAt(row, 0);
                txtNama.setText((String) tableModel.getValueAt(row, 1));
                cbPosisi.setSelectedItem(tableModel.getValueAt(row, 2));
                spnBatasShift.setValue(tableModel.getValueAt(row, 3));

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

    // ==================== CRUD OPERATIONS ====================

    private void tambahKaryawan() {
        if (!validateForm()) return;

        Karyawan karyawan = new Karyawan(
            txtNama.getText().trim(),
            (String) cbPosisi.getSelectedItem(),
            (int) spnBatasShift.getValue()
        );

        try {
            if (karyawanDAO.insert(karyawan)) {
                JOptionPane.showMessageDialog(this, "Karyawan berhasil ditambahkan!",
                    "Sukses", JOptionPane.INFORMATION_MESSAGE);
                bersihkanForm();
                refreshData();
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Gagal menambahkan karyawan: " + e.getMessage(),
                "Error Database", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void editKaryawan() {
        if (!validateForm() || selectedId == 0) return;

        Karyawan karyawan = new Karyawan(
            selectedId,
            txtNama.getText().trim(),
            (String) cbPosisi.getSelectedItem(),
            (int) spnBatasShift.getValue()
        );

        try {
            if (karyawanDAO.update(karyawan)) {
                JOptionPane.showMessageDialog(this, "Data karyawan berhasil diperbarui!",
                    "Sukses", JOptionPane.INFORMATION_MESSAGE);
                bersihkanForm();
                refreshData();
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Gagal memperbarui data: " + e.getMessage(),
                "Error Database", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void hapusKaryawan() {
        if (selectedId == 0) return;

        int confirm = JOptionPane.showConfirmDialog(this,
            "Apakah Anda yakin ingin menghapus karyawan ini?\nSemua data cuti dan jadwal terkait juga akan dihapus.",
            "Konfirmasi Hapus", JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);

        if (confirm == JOptionPane.YES_OPTION) {
            try {
                if (karyawanDAO.delete(selectedId)) {
                    JOptionPane.showMessageDialog(this, "Karyawan berhasil dihapus!",
                        "Sukses", JOptionPane.INFORMATION_MESSAGE);
                    bersihkanForm();
                    refreshData();
                }
            } catch (SQLException e) {
                JOptionPane.showMessageDialog(this, "Gagal menghapus karyawan: " + e.getMessage(),
                    "Error Database", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private boolean validateForm() {
        if (txtNama.getText().trim().isEmpty()) {
            JOptionPane.showMessageDialog(this, "Nama karyawan tidak boleh kosong!",
                "Validasi", JOptionPane.WARNING_MESSAGE);
            txtNama.requestFocus();
            return false;
        }
        return true;
    }

    private void bersihkanForm() {
        selectedId = 0;
        txtNama.setText("");
        cbPosisi.setSelectedIndex(0);
        spnBatasShift.setValue(5);
        table.clearSelection();
        btnTambah.setEnabled(true);
        btnEdit.setEnabled(false);
        btnHapus.setEnabled(false);
    }

    /**
     * Memuat ulang data dari database ke tabel.
     */
    public void refreshData() {
        try {
            tableModel.setRowCount(0);
            List<Karyawan> list = karyawanDAO.getAll();
            for (Karyawan k : list) {
                tableModel.addRow(new Object[]{
                    k.getId(),
                    k.getNama(),
                    k.getPosisi(),
                    k.getBatasShiftMingguan()
                });
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Gagal memuat data karyawan: " + e.getMessage(),
                "Error Database", JOptionPane.ERROR_MESSAGE);
        }
    }

    // ==================== HELPER METHODS ====================

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
