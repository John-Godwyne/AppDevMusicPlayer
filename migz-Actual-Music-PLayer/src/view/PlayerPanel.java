package view;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;

public class PlayerPanel extends JPanel {
    private JLabel imageLabel;
    private JTextArea lyricsArea;
    private JList<String> songList;
    private DefaultListModel<String> listModel;
    
    // Buttons
    public JButton playButton;
    public JButton pauseButton;
    public JButton stopButton;

    public PlayerPanel() {
        // Use BorderLayout for the main panel
        setLayout(new BorderLayout(10, 10));
        setBorder(new EmptyBorder(10, 10, 10, 10)); // Add padding

        // --- WEST: Song List ---
        listModel = new DefaultListModel<>();
        songList = new JList<>(listModel);
        songList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        songList.setFont(new Font("Arial", Font.PLAIN, 14));
        JScrollPane listScrollPane = new JScrollPane(songList);
        listScrollPane.setPreferredSize(new Dimension(200, 0));
        listScrollPane.setBorder(BorderFactory.createTitledBorder("Playlist"));
        add(listScrollPane, BorderLayout.WEST);

        // --- CENTER: Album Art ---
        imageLabel = new JLabel("No Song Selected", SwingConstants.CENTER);
        imageLabel.setPreferredSize(new Dimension(300, 300));
        imageLabel.setBorder(BorderFactory.createLineBorder(Color.GRAY));
        add(imageLabel, BorderLayout.CENTER);

        // --- EAST: Lyrics ---
        lyricsArea = new JTextArea();
        lyricsArea.setEditable(false);
        lyricsArea.setLineWrap(true);
        lyricsArea.setWrapStyleWord(true);
        lyricsArea.setFont(new Font("Serif", Font.PLAIN, 14));
        JScrollPane lyricsScrollPane = new JScrollPane(lyricsArea);
        lyricsScrollPane.setPreferredSize(new Dimension(250, 0));
        lyricsScrollPane.setBorder(BorderFactory.createTitledBorder("Lyrics"));
        add(lyricsScrollPane, BorderLayout.EAST);

        // --- SOUTH: Controls ---
        JPanel controlsPanel = new JPanel();
        controlsPanel.setLayout(new FlowLayout(FlowLayout.CENTER, 20, 10)); // FlowLayout for buttons
        
        playButton = new JButton("▶ Play");
        pauseButton = new JButton("⏸ Pause");
        stopButton = new JButton("⏹ Stop");
        
        // Make buttons look slightly bigger
        Font buttonFont = new Font("Arial", Font.BOLD, 14);
        playButton.setFont(buttonFont);
        pauseButton.setFont(buttonFont);
        stopButton.setFont(buttonFont);

        controlsPanel.add(playButton);
        controlsPanel.add(pauseButton);
        controlsPanel.add(stopButton);
        
        add(controlsPanel, BorderLayout.SOUTH);
    }

    // Getters so the Controller can access the UI components
    public JList<String> getSongList() { return songList; }
    public DefaultListModel<String> getListModel() { return listModel; }
    public JLabel getImageLabel() { return imageLabel; }
    public JTextArea getLyricsArea() { return lyricsArea; }
}