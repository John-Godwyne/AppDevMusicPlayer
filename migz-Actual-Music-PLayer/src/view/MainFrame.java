package view;

import model.Song;
import persistence.DatabaseManager;

import javax.swing.*;
import java.awt.*;
import java.util.List;

public class MainFrame extends JFrame {
    public PlayerPanel playerPanel;
    public CategoryPanel categoryPanel;
    private JTabbedPane tabs;

    public MainFrame() {
        setTitle("Migz Music Player");
        setSize(1100, 700);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setResizable(false);

        getContentPane().setBackground(new Color(2, 10, 30));

        List<Song> songs = DatabaseManager.getAllSongs();

        tabs = new JTabbedPane();
        tabs.setFont(new Font("Segoe UI", Font.BOLD, 13));
        tabs.setBackground(new Color(2, 10, 30));
        tabs.setForeground(new Color(230, 250, 255));

        playerPanel = new PlayerPanel();
        categoryPanel = new CategoryPanel(songs);

        tabs.addTab("// PLAYER",               playerPanel);
        tabs.addTab("// BROWSE BY CATEGORY",   categoryPanel);

        add(tabs);
    }

    /** Switches to the Player tab (index 0). */
    public void switchToPlayerTab() {
        if (tabs != null) {
            tabs.setSelectedIndex(0);
        }
    }
}