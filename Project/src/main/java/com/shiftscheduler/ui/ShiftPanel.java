package com.shiftscheduler.ui;

import com.shiftscheduler.dao.ShiftDAO;
import com.shiftscheduler.model.Shift;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.sql.SQLException;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

/**
 * Panel CRUD untuk manajemen data Shift.
 * Menampilkan tabel shift dan form input dengan time picker (JSpinner).
 */
public class ShiftPanel extends JPanel {

    private final ShiftDAO shiftDAO;

    // Form fields
    private JTextField txtKodeShift;
    private JSpinner spnJamMulai;
    private JSpinner spnJamSelesai;
    private JSpinner spnKuota;
    private JButton btnTambah, btnEdit, btnHapus, btnBersihkan;

    // Tabel
    private DefaultTableModel tableModel;
    private JTable table;

    // ID shift yang sedang diedit
    private int selectedId = 0;

    // Warna tema
    private static final Color BG_COLOR = new Color(38, 42, 52);
    private static final Color CARD_BG = new Color(48, 53, 65);
    private static final Color CARD_BORDER = new Color(60, 65, 78);
    private static final Color ACCENT_BLUE = new Color(59, 130, 246);
    private static final Color ACCENT_RED = new Color(239, 68, 68);
    private static final Color ACCENT_GREEN = new Color(34, 197, 94);

    public ShiftPanel() {
        this.shiftDAO = new ShiftDAO();

        setBackground(BG_COLOR);
        setLayout(new BorderLayout(0, 15));
        setBorder(new EmptyBorder(20, 25, 20, 25));

        initComponents();
        refreshData();
    }

