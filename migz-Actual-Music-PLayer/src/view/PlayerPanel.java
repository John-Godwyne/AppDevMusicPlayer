package view;

import java.awt.*;
import java.awt.event.MouseEvent;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.plaf.basic.BasicScrollBarUI;
import javax.swing.plaf.basic.BasicSliderUI;

/**
 * Persona 3 FES themed player panel (sleek HUD style).
 * Call setPlaying(true/false) from your controller to toggle "Dark Hour" mode,
 * which tints the whole UI from cyan to green while a song plays.
 */
public class PlayerPanel extends JPanel {
    public ImagePanel imagePanel;
    public JLabel nowPlayingLabel;
    public JLabel timeLabel;
    public JLabel volumeLabel;
    public JTextArea lyricsArea;
    public JList<String> songList;
    public DefaultListModel<String> listModel;
    public JButton playButton, stopButton, prevButton, nextButton;
    public JSlider progressSlider;
    public JSlider volumeSlider;

    // ---------- Editable words ----------
    private static final String TXT_TRACKS      = "// TRACK SELECT";
    private static final String TXT_LYRICS      = "// LYRICS DATA";
    private static final String TXT_IDLE        = "The Moment is Dark and Silent";
    private static final String TXT_PLACEHOLDER = "Select a Tune to Vibe";

    // ---------- P3 FES palette ----------
    private final Color BG_TOP          = new Color(1, 6, 20);      // midnight
    private final Color BG_BOTTOM       = new Color(4, 26, 64);     // menu blue
    private final Color BG_BOTTOM_DH    = new Color(2, 36, 42);     // Dark Hour teal-black
    private final Color PANEL_FILL      = new Color(5, 18, 46, 205);
    private final Color NAVY            = new Color(2, 10, 30);
    private final Color TEXT_LIGHT      = new Color(230, 250, 255);
    private final Color TEXT_MUTED      = new Color(120, 170, 200);
    private final Color ICE             = new Color(190, 240, 255); // near-white cyan
    private final Color MOON            = new Color(255, 236, 160); // moonlight yellow
    private final Color ACCENT          = new Color(0, 170, 230);
    private final Color ACCENT_HOVER    = new Color(120, 225, 255);
    private final Color DARK_HOUR       = new Color(120, 255, 170);
    private final Color TRACK_BG        = new Color(20, 50, 100);

    private static final String FONT_FAMILY = pickFont();
    private boolean playing = false;

    private static String pickFont() {
        Set<String> available = new HashSet<>(Arrays.asList(
            GraphicsEnvironment.getLocalGraphicsEnvironment().getAvailableFontFamilyNames()));
        for (String f : new String[]{"Bahnschrift", "Rajdhani", "Teko", "Segoe UI"}) {
            if (available.contains(f)) return f;
        }
        return Font.SANS_SERIF;
    }

    private Font font(int style, int size) { return new Font(FONT_FAMILY, style, size); }
    private Color accent()   { return playing ? DARK_HOUR : ACCENT; }
    private Color onAccent() { return playing ? NAVY : Color.WHITE; }
    private Color alpha(Color c, int a) { return new Color(c.getRed(), c.getGreen(), c.getBlue(), a); }

