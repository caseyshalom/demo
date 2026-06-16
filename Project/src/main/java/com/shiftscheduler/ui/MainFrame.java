package com.shiftscheduler.ui;

import com.shiftscheduler.db.DatabaseConnection;

import javax.swing.*;
import java.awt.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

/**
 * Frame utama aplikasi dengan sidebar navigation dan content area menggunakan CardLayout.
 * 
 * Layout:
 * ┌──────────────┬──────────────────────────────────────────┐
 * │   SIDEBAR    │           CONTENT AREA                   │
 * │              │         (CardLayout)                     │
 * │  🏠 Dashboard│                                          │
 * │  👤 Karyawan │    Menampilkan panel sesuai              │
 * │  🕐 Shift    │    menu yang dipilih                     │
 * │  📋 Cuti     │                                          │
 * │  📅 Jadwal   │                                          │
 * │              │                                          │
 * └──────────────┴──────────────────────────────────────────┘
 */
public class MainFrame extends JFrame {

    private static final String CARD_DASHBOARD = "Dashboard";
    private static final String CARD_KARYAWAN = "Karyawan";
    private static final String CARD_SHIFT = "Shift";
    private static final String CARD_CUTI = "Cuti";
    private static final String CARD_JADWAL = "Jadwal";

    private JPanel contentPanel;
    private CardLayout cardLayout;

    // Panel-panel konten
    private DashboardPanel dashboardPanel;
    private KaryawanPanel karyawanPanel;
    private ShiftPanel shiftPanel;
    private CutiPanel cutiPanel;
    private JadwalPanel jadwalPanel;

    // Tombol sidebar (disimpan untuk styling aktif/inaktif)
    private JButton[] sidebarButtons;
    private int activeIndex = 0;

    // Warna tema
    private static final Color SIDEBAR_BG = new Color(30, 33, 40);
    private static final Color SIDEBAR_ACTIVE = new Color(59, 130, 246);
    private static final Color SIDEBAR_HOVER = new Color(45, 50, 60);
    private static final Color SIDEBAR_TEXT = new Color(200, 200, 210);
    private static final Color CONTENT_BG = new Color(38, 42, 52);

