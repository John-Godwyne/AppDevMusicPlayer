package view;

import javax.swing.*;

public class MainFrame extends JFrame {
    private PlayerPanel playerPanel;

    public MainFrame() {
        setTitle("Migz Music Player");
        setSize(1000, 650);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null); // Center on screen
        setLayout(new java.awt.BorderLayout()); // Main frame layout

        playerPanel = new PlayerPanel();
        add(playerPanel, java.awt.BorderLayout.CENTER);
    }

    public PlayerPanel getPlayerPanel() {
        return playerPanel;
    }
}