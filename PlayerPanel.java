package view;

import java.awt.*;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;
import view.ImagePanel;

public class PlayerPanel extends JPanel {
    public ImagePanel imagePanel;
    public JLabel nowPlayingLabel;
    public JLabel timeLabel;
    public JLabel volumeLabel;         // <-- ADDED: volume % display
    public JTextArea lyricsArea;
    public JList<String> songList;
    public DefaultListModel<String> listModel;
    public JButton playButton, stopButton, prevButton, nextButton;
    public JSlider progressSlider;
    public JSlider volumeSlider;       // <-- ADDED: volume slider

    // Theme Colors
    private final Color BG_DARK = new Color(24, 24, 28);
    private final Color BG_PANEL = new Color(38, 38, 44);
    private final Color TEXT_LIGHT = new Color(235, 235, 240);
    private final Color TEXT_MUTED = new Color(150, 150, 160);
    private final Color ACCENT = new Color(88, 101, 242);
    private final Color ACCENT_HOVER = new Color(114, 125, 245);
    private final Color BORDER = new Color(70, 70, 80);
    private final Font symbol = new Font(Font.DIALOG, Font.BOLD, 14);

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
            new LineBorder(BORDER, 1, true), "𝅘𝅥𝅮 Playlist", 0, 0, 
            symbol, TEXT_LIGHT));
        listScroll.getViewport().setBackground(BG_PANEL);
        listScroll.setBackground(BG_PANEL);
        add(listScroll, BorderLayout.WEST);

        // ================= CENTER: Album Art =================
        JPanel centerPanel = new JPanel(new BorderLayout(0, 10));
        centerPanel.setBackground(BG_DARK);
        // Prefer to be as wide as possible
        centerPanel.setPreferredSize(new Dimension(500, 0));
        
        imagePanel = new ImagePanel();
        imagePanel.setPlaceholder("Select a song to begin");
        // Let ImagePanel take all remaining vertical space in the center panel
        imagePanel.setPreferredSize(new Dimension(400, 400));
        centerPanel.add(imagePanel, BorderLayout.CENTER);
        
        // Now Playing label
        nowPlayingLabel = new JLabel("𝅘𝅥𝅮 Nothing playing", SwingConstants.CENTER);
        nowPlayingLabel.setFont(symbol);
        nowPlayingLabel.setForeground(TEXT_LIGHT);
        nowPlayingLabel.setBorder(new EmptyBorder(8, 0, 0, 0));
        centerPanel.add(nowPlayingLabel, BorderLayout.SOUTH);
        
        // Add centerPanel to the middle of the outer panel so it expands
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
            new LineBorder(BORDER, 1, true), "🗎 Lyrics", 0, 0, 
            symbol, TEXT_LIGHT));
        lyricsScroll.getViewport().setBackground(BG_PANEL);
        lyricsScroll.setBackground(BG_PANEL);
        add(lyricsScroll, BorderLayout.EAST);

        // ================= SOUTH: Slider + Volume + Controls =================
        JPanel southPanel = new JPanel(new BorderLayout(10, 12));
        southPanel.setBackground(BG_DARK);

        // Row 1: Progress slider (center) + Time (east) + Volume (west)
        JPanel sliderPanel = new JPanel(new BorderLayout(10, 0));
        sliderPanel.setBackground(BG_DARK);

        // ---- Progress time on the right ----
        timeLabel = new JLabel("00:00 / 00:00");
        timeLabel.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        timeLabel.setForeground(TEXT_MUTED);
        sliderPanel.add(timeLabel, BorderLayout.EAST);

        // ---- Progress slider in center ----
        progressSlider = new JSlider(0, 100, 0);
        progressSlider.setBackground(BG_DARK);
        progressSlider.setForeground(ACCENT);
        progressSlider.setFocusable(false);
        sliderPanel.add(progressSlider, BorderLayout.CENTER);

        // ---- Volume group on the left ----
        JPanel volumePanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 0));
        volumePanel.setBackground(BG_DARK);

        JLabel volumeIcon = new JLabel("🔊");
        volumeIcon.setFont(new Font(Font.DIALOG, Font.PLAIN, 16));
        volumeIcon.setForeground(TEXT_LIGHT);
        volumePanel.add(volumeIcon);

        volumeSlider = new JSlider(0, 100, 80); // default 80%
        volumeSlider.setPreferredSize(new Dimension(120, 25));
        volumeSlider.setBackground(BG_DARK);
        volumeSlider.setForeground(ACCENT);
        volumeSlider.setFocusable(false);
        volumePanel.add(volumeSlider);

        volumeLabel = new JLabel("80%");
        volumeLabel.setFont(new Font("Segoe UI", Font.BOLD, 12));
        volumeLabel.setForeground(TEXT_MUTED);
        volumeLabel.setPreferredSize(new Dimension(40, 20)); // fixed width to stop jumpy layout
        volumePanel.add(volumeLabel);

        sliderPanel.add(volumePanel, BorderLayout.WEST);

        southPanel.add(sliderPanel, BorderLayout.NORTH);

        // Row 2: Playback buttons
        JPanel controls = new JPanel(new FlowLayout(FlowLayout.CENTER, 15, 5));
        controls.setBackground(BG_DARK);

        prevButton = createStyledButton("⏮", 60);
        playButton = createStyledButton("▶ Play", 120);
        stopButton = createStyledButton("⏹ Stop", 110);
        nextButton = createStyledButton("⏭", 60);

        controls.add(prevButton);
        controls.add(playButton);
        controls.add(stopButton);
        controls.add(nextButton);

        southPanel.add(controls, BorderLayout.CENTER);
        add(southPanel, BorderLayout.SOUTH);
    }

    // Helper method to create modern-looking buttons
    private JButton createStyledButton(String text, int width) {
        JButton button = new JButton(text);
        button.setFont(new Font(Font.DIALOG, Font.BOLD, 13));
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