    public MainFrame() {
        setTitle("Sistem Penjadwalan Shift Karyawan");
        setSize(1200, 750);
        setMinimumSize(new Dimension(1000, 600));
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        // Tutup koneksi DB saat window ditutup
        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                DatabaseConnection.getInstance().closeConnection();
            }
        });

        initComponents();
    }

    /**
     * Menginisialisasi semua komponen UI.
     */
    private void initComponents() {
        setLayout(new BorderLayout());

        // ==================== SIDEBAR ====================
        JPanel sidebar = createSidebar();
        add(sidebar, BorderLayout.WEST);

        // ==================== CONTENT AREA ====================
        cardLayout = new CardLayout();
        contentPanel = new JPanel(cardLayout);
        contentPanel.setBackground(CONTENT_BG);

        // Inisialisasi semua panel konten
        dashboardPanel = new DashboardPanel();
        karyawanPanel = new KaryawanPanel();
        shiftPanel = new ShiftPanel();
        cutiPanel = new CutiPanel();
        jadwalPanel = new JadwalPanel();

        contentPanel.add(dashboardPanel, CARD_DASHBOARD);
        contentPanel.add(karyawanPanel, CARD_KARYAWAN);
        contentPanel.add(shiftPanel, CARD_SHIFT);
        contentPanel.add(cutiPanel, CARD_CUTI);
        contentPanel.add(jadwalPanel, CARD_JADWAL);

        add(contentPanel, BorderLayout.CENTER);

        // Tampilkan Dashboard sebagai default
        cardLayout.show(contentPanel, CARD_DASHBOARD);
        updateSidebarActive(0);
    }

    /**
     * Membuat panel sidebar dengan tombol navigasi.
     */
    private JPanel createSidebar() {
        JPanel sidebar = new JPanel();
        sidebar.setLayout(new BoxLayout(sidebar, BoxLayout.Y_AXIS));
        sidebar.setBackground(SIDEBAR_BG);
        sidebar.setPreferredSize(new Dimension(220, 0));
        sidebar.setBorder(BorderFactory.createEmptyBorder(0, 0, 0, 0));

        // === Header/Logo ===
        JPanel logoPanel = new JPanel(new BorderLayout());
        logoPanel.setBackground(SIDEBAR_BG);
        logoPanel.setMaximumSize(new Dimension(220, 80));
        logoPanel.setPreferredSize(new Dimension(220, 80));
        logoPanel.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));

        JLabel logoLabel = new JLabel("📅 ShiftScheduler");
        logoLabel.setForeground(Color.WHITE);
        logoLabel.setFont(new Font("Segoe UI", Font.BOLD, 16));
        logoPanel.add(logoLabel, BorderLayout.CENTER);

        sidebar.add(logoPanel);

        // Separator
        JSeparator sep = new JSeparator();
        sep.setForeground(new Color(60, 65, 75));
        sep.setMaximumSize(new Dimension(220, 2));
        sidebar.add(sep);
        sidebar.add(Box.createVerticalStrut(10));

        // === Menu Label ===
        JLabel menuLabel = new JLabel("   MENU UTAMA");
        menuLabel.setForeground(new Color(120, 125, 135));
        menuLabel.setFont(new Font("Segoe UI", Font.PLAIN, 10));
        menuLabel.setMaximumSize(new Dimension(220, 25));
        sidebar.add(menuLabel);
        sidebar.add(Box.createVerticalStrut(5));

        // === Tombol Navigasi ===
        String[] labels = {"🏠  Dashboard", "👤  Karyawan", "🕐  Shift", "📋  Cuti / Izin", "📅  Jadwal"};
        String[] cards = {CARD_DASHBOARD, CARD_KARYAWAN, CARD_SHIFT, CARD_CUTI, CARD_JADWAL};
        sidebarButtons = new JButton[labels.length];

        for (int i = 0; i < labels.length; i++) {
            final int index = i;
            JButton btn = createSidebarButton(labels[i]);
            btn.addActionListener(e -> {
                cardLayout.show(contentPanel, cards[index]);
                updateSidebarActive(index);

                // Refresh data saat panel ditampilkan
                refreshPanel(cards[index]);
            });
            sidebarButtons[i] = btn;
            sidebar.add(btn);
            sidebar.add(Box.createVerticalStrut(2));
        }

        sidebar.add(Box.createVerticalGlue());

        // === Footer info ===
        JLabel verLabel = new JLabel("   v1.0 — PBO Project");
        verLabel.setForeground(new Color(80, 85, 95));
        verLabel.setFont(new Font("Segoe UI", Font.PLAIN, 10));
        verLabel.setMaximumSize(new Dimension(220, 30));
        sidebar.add(verLabel);
        sidebar.add(Box.createVerticalStrut(10));

        return sidebar;
    }

    /**
     * Membuat tombol sidebar dengan styling modern.
     */
    private JButton createSidebarButton(String text) {
        JButton btn = new JButton(text);
        btn.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        btn.setForeground(SIDEBAR_TEXT);
        btn.setBackground(SIDEBAR_BG);
        btn.setMaximumSize(new Dimension(220, 42));
        btn.setPreferredSize(new Dimension(220, 42));
        btn.setHorizontalAlignment(SwingConstants.LEFT);
        btn.setBorder(BorderFactory.createEmptyBorder(8, 20, 8, 15));
        btn.setFocusPainted(false);
        btn.setBorderPainted(false);
        btn.setContentAreaFilled(false);
        btn.setOpaque(true);
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));

        // Hover effect
        btn.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override
            public void mouseEntered(java.awt.event.MouseEvent e) {
                if (btn.getBackground() != SIDEBAR_ACTIVE) {
                    btn.setBackground(SIDEBAR_HOVER);
                }
            }

            @Override
            public void mouseExited(java.awt.event.MouseEvent e) {
                if (btn.getBackground() != SIDEBAR_ACTIVE) {
                    btn.setBackground(SIDEBAR_BG);
                }
            }
        });

        return btn;
    }

    /**
     * Update styling tombol sidebar aktif.
     */
    private void updateSidebarActive(int index) {
        for (int i = 0; i < sidebarButtons.length; i++) {
            if (i == index) {
                sidebarButtons[i].setBackground(SIDEBAR_ACTIVE);
                sidebarButtons[i].setForeground(Color.WHITE);
                sidebarButtons[i].setFont(new Font("Segoe UI", Font.BOLD, 13));
            } else {
                sidebarButtons[i].setBackground(SIDEBAR_BG);
                sidebarButtons[i].setForeground(SIDEBAR_TEXT);
                sidebarButtons[i].setFont(new Font("Segoe UI", Font.PLAIN, 13));
            }
        }
        activeIndex = index;
    }

    /**
     * Refresh data panel saat ditampilkan.
     */
    private void refreshPanel(String cardName) {
        switch (cardName) {
            case CARD_DASHBOARD -> dashboardPanel.refreshData();
            case CARD_KARYAWAN -> karyawanPanel.refreshData();
            case CARD_SHIFT -> shiftPanel.refreshData();
            case CARD_CUTI -> cutiPanel.refreshData();
            case CARD_JADWAL -> jadwalPanel.refreshData();
        }
    }
}
