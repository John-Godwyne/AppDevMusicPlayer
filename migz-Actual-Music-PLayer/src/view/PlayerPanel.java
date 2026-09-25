package view;

import javax.swing.*;
import java.awt.*;

public class PlayerPanel extends JPanel {
    public JLabel imageLabel;
    public JTextArea lyricsArea;
    public JList<String> songList;
    public DefaultListModel<String> listModel;
    public JButton playButton, pauseButton, stopButton;

    public PlayerPanel() {
        setLayout(new BorderLayout(10, 10));
        setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        // WEST: Song List
        listModel = new DefaultListModel<>();
        songList = new JList<>(listModel);
        JScrollPane listScroll = new JScrollPane(songList);
        listScroll.setPreferredSize(new Dimension(200, 0));
        listScroll.setBorder(BorderFactory.createTitledBorder("Playlist"));
        add(listScroll, BorderLayout.WEST);

        // CENTER: Image
        imageLabel = new JLabel("No Song Selected", SwingConstants.CENTER);
        imageLabel.setBorder(BorderFactory.createLineBorder(Color.GRAY));
        add(imageLabel, BorderLayout.CENTER);

        // EAST: Lyrics
        lyricsArea = new JTextArea();
        lyricsArea.setEditable(false);
        lyricsArea.setLineWrap(true);
        lyricsArea.setWrapStyleWord(true);
        JScrollPane lyricsScroll = new JScrollPane(lyricsArea);
        lyricsScroll.setPreferredSize(new Dimension(250, 0));
        lyricsScroll.setBorder(BorderFactory.createTitledBorder("Lyrics"));
        add(lyricsScroll, BorderLayout.EAST);

        // SOUTH: Controls
        JPanel controls = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 10));
        playButton = new JButton("▶ Play");
        pauseButton = new JButton("⏸ Pause");
        stopButton = new JButton("⏹ Stop");
        controls.add(playButton);
        controls.add(pauseButton);
        controls.add(stopButton);
        add(controls, BorderLayout.SOUTH);
    }
}