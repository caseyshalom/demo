import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class DataGajiPegawai extends JFrame {
    private JComboBox<String> cbNip, cbGolongan;
    private JTextField txtNama, txtJabatan, txtGajiPokok, txtTunjangan, txtTotalGaji;
    private JButton btnHitung, btnBatal;

    public DataGajiPegawai() {
        // Set Judul Form sesuai dengan screenshot
        setTitle("Data Gaji Pegawai");
        setSize(400, 380);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());

        // Gunakan System Look and Feel agar terlihat modern dan bersih
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception e) {
            // Abaikan jika gagal
        }

        // Panel Utama Form dengan GridBagLayout agar rapi dan responsif
        JPanel panelForm = new JPanel(new GridBagLayout());
        panelForm.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(6, 6, 6, 6);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        // Inisialisasi Pilihan NIP
        String[] nipList = {"- Pilih NIP -", "200803735", "200803736", "200803737", "200803738", "200803739"};
        cbNip = new JComboBox<>(nipList);

        // Inisialisasi Pilihan Golongan
        String[] golList = {"- Pilih Golongan -", "I", "II", "III"};
        cbGolongan = new JComboBox<>(golList);

        // Inisialisasi TextFields
        txtNama = new JTextField();
        txtNama.setEditable(false); // Sesuai ketentuan, nama ditentukan otomatis
        
        txtJabatan = new JTextField();
        txtJabatan.setEditable(false); // Jabatan ditentukan otomatis

        txtGajiPokok = new JTextField();
        txtGajiPokok.setEditable(false); // Gaji Pokok ditentukan otomatis

        txtTunjangan = new JTextField();
        txtTunjangan.setEditable(false); // Tunjangan ditentukan otomatis

        txtTotalGaji = new JTextField();
        txtTotalGaji.setEditable(false); // Total Gaji dihitung oleh sistem

        // Tambah Komponen ke Form Panel
        // 1. NIP
        gbc.gridy = 0;
        gbc.gridx = 0; gbc.weightx = 0.0;
        panelForm.add(new JLabel("NIP"), gbc);
        gbc.gridx = 1; gbc.weightx = 1.0;
        panelForm.add(cbNip, gbc);

        // 2. Nama
        gbc.gridy = 1;
        gbc.gridx = 0; gbc.weightx = 0.0;
        panelForm.add(new JLabel("Nama"), gbc);
        gbc.gridx = 1; gbc.weightx = 1.0;
        panelForm.add(txtNama, gbc);

        // 3. Golongan
        gbc.gridy = 2;
        gbc.gridx = 0; gbc.weightx = 0.0;
        panelForm.add(new JLabel("Golongan"), gbc);
        gbc.gridx = 1; gbc.weightx = 1.0;
        panelForm.add(cbGolongan, gbc);

        // 4. Jabatan
        gbc.gridy = 3;
        gbc.gridx = 0; gbc.weightx = 0.0;
        panelForm.add(new JLabel("Jabatan"), gbc);
        gbc.gridx = 1; gbc.weightx = 1.0;
        panelForm.add(txtJabatan, gbc);

        // 5. Gaji Pokok
        gbc.gridy = 4;
        gbc.gridx = 0; gbc.weightx = 0.0;
        panelForm.add(new JLabel("Gaji Pokok"), gbc);
        gbc.gridx = 1; gbc.weightx = 1.0;
        panelForm.add(txtGajiPokok, gbc);

        // 6. Tunjangan
        gbc.gridy = 5;
        gbc.gridx = 0; gbc.weightx = 0.0;
        panelForm.add(new JLabel("Tunjangan"), gbc);
        gbc.gridx = 1; gbc.weightx = 1.0;
        panelForm.add(txtTunjangan, gbc);

        // 7. Total Gaji
        gbc.gridy = 6;
        gbc.gridx = 0; gbc.weightx = 0.0;
        panelForm.add(new JLabel("TotalGaji"), gbc);
        gbc.gridx = 1; gbc.weightx = 1.0;
        panelForm.add(txtTotalGaji, gbc);

        add(panelForm, BorderLayout.CENTER);

        // Panel Tombol
        JPanel panelTombol = new JPanel(new FlowLayout(FlowLayout.CENTER, 15, 10));
        btnHitung = new JButton("Hitung");
        btnBatal = new JButton("Batal");
        panelTombol.add(btnHitung);
        panelTombol.add(btnBatal);
        add(panelTombol, BorderLayout.SOUTH);

        // Event Listener untuk JComboBox NIP
        cbNip.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                updateNama();
            }
        });

        // Event Listener untuk JComboBox Golongan
        cbGolongan.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                updateGolonganDetails();
            }
        });

        // Event Listener untuk Tombol Hitung
        btnHitung.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                hitungTotalGaji();
            }
        });

        // Event Listener untuk Tombol Batal
        btnBatal.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                resetForm();
            }
        });
    }

    // Metode update nama berdasarkan NIP yang dipilih
    private void updateNama() {
        String nip = (String) cbNip.getSelectedItem();
        if (nip != null) {
            switch (nip) {
                case "200803735":
                    txtNama.setText("Muhammad");
                    break;
                case "200803736":
                    txtNama.setText("Abdul Aziz");
                    break;
                case "200803737":
                    txtNama.setText("Suhair");
                    break;
                case "200803738":
                    txtNama.setText("Ibrahim");
                    break;
                case "200803739":
                    txtNama.setText("Arhan");
                    break;
                default:
                    txtNama.setText("");
                    break;
            }
        }
    }

    // Metode update jabatan, gaji pokok, dan tunjangan berdasarkan Golongan yang dipilih
    private void updateGolonganDetails() {
        String golongan = (String) cbGolongan.getSelectedItem();
        if (golongan != null) {
            switch (golongan) {
                case "I":
                    txtJabatan.setText("Direktur");
                    txtGajiPokok.setText("3000000");
                    txtTunjangan.setText("1000000");
                    break;
                case "II":
                    txtJabatan.setText("Manager");
                    txtGajiPokok.setText("2000000");
                    txtTunjangan.setText("500000");
                    break;
                case "III":
                    txtJabatan.setText("Kabag");
                    txtGajiPokok.setText("1500000");
                    txtTunjangan.setText("3000000");
                    break;
                default:
                    txtJabatan.setText("");
                    txtGajiPokok.setText("");
                    txtTunjangan.setText("");
                    break;
            }
        }
    }

    // Metode hitung total gaji
    private void hitungTotalGaji() {
        try {
            String gajiPokokStr = txtGajiPokok.getText();
            String tunjanganStr = txtTunjangan.getText();

            if (gajiPokokStr.isEmpty() || tunjanganStr.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Silakan pilih Golongan terlebih dahulu untuk memuat gaji pokok dan tunjangan!", "Peringatan", JOptionPane.WARNING_MESSAGE);
                return;
            }

            long gajiPokok = Long.parseLong(gajiPokokStr);
            long tunjangan = Long.parseLong(tunjanganStr);
            long totalGaji = gajiPokok + tunjangan;

            txtTotalGaji.setText(String.valueOf(totalGaji));
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this, "Data Gaji tidak valid!", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    // Metode reset form kembali ke default
    private void resetForm() {
        cbNip.setSelectedIndex(0);
        cbGolongan.setSelectedIndex(0);
        txtNama.setText("");
        txtJabatan.setText("");
        txtGajiPokok.setText("");
        txtTunjangan.setText("");
        txtTotalGaji.setText("");
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                new DataGajiPegawai().setVisible(true);
            }
        });
    }
}
