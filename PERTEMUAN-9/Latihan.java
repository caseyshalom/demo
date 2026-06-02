import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class Latihan extends JFrame {
    private JTextField txtAbsen, txtTugas, txtUts, txtUas, txtHasil, txtGrade;
    private JButton btnHitung, btnBatal;

    public Latihan() {
        setTitle("GUI Swing");
        setSize(320, 250);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());
        JPanel panelUtama = new JPanel();
        panelUtama.setLayout(new GridLayout(6, 2, 5, 5));
        panelUtama.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        panelUtama.add(new JLabel("Nilai absen"));
        txtAbsen = new JTextField();
        panelUtama.add(txtAbsen);
        panelUtama.add(new JLabel("Nilai tugas"));
        txtTugas = new JTextField();
        panelUtama.add(txtTugas);
        panelUtama.add(new JLabel("Nilai UTS"));
        txtUts = new JTextField();
        panelUtama.add(txtUts);
        panelUtama.add(new JLabel("Nilai UAS"));
        txtUas = new JTextField();
        panelUtama.add(txtUas);
        panelUtama.add(new JLabel("Hasil"));
        txtHasil = new JTextField();
        txtHasil.setEditable(false);
        panelUtama.add(txtHasil);
        panelUtama.add(new JLabel("Grade"));
        txtGrade = new JTextField();
        txtGrade.setEditable(false);
        panelUtama.add(txtGrade);
        add(panelUtama, BorderLayout.CENTER);
        JPanel panelTombol = new JPanel();
        panelTombol.setLayout(new FlowLayout(FlowLayout.RIGHT));
        btnHitung = new JButton("Hitung");
        btnBatal = new JButton("batal");
        panelTombol.add(btnHitung);
        panelTombol.add(btnBatal);
        add(panelTombol, BorderLayout.SOUTH);
        btnHitung.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                hitungNilai();
            }
        });
        btnBatal.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                batal();
            }
        });
    }

    private void hitungNilai() {
        try {
            double absen = Double.parseDouble(txtAbsen.getText());
            double tugas = Double.parseDouble(txtTugas.getText());
            double uts = Double.parseDouble(txtUts.getText());
            double uas = Double.parseDouble(txtUas.getText());
            double hasil = (absen * 0.20) + (tugas * 0.25) + (uts * 0.25) + (uas * 0.30);
            txtHasil.setText(String.valueOf(hasil));
            String grade = "";
            if (hasil >= 80) {
                grade = "A";
            } else if (hasil >= 70) {
                grade = "B";
            } else if (hasil >= 50) {
                grade = "C";
            } else if (hasil >= 30) {
                grade = "D";
            } else if (hasil >= 0) {
                grade = "E";
            } else {
                grade = "Invalid";
            }
            txtGrade.setText(grade);
        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this, "Masukkan nilai angka yang valid!", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void batal() {
        txtAbsen.setText("");
        txtTugas.setText("");
        txtUts.setText("");
        txtUas.setText("");
        txtHasil.setText("");
        txtGrade.setText("");
        txtAbsen.requestFocus();
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                new Latihan().setVisible(true);
            }
        });
    }
}
