package com.shiftscheduler;

import com.formdev.flatlaf.FlatDarkLaf;
import com.shiftscheduler.ui.MainFrame;

import javax.swing.*;

/**
 * Entry point aplikasi Sistem Penjadwalan Shift Karyawan.
 * Menginisialisasi FlatLaf Dark Look & Feel dan meluncurkan MainFrame.
 */
public class Main {

    public static void main(String[] args) {
        // Setup FlatLaf Dark theme sebelum membuat komponen Swing
        try {
            UIManager.setLookAndFeel(new FlatDarkLaf());

            // Kustomisasi tambahan FlatLaf
            UIManager.put("Button.arc", 8);
            UIManager.put("Component.arc", 8);
            UIManager.put("TextComponent.arc", 8);
            UIManager.put("Table.showHorizontalLines", true);
            UIManager.put("Table.showVerticalLines", true);
            UIManager.put("ScrollBar.thumbArc", 999);
            UIManager.put("ScrollBar.width", 10);

            System.out.println("[APP] FlatLaf Dark theme berhasil dimuat.");
        } catch (UnsupportedLookAndFeelException e) {
            System.err.println("[APP] Gagal memuat FlatLaf: " + e.getMessage());
            System.err.println("[APP] Menggunakan Look & Feel default.");
        }

        // Luncurkan GUI pada Event Dispatch Thread (EDT)
        SwingUtilities.invokeLater(() -> {
            MainFrame frame = new MainFrame();
            frame.setVisible(true);
        });
    }
}