    public PlayerPanel() {
        setLayout(new BorderLayout(15, 15));
        setBorder(new EmptyBorder(18, 18, 18, 18));
        setBackground(NAVY);

        // ================= WEST: Track list =================
        listModel = new DefaultListModel<>();
        songList = new JList<String>(listModel) {
            @Override
            public String getToolTipText(MouseEvent e) {
                int i = locationToIndex(e.getPoint());
                return (i >= 0 && i < listModel.size()) ? listModel.get(i) : null;
            }
        };
        ToolTipManager.sharedInstance().registerComponent(songList);
        songList.setOpaque(false);
        songList.setFixedCellHeight(40);
        songList.setCellRenderer(new FloorRenderer());

        HudPanel listBox = new HudPanel(TXT_TRACKS, plainScroll(songList));
        listBox.setPreferredSize(new Dimension(270, 0));
        add(listBox, BorderLayout.WEST);

        // ================= CENTER: Album art =================
        JPanel centerPanel = new JPanel(new BorderLayout(0, 6));
        centerPanel.setOpaque(false);
        centerPanel.setPreferredSize(new Dimension(500, 0));

        imagePanel = new ImagePanel();
        imagePanel.setPlaceholder(TXT_PLACEHOLDER);
        imagePanel.setBackground(new Color(4, 16, 42));
        imagePanel.setBorder(BorderFactory.createLineBorder(new Color(0, 110, 170), 1));
        imagePanel.setPreferredSize(new Dimension(400, 400));
        centerPanel.add(new ArtFrame(imagePanel), BorderLayout.CENTER);

        nowPlayingLabel = new JLabel(TXT_IDLE, SwingConstants.CENTER) {
            @Override
            protected void paintComponent(Graphics g) {
                setForeground(accent());
                super.paintComponent(g);
                Graphics2D g2 = (Graphics2D) g.create();
                int y = getHeight() - 1;
                g2.setColor(alpha(accent(), 110));
                g2.fillRect(0, y, getWidth(), 1);
                g2.setColor(accent());
                g2.fillRect(0, y - 1, 40, 2);
                g2.fillRect(getWidth() - 40, y - 1, 40, 2);
                g2.dispose();
            }
        };
        nowPlayingLabel.setFont(font(Font.BOLD | Font.ITALIC, 16));
        nowPlayingLabel.setBorder(new EmptyBorder(6, 0, 8, 0));
        centerPanel.add(nowPlayingLabel, BorderLayout.SOUTH);
        add(centerPanel, BorderLayout.CENTER);

        // ================= EAST: Lyrics =================
        lyricsArea = new JTextArea();
        lyricsArea.setEditable(false);
        lyricsArea.setLineWrap(true);
        lyricsArea.setWrapStyleWord(true);
        lyricsArea.setFont(font(Font.PLAIN, 15));
        lyricsArea.setOpaque(false);
        lyricsArea.setForeground(TEXT_LIGHT);
        lyricsArea.setCaretColor(TEXT_LIGHT);
        lyricsArea.setMargin(new Insets(10, 12, 10, 12));

        HudPanel lyricsBox = new HudPanel(TXT_LYRICS, plainScroll(lyricsArea));
        lyricsBox.setPreferredSize(new Dimension(310, 0));
        add(lyricsBox, BorderLayout.EAST);

        // ================= SOUTH: Sliders + Controls =================
        JPanel southPanel = new JPanel(new BorderLayout(10, 10));
        southPanel.setOpaque(false);

        JPanel sliderPanel = new JPanel(new BorderLayout(12, 0));
        sliderPanel.setOpaque(false);

        timeLabel = new JLabel("00:00 / 00:00");
        timeLabel.setFont(font(Font.BOLD, 12));
        timeLabel.setForeground(TEXT_MUTED);
        sliderPanel.add(timeLabel, BorderLayout.EAST);

        progressSlider = new JSlider(0, 100, 0);
        progressSlider.setOpaque(false);
        progressSlider.setFocusable(false);
        progressSlider.setUI(new DiamondSliderUI(progressSlider));
        sliderPanel.add(progressSlider, BorderLayout.CENTER);

        JPanel volumePanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 0));
        volumePanel.setOpaque(false);

        JLabel volumeIcon = new JLabel("VOL");
        volumeIcon.setFont(font(Font.BOLD | Font.ITALIC, 12));
        volumeIcon.setForeground(TEXT_MUTED);
        volumePanel.add(volumeIcon);

        volumeSlider = new JSlider(0, 100, 80);
        volumeSlider.setPreferredSize(new Dimension(120, 25));
        volumeSlider.setOpaque(false);
        volumeSlider.setFocusable(false);
        volumeSlider.setUI(new DiamondSliderUI(volumeSlider));
        volumePanel.add(volumeSlider);

        volumeLabel = new JLabel("80%");
        volumeLabel.setFont(font(Font.BOLD, 12));
        volumeLabel.setForeground(TEXT_MUTED);
        volumeLabel.setPreferredSize(new Dimension(40, 20));
        volumePanel.add(volumeLabel);

        sliderPanel.add(volumePanel, BorderLayout.WEST);
        southPanel.add(sliderPanel, BorderLayout.NORTH);

        JPanel controls = new JPanel(new FlowLayout(FlowLayout.CENTER, 12, 5));
        controls.setOpaque(false);

        prevButton  = createStyledButton("[<<] PREV", 100);
        playButton  = createStyledButton("[>] PLAY", 120);
        stopButton  = createStyledButton("[||] STOP", 105);
        nextButton  = createStyledButton("NEXT [>>]", 100);

        controls.add(prevButton);
        controls.add(playButton);
        controls.add(stopButton);
        controls.add(nextButton);

        playButton.addActionListener(e -> setPlaying(true));
        nextButton.addActionListener(e -> setPlaying(true));
        prevButton.addActionListener(e -> setPlaying(true));
        stopButton.addActionListener(e -> setPlaying(false));

        southPanel.add(controls, BorderLayout.CENTER);
        add(southPanel, BorderLayout.SOUTH);
    }

    // ================= Dark Hour mode =================

    /** true = song playing (green Dark Hour tint), false = stopped/paused (cyan). */
    public void setPlaying(boolean isPlaying) {
        this.playing = isPlaying;
        progressSlider.repaint();
        volumeSlider.repaint();
        repaint();
    }
    // ================= Background =================

    @Override
    protected void paintComponent(Graphics g) {
        Graphics2D g2 = (Graphics2D) g.create();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        int w = getWidth(), h = getHeight();

        g2.setPaint(new GradientPaint(0, 0, BG_TOP, 0, h, playing ? BG_BOTTOM_DH : BG_BOTTOM));
        g2.fillRect(0, 0, w, h);

        // faint diagonal shards, like the P3 menu backdrop
        g2.setColor(alpha(playing ? DARK_HOUR : ACCENT, 9));
        int dx = (int) (h * 0.6);
        for (int x = -dx; x < w; x += 110) {
            g2.fillPolygon(new int[]{x, x + 26, x + 26 + dx, x + dx},
                           new int[]{h, h, 0, 0}, 4);
        }
        g2.dispose();
    }

    // ================= Helpers =================

    private JScrollPane plainScroll(Component view) {
        JScrollPane sp = new JScrollPane(view,
            ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED,
            ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
        sp.setBorder(null);
        sp.setOpaque(false);
        sp.getViewport().setOpaque(false);
        // simple scrolling avoids ghosting over translucent backgrounds
        sp.getViewport().setScrollMode(JViewport.SIMPLE_SCROLL_MODE);
        JScrollBar vb = sp.getVerticalScrollBar();
        vb.setUI(new SlimScrollBarUI());
        vb.setOpaque(false);
        vb.setPreferredSize(new Dimension(8, 0));
        vb.setUnitIncrement(14);
        return sp;
    }

    private JButton createStyledButton(String text, int width) {
        JButton button = new JButton(text) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
                                    RenderingHints.VALUE_ANTIALIAS_ON);
                int w = getWidth() - 1, h = getHeight() - 1, c = 10;
                Polygon shape = new Polygon(
                    new int[]{c, w, w, w - c, 0, 0},
                    new int[]{0, 0, h - c, h, h, c}, 6);

                boolean hover = getModel().isRollover();
                boolean pressed = getModel().isArmed();
                g2.setColor(pressed ? accent().darker() : hover ? Color.WHITE : accent());
                g2.fillPolygon(shape);
                g2.setColor(alpha(ICE, 110));
                g2.drawPolygon(shape);
                g2.dispose();

                setForeground(hover && !pressed ? NAVY : onAccent());
                super.paintComponent(g);
            }
        };
        button.setFont(font(Font.BOLD | Font.ITALIC, 14));
        button.setFocusPainted(false);
        button.setBorderPainted(false);
        button.setContentAreaFilled(false);
        button.setOpaque(false);
        button.setRolloverEnabled(true);
        button.setPreferredSize(new Dimension(width, 42));
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));
        return button;
    }

    // ================= HUD panel (thin border + header + left bar) =================

    private class HudPanel extends JPanel {
        private final String title;

        HudPanel(String title, JComponent content) {
            super(new BorderLayout());
            this.title = title;
            setOpaque(false);
            setBorder(new EmptyBorder(34, 10, 6, 6));
            add(content, BorderLayout.CENTER);
        }

        @Override
        protected void paintComponent(Graphics g) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING,
                                RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
            int w = getWidth(), h = getHeight();

            g2.setColor(PANEL_FILL);
            g2.fillRect(0, 0, w, h);

            g2.setColor(alpha(accent(), 90));
            g2.drawRect(0, 0, w - 1, h - 1);

            g2.setColor(accent());
            g2.fillRect(0, 0, 46, 2);          // bright tab on the top edge
            g2.fillRect(2, 32, 4, h - 35);     // left accent bar

            g2.setFont(font(Font.BOLD | Font.ITALIC, 14));
            FontMetrics fm = g2.getFontMetrics();
            int x = 12, y = 22;
            String rest = title;
            if (title.startsWith("//")) {
                g2.setColor(ICE);
                g2.drawString("//", x, y);
                x += fm.stringWidth("//") + fm.charWidth(' ');
                rest = title.substring(2).trim();
            }
            g2.setColor(accent());
            g2.drawString(rest, x, y);
            g2.dispose();
        }
    }

    // ================= Album art frame (double border + glow) =================

    private class ArtFrame extends JPanel {
        ArtFrame(JComponent content) {
            super(new BorderLayout());
            setOpaque(false);
            setBorder(new EmptyBorder(11, 11, 11, 11));
            add(content, BorderLayout.CENTER);
        }

        @Override
        protected void paintComponent(Graphics g) {
            Graphics2D g2 = (Graphics2D) g.create();
            int w = getWidth(), h = getHeight();
            for (int k = 0; k < 4; k++) {          // soft outer glow
                g2.setColor(alpha(accent(), 12 + k * 14));
                g2.drawRect(k, k, w - 1 - 2 * k, h - 1 - 2 * k);
            }
            g2.setColor(accent());
            g2.setStroke(new BasicStroke(2f));
            g2.drawRect(5, 5, w - 11, h - 11);
            g2.dispose();
        }
    }

    // ================= Track renderer =================

    private class FloorRenderer extends JComponent implements ListCellRenderer<String> {
        private String number = "", title = "";
        private boolean selected;
        private int row;

        @Override
        public Component getListCellRendererComponent(JList<? extends String> list,
                String value, int index, boolean isSelected, boolean cellHasFocus) {
            number = String.format("%02d //", index + 1);
            title = value == null ? "" : value;
            selected = isSelected;
            row = index;
            return this;
        }

        @Override
        public Dimension getPreferredSize() { return new Dimension(100, 40); }

        @Override
        protected void paintComponent(Graphics g) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING,
                                RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
            int w = getWidth(), h = getHeight();

            if (selected) {
                g2.setColor(accent());
                g2.fillRect(0, 0, w, h);
                g2.setColor(Color.WHITE);
                g2.fillRect(0, 0, 5, h);
                // play marker drawn as a shape (no font glyph needed)
                g2.setColor(onAccent());
                int cy = h / 2;
                g2.fillPolygon(new int[]{14, 14, 22}, new int[]{cy - 5, cy + 5, cy}, 3);
            } else if (row % 2 == 1) {
                g2.setColor(new Color(40, 110, 200, 28));
                g2.fillRect(0, 0, w, h);
            }

            g2.setFont(font(selected ? Font.BOLD : Font.PLAIN, 14));
            FontMetrics fm = g2.getFontMetrics();
            int baseline = (h + fm.getAscent() - fm.getDescent()) / 2;
            int x = 32;

            g2.setColor(selected ? onAccent() : MOON);
            g2.drawString(number, x, baseline);
            x += fm.stringWidth(number) + 7;

            g2.setColor(selected ? onAccent() : TEXT_LIGHT);
            g2.drawString(ellipsize(title, fm, w - x - 10), x, baseline);
            g2.dispose();
        }

        private String ellipsize(String s, FontMetrics fm, int maxW) {
            if (fm.stringWidth(s) <= maxW) return s;
            int end = s.length();
            while (end > 0 && fm.stringWidth(s.substring(0, end) + "...") > maxW) end--;
            return s.substring(0, end) + "...";
        }
    }

    // ================= Slim scrollbar =================

    private class SlimScrollBarUI extends BasicScrollBarUI {
        @Override protected JButton createDecreaseButton(int o) { return zeroButton(); }
        @Override protected JButton createIncreaseButton(int o) { return zeroButton(); }

        private JButton zeroButton() {
            JButton b = new JButton();
            Dimension d = new Dimension(0, 0);
            b.setPreferredSize(d);
            b.setMinimumSize(d);
            b.setMaximumSize(d);
            return b;
        }

        @Override
        protected void paintTrack(Graphics g, JComponent c, Rectangle r) {
            g.setColor(new Color(255, 255, 255, 14));
            g.fillRect(r.x, r.y, r.width, r.height);
        }

        @Override
        protected void paintThumb(Graphics g, JComponent c, Rectangle r) {
            if (r.isEmpty() || !scrollbar.isEnabled()) return;
            g.setColor(accent());
            g.fillRect(r.x + 1, r.y, r.width - 2, r.height);
        }
    }

    // ================= Diamond-thumb slider =================

    private class DiamondSliderUI extends BasicSliderUI {
        DiamondSliderUI(JSlider s) { super(s); }

        @Override protected Dimension getThumbSize() { return new Dimension(16, 18); }
        @Override public void paintFocus(Graphics g) { }

        @Override
        public void paintTrack(Graphics g) {
            Graphics2D g2 = (Graphics2D) g.create();
            int cy = thumbRect.y + thumbRect.height / 2;
            g2.setColor(TRACK_BG);
            g2.fillRect(trackRect.x, cy - 1, trackRect.width, 3);
            int fillW = Math.min(trackRect.width, Math.max(0, thumbRect.x + thumbRect.width / 2 - trackRect.x));
            g2.setColor(accent());
            g2.fillRect(trackRect.x, cy - 1, Math.max(0, fillW), 3);
            g2.dispose();
        }

        @Override
        public void paintThumb(Graphics g) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            int cx = thumbRect.x + thumbRect.width / 2;
            int cy = thumbRect.y + thumbRect.height / 2;
            int r = 7;
            Polygon d = new Polygon(new int[]{cx, cx + r, cx, cx - r},
                                    new int[]{cy - r, cy, cy + r, cy}, 4);
            g2.setColor(accent());
            g2.fillPolygon(d);
            g2.setColor(ICE);
            g2.drawPolygon(d);
            g2.dispose();
        }
    }
}