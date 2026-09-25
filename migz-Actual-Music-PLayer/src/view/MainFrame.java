package view;

import javax.swing.*;

public class MainFrame extends JFrame {
    public PlayerPanel playerPanel;

    public MainFrame() {
        setTitle("Migz Music Player");
        setSize(1000, 650);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        
        playerPanel = new PlayerPanel();
        add(playerPanel);
    }
}