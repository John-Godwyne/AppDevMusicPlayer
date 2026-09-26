package view;

import java.awt.*;
import javax.swing.*;

public class PlayerPanel extends JPanel {
    public JLabel imageLabel;
    public JTextArea lyricsArea;
    public JList<String> songList;
    public DefaultListModel<String> listModel;
    public JButton playButton, pauseButton, stopButton, prevButton, nextButton; // ADDED prevButton, nextButton
    public JSlider progressSlider; // ADDED

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

        // SOUTH: Slider + Controls
        JPanel southPanel = new JPanel(new BorderLayout(5, 5)); // ADDED wrapper

        progressSlider = new JSlider(0, 100, 0); // ADDED
        southPanel.add(progressSlider, BorderLayout.NORTH); // ADDED

        JPanel controls = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 10));
        prevButton = new JButton("⏮ Prev"); // ADDED
        playButton = new JButton("▶ Play");
        pauseButton = new JButton("⏸ Pause");
        stopButton = new JButton("⏹ Stop");
        nextButton = new JButton("⏭ Next"); // ADDED

        controls.add(prevButton); // ADDED
        controls.add(playButton);
        controls.add(pauseButton);
        controls.add(stopButton);
        controls.add(nextButton); // ADDED

        southPanel.add(controls, BorderLayout.CENTER); // CHANGED from add(controls, BorderLayout.SOUTH)
        add(southPanel, BorderLayout.SOUTH); // CHANGED
    }
}