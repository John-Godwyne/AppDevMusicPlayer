package view;

import java.awt.*;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;

public class PlayerPanel extends JPanel {
    public JLabel imageLabel;
    public JLabel nowPlayingLabel;
    public JLabel timeLabel;
    public JTextArea lyricsArea;
    public JList<String> songList;
    public DefaultListModel<String> listModel;
    public JButton playButton, pauseButton, stopButton, prevButton, nextButton;
    public JSlider progressSlider;

    // Theme Colors
    private final Color BG_DARK = new Color(24, 24, 28);
    private final Color BG_PANEL = new Color(38, 38, 44);
    private final Color BG_ELEVATED = new Color(52, 52, 60);
    private final Color TEXT_LIGHT = new Color(235, 235, 240);
    private final Color TEXT_MUTED = new Color(150, 150, 160);
    private final Color ACCENT = new Color(88, 101, 242);       // Discord-like blue
    private final Color ACCENT_HOVER = new Color(114, 125, 245);
    private final Color BORDER = new Color(70, 70, 80);

    public PlayerPanel() {
        setLayout(new BorderLayout(15, 15));
        setBorder(new EmptyBorder(18, 18, 18, 18));
        setBackground(BG_DARK);

        // ================= WEST: Song List =================
        listModel = new DefaultListModel<>();
        songList = new JList<>(listModel);
        songList.setBackground(BG_PANEL);
        songList.setForeground(TEXT_LIGHT);
        songList.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        songList.setSelectionBackground(ACCENT);
        songList.setSelectionForeground(Color.WHITE);
        songList.setFixedCellHeight(38);
        songList.setBorder(new EmptyBorder(8, 8, 8, 8));
        songList.setOpaque(true);
        
        JScrollPane listScroll = new JScrollPane(songList);
        listScroll.setPreferredSize(new Dimension(260, 0));
        listScroll.setBorder(BorderFactory.createTitledBorder(
            new LineBorder(BORDER, 1, true), "🎵 Playlist", 0, 0, 
            new Font("Segoe UI", Font.BOLD, 14), TEXT_LIGHT));
        listScroll.getViewport().setBackground(BG_PANEL);
        listScroll.setBackground(BG_PANEL);
        add(listScroll, BorderLayout.WEST);

        // ================= CENTER: Album Art =================
        JPanel centerPanel = new JPanel(new BorderLayout(0, 10));
        centerPanel.setBackground(BG_DARK);
        
        imageLabel = new JLabel("Select a song to begin", SwingConstants.CENTER);
        imageLabel.setFont(new Font("Segoe UI", Font.ITALIC, 16));
        imageLabel.setForeground(TEXT_MUTED);
        imageLabel.setBackground(BG_PANEL);
        imageLabel.setOpaque(true);
        imageLabel.setBorder(new LineBorder(BORDER, 1, true));
        centerPanel.add(imageLabel, BorderLayout.CENTER);
        
        // Now Playing label
        nowPlayingLabel = new JLabel("♪ Nothing playing", SwingConstants.CENTER);
        nowPlayingLabel.setFont(new Font("Segoe UI", Font.BOLD, 16));
        nowPlayingLabel.setForeground(TEXT_LIGHT);
        nowPlayingLabel.setBorder(new EmptyBorder(8, 0, 0, 0));
        centerPanel.add(nowPlayingLabel, BorderLayout.SOUTH);
        
        add(centerPanel, BorderLayout.CENTER);

        // ================= EAST: Lyrics =================
        lyricsArea = new JTextArea();
        lyricsArea.setEditable(false);
        lyricsArea.setLineWrap(true);
        lyricsArea.setWrapStyleWord(true);
        lyricsArea.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        lyricsArea.setBackground(BG_PANEL);
        lyricsArea.setForeground(TEXT_LIGHT);
        lyricsArea.setCaretColor(TEXT_LIGHT);
        lyricsArea.setMargin(new Insets(12, 12, 12, 12));
        lyricsArea.setOpaque(true);
        
        JScrollPane lyricsScroll = new JScrollPane(lyricsArea);
        lyricsScroll.setPreferredSize(new Dimension(300, 0));
        lyricsScroll.setBorder(BorderFactory.createTitledBorder(
            new LineBorder(BORDER, 1, true), "📜 Lyrics", 0, 0, 
            new Font("Segoe UI", Font.BOLD, 14), TEXT_LIGHT));
        lyricsScroll.getViewport().setBackground(BG_PANEL);
        lyricsScroll.setBackground(BG_PANEL);
        add(lyricsScroll, BorderLayout.EAST);

        // ================= SOUTH: Slider + Controls =================
        JPanel southPanel = new JPanel(new BorderLayout(10, 12));
        southPanel.setBackground(BG_DARK);

        // Time + Slider
        JPanel sliderPanel = new JPanel(new BorderLayout(10, 0));
        sliderPanel.setBackground(BG_DARK);
        
        timeLabel = new JLabel("00:00 / 00:00");
        timeLabel.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        timeLabel.setForeground(TEXT_MUTED);
        sliderPanel.add(timeLabel, BorderLayout.EAST);
        
        progressSlider = new JSlider(0, 100, 0);
        progressSlider.setBackground(BG_DARK);
        progressSlider.setForeground(ACCENT);
        progressSlider.setFocusable(false);
        sliderPanel.add(progressSlider, BorderLayout.CENTER);
        
        southPanel.add(sliderPanel, BorderLayout.NORTH);

        // Buttons
        JPanel controls = new JPanel(new FlowLayout(FlowLayout.CENTER, 15, 5));
        controls.setBackground(BG_DARK);

        prevButton = createStyledButton("⏮", 60);
        playButton = createStyledButton("▶ Play", 110);
        pauseButton = createStyledButton("⏸ Pause", 110);
        stopButton = createStyledButton("⏹ Stop", 110);
        nextButton = createStyledButton("⏭", 60);

        controls.add(prevButton);
        controls.add(playButton);
        controls.add(pauseButton);
        controls.add(stopButton);
        controls.add(nextButton);

        southPanel.add(controls, BorderLayout.CENTER);
        add(southPanel, BorderLayout.SOUTH);
    }

    // Helper method to create modern-looking buttons
    private JButton createStyledButton(String text, int width) {
        JButton button = new JButton(text);
        button.setFont(new Font("Segoe UI", Font.BOLD, 13));
        button.setForeground(Color.WHITE);
        button.setBackground(ACCENT);
        button.setFocusPainted(false);
        button.setBorderPainted(false);
        button.setOpaque(true);
        button.setContentAreaFilled(true);
        button.setPreferredSize(new Dimension(width, 42));
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));
        
        button.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                button.setBackground(ACCENT_HOVER);
            }
            public void mouseExited(java.awt.event.MouseEvent evt) {
                button.setBackground(ACCENT);
            }
        });
        return button;
    }
}