    private void initComponents() {
        // === HEADER ===
        JLabel titleLabel = new JLabel("🕐 Manajemen Shift");
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 24));
        titleLabel.setForeground(Color.WHITE);
        add(titleLabel, BorderLayout.NORTH);

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

        // Baris 1
        gbc.gridy = 0;

        gbc.gridx = 0; gbc.weightx = 0;
        panel.add(createLabel("Kode Shift"), gbc);
        gbc.gridx = 1; gbc.weightx = 0.5;
        txtKodeShift = new JTextField(5);
        txtKodeShift.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        panel.add(txtKodeShift, gbc);

        gbc.gridx = 2; gbc.weightx = 0;
        panel.add(createLabel("Jam Mulai"), gbc);
        gbc.gridx = 3; gbc.weightx = 0.5;
        spnJamMulai = createTimeSpinner();
        panel.add(spnJamMulai, gbc);

        gbc.gridx = 4; gbc.weightx = 0;
        panel.add(createLabel("Jam Selesai"), gbc);
        gbc.gridx = 5; gbc.weightx = 0.5;
        spnJamSelesai = createTimeSpinner();
        panel.add(spnJamSelesai, gbc);

        gbc.gridx = 6; gbc.weightx = 0;
        panel.add(createLabel("Kuota"), gbc);
        gbc.gridx = 7; gbc.weightx = 0.3;
        spnKuota = new JSpinner(new SpinnerNumberModel(3, 1, 50, 1));
        spnKuota.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        panel.add(spnKuota, gbc);

        // Baris 2: Tombol
        gbc.gridy = 1;
        gbc.gridx = 0; gbc.gridwidth = 8;
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
        btnTambah.addActionListener(e -> tambahShift());
        btnEdit.addActionListener(e -> editShift());
        btnHapus.addActionListener(e -> hapusShift());
        btnBersihkan.addActionListener(e -> bersihkanForm());

        return panel;
    }

    private JPanel createTablePanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setOpaque(false);

        String[] columns = {"ID", "Kode Shift", "Jam Mulai", "Jam Selesai", "Kuota Kebutuhan"};
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
        for (int i = 0; i < table.getColumnCount(); i++) {
            table.getColumnModel().getColumn(i).setCellRenderer(centerRenderer);
        }

        // Klik baris → isi form
        table.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting() && table.getSelectedRow() >= 0) {
                int row = table.getSelectedRow();
                selectedId = (int) tableModel.getValueAt(row, 0);
                txtKodeShift.setText((String) tableModel.getValueAt(row, 1));

                setTimeSpinner(spnJamMulai, (String) tableModel.getValueAt(row, 2));
                setTimeSpinner(spnJamSelesai, (String) tableModel.getValueAt(row, 3));
                spnKuota.setValue(tableModel.getValueAt(row, 4));

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

    private void tambahShift() {
        if (!validateForm()) return;

        Shift shift = new Shift(
            txtKodeShift.getText().trim().toUpperCase(),
            getTimeFromSpinner(spnJamMulai),
            getTimeFromSpinner(spnJamSelesai),
            (int) spnKuota.getValue()
        );

        try {
            if (shiftDAO.insert(shift)) {
                JOptionPane.showMessageDialog(this, "Shift berhasil ditambahkan!",
                    "Sukses", JOptionPane.INFORMATION_MESSAGE);
                bersihkanForm();
                refreshData();
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Gagal menambahkan shift: " + e.getMessage(),
                "Error Database", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void editShift() {
        if (!validateForm() || selectedId == 0) return;

        Shift shift = new Shift(
            selectedId,
            txtKodeShift.getText().trim().toUpperCase(),
            getTimeFromSpinner(spnJamMulai),
            getTimeFromSpinner(spnJamSelesai),
            (int) spnKuota.getValue()
        );

        try {
            if (shiftDAO.update(shift)) {
                JOptionPane.showMessageDialog(this, "Data shift berhasil diperbarui!",
                    "Sukses", JOptionPane.INFORMATION_MESSAGE);
                bersihkanForm();
                refreshData();
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Gagal memperbarui shift: " + e.getMessage(),
                "Error Database", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void hapusShift() {
        if (selectedId == 0) return;

        int confirm = JOptionPane.showConfirmDialog(this,
            "Apakah Anda yakin ingin menghapus shift ini?\nSemua jadwal yang menggunakan shift ini juga akan dihapus.",
            "Konfirmasi Hapus", JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);

        if (confirm == JOptionPane.YES_OPTION) {
            try {
                if (shiftDAO.delete(selectedId)) {
                    JOptionPane.showMessageDialog(this, "Shift berhasil dihapus!",
                        "Sukses", JOptionPane.INFORMATION_MESSAGE);
                    bersihkanForm();
                    refreshData();
                }
            } catch (SQLException e) {
                JOptionPane.showMessageDialog(this, "Gagal menghapus shift: " + e.getMessage(),
                    "Error Database", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private boolean validateForm() {
        if (txtKodeShift.getText().trim().isEmpty()) {
            JOptionPane.showMessageDialog(this, "Kode shift tidak boleh kosong!",
                "Validasi", JOptionPane.WARNING_MESSAGE);
            txtKodeShift.requestFocus();
            return false;
        }
        return true;
    }

    private void bersihkanForm() {
        selectedId = 0;
        txtKodeShift.setText("");
        // Reset time spinners to defaults
        setTimeSpinner(spnJamMulai, "06:00");
        setTimeSpinner(spnJamSelesai, "14:00");
        spnKuota.setValue(3);
        table.clearSelection();
        btnTambah.setEnabled(true);
        btnEdit.setEnabled(false);
        btnHapus.setEnabled(false);
    }

    public void refreshData() {
        try {
            tableModel.setRowCount(0);
            List<Shift> list = shiftDAO.getAll();
            for (Shift s : list) {
                tableModel.addRow(new Object[]{
                    s.getId(),
                    s.getKodeShift(),
                    s.getJamMulai().format(DateTimeFormatter.ofPattern("HH:mm")),
                    s.getJamSelesai().format(DateTimeFormatter.ofPattern("HH:mm")),
                    s.getKuotaKebutuhan()
                });
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Gagal memuat data shift: " + e.getMessage(),
                "Error Database", JOptionPane.ERROR_MESSAGE);
        }
    }

    // ==================== HELPER ====================

    private JSpinner createTimeSpinner() {
        SpinnerDateModel model = new SpinnerDateModel();
        JSpinner spinner = new JSpinner(model);
        JSpinner.DateEditor editor = new JSpinner.DateEditor(spinner, "HH:mm");
        spinner.setEditor(editor);
        spinner.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        return spinner;
    }

    private LocalTime getTimeFromSpinner(JSpinner spinner) {
        Date date = (Date) spinner.getValue();
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        return LocalTime.of(cal.get(Calendar.HOUR_OF_DAY), cal.get(Calendar.MINUTE));
    }

    private void setTimeSpinner(JSpinner spinner, String time) {
        try {
            String[] parts = time.split(":");
            Calendar cal = Calendar.getInstance();
            cal.set(Calendar.HOUR_OF_DAY, Integer.parseInt(parts[0]));
            cal.set(Calendar.MINUTE, Integer.parseInt(parts[1]));
            cal.set(Calendar.SECOND, 0);
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
}
