package view;

import java.awt.*;
import javax.swing.*;
public class MainFrame extends JFrame {
    public PlayerPanel playerPanel;

    public MainFrame() {
        
        setTitle("Migz Music Player");
        setSize(1100, 700); // Slightly bigger for better spacing
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        
        // Set the background color of the frame
        getContentPane().setBackground(new Color(30, 30, 30));
        
        playerPanel = new PlayerPanel();
        add(playerPanel);
    }
}