package view;

import model.Song;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.io.File;
import java.util.*;
import java.util.List;
import java.util.function.Function;

public class CategoryPanel extends JPanel {

    // ---------- Callback for "user clicked a song" ----------
    public interface SongSelectedListener {
        void songSelected(Song song);
    }

    private SongSelectedListener songSelectedListener;

    public void setSongSelectedListener(SongSelectedListener listener) {
        this.songSelectedListener = listener;
    }

    // P3 FES palette
    private final Color BG_TOP      = new Color(1, 6, 20);
    private final Color BG_BOTTOM   = new Color(4, 26, 64);
    private final Color PANEL_FILL  = new Color(5, 18, 46);
    private final Color NAVY        = new Color(2, 10, 30);
    private final Color TEXT_LIGHT  = new Color(230, 250, 255);
    private final Color TEXT_MUTED  = new Color(120, 170, 200);
    private final Color ACCENT      = new Color(0, 170, 230);
    private final Color BORDER      = new Color(0, 110, 170);

    private final Font FONT_TITLE = new Font("Segoe UI", Font.BOLD, 14);
    private final Font FONT_SUB   = new Font("Segoe UI", Font.PLAIN, 12);

    public CategoryPanel(List<Song> songs) {
        setLayout(new BorderLayout(12, 12));
        setBorder(new EmptyBorder(18, 18, 18, 18));

        JTabbedPane tabs = new JTabbedPane();
        tabs.setFont(new Font("Segoe UI", Font.BOLD, 13));
        tabs.setBackground(NAVY);
        tabs.setForeground(TEXT_LIGHT);
        tabs.setOpaque(true);

        tabs.addTab("// GENRE",    buildTab(songs, s -> safe(s.getGenre())));
        tabs.addTab("// COMPOSER", buildTab(songs, s -> safe(s.getComposer())));
        tabs.addTab("// YEAR",     buildTab(songs, s -> String.valueOf(s.getReleaseYear())));

        add(tabs, BorderLayout.CENTER);
    }

    private String safe(String s) {
        return (s == null || s.isEmpty()) ? "Unknown" : s;
    }

    private JPanel buildTab(List<Song> songs, Function<Song, String> classifier) {
        JPanel panel = new JPanel(new BorderLayout(12, 12));
        panel.setOpaque(false);

        // Group songs
        Map<String, List<Song>> grouped = new TreeMap<>();
        for (Song s : songs) {
            grouped.computeIfAbsent(classifier.apply(s), k -> new ArrayList<>()).add(s);
        }

        // ---------- WEST: Category list ----------
        DefaultListModel<String> catModel = new DefaultListModel<>();
        for (String key : grouped.keySet()) catModel.addElement(key);
        JList<String> catList = new JList<>(catModel);
        catList.setBackground(PANEL_FILL);
        catList.setForeground(TEXT_LIGHT);
        catList.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        catList.setSelectionBackground(ACCENT);
        catList.setSelectionForeground(NAVY);
        catList.setFixedCellHeight(34);
        catList.setBorder(new EmptyBorder(8, 8, 8, 8));

        JScrollPane catScroll = new JScrollPane(catList);
        catScroll.setPreferredSize(new Dimension(220, 0));
        catScroll.setBorder(BorderFactory.createTitledBorder(
            BorderFactory.createLineBorder(BORDER, 1),
            "// CATEGORIES", 0, 0,
            new Font("Segoe UI", Font.BOLD, 13), ACCENT));

        // ---------- EAST: Songs with thumbnails ----------
        DefaultListModel<Song> songModel = new DefaultListModel<>();
        JList<Song> songList = new JList<>(songModel);
        songList.setBackground(PANEL_FILL);
        songList.setForeground(TEXT_LIGHT);
        songList.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        songList.setSelectionBackground(ACCENT);
        songList.setSelectionForeground(NAVY);
        songList.setFixedCellHeight(72);
        songList.setCellRenderer(new SongThumbnailRenderer());

        // ---- Click-to-play callback ----
        songList.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override
            public void mouseClicked(java.awt.event.MouseEvent e) {
                Song clicked = songList.getSelectedValue();
                if (clicked != null && songSelectedListener != null) {
                    songSelectedListener.songSelected(clicked);
                }
            }
        });

        JScrollPane songScroll = new JScrollPane(songList);
        songScroll.setBorder(BorderFactory.createTitledBorder(
            BorderFactory.createLineBorder(BORDER, 1),
            "// TRACKS", 0, 0,
            new Font("Segoe UI", Font.BOLD, 13), ACCENT));

        // Wire category selection -> song list
        catList.addListSelectionListener(e -> {
            if (e.getValueIsAdjusting()) return;
            String selected = catList.getSelectedValue();
            songModel.clear();
            if (selected != null && grouped.containsKey(selected)) {
                for (Song s : grouped.get(selected)) {
                    songModel.addElement(s);
                }
            }
        });

        panel.add(catScroll, BorderLayout.WEST);
        panel.add(songScroll, BorderLayout.CENTER);
        return panel;
    }

    // ==========================================================
    // Custom renderer: shows a thumbnail, title, artist, year
    // ==========================================================
    private class SongThumbnailRenderer extends JPanel implements ListCellRenderer<Song> {
        private Song song;
        private boolean selected;

        SongThumbnailRenderer() {
            setOpaque(true);
            setLayout(new BorderLayout(12, 0));
            setBorder(new EmptyBorder(8, 10, 8, 10));
        }

        @Override
        public Component getListCellRendererComponent(JList<? extends Song> list,
                Song value, int index, boolean isSelected, boolean cellHasFocus) {
            this.song = value;
            this.selected = isSelected;

            if (isSelected) {
                setBackground(ACCENT);
            } else if (index % 2 == 1) {
                setBackground(new Color(40, 110, 200, 28));
            } else {
                setBackground(PANEL_FILL);
            }

            removeAll();

            // ---- Thumbnail ----
            JLabel thumb = new JLabel();
            thumb.setPreferredSize(new Dimension(56, 56));
            thumb.setOpaque(false);
            thumb.setBorder(BorderFactory.createLineBorder(BORDER, 1));

            if (song != null && song.getImagePath() != null
                    && new File(song.getImagePath()).exists()) {
                ImageIcon icon = new ImageIcon(song.getImagePath());
                Image scaled = icon.getImage().getScaledInstance(56, 56, Image.SCALE_SMOOTH);
                thumb.setIcon(new ImageIcon(scaled));
            } else {
                thumb.setText("?");
                thumb.setHorizontalAlignment(SwingConstants.CENTER);
                thumb.setForeground(TEXT_MUTED);
                thumb.setFont(FONT_TITLE);
            }

            // ---- Text column ----
            JPanel textCol = new JPanel(new BorderLayout());
            textCol.setOpaque(false);

            String titleText  = (song == null) ? "" : song.getTitle();
            String artistText = (song == null) ? "" : song.getArtist();
            String yearText   = (song == null) ? "" : String.valueOf(song.getReleaseYear());

            JLabel titleLabel = new JLabel(titleText);
            titleLabel.setFont(FONT_TITLE);
            titleLabel.setForeground(isSelected ? NAVY : TEXT_LIGHT);

            String subHtml = String.format(
                "<html><span style='color:%s;'>%s</span>"
                + " <span style='color:%s;'>· %s</span></html>",
                isSelected ? "#0a1930" : "#9bbfe0",
                escapeHtml(artistText),
                isSelected ? "#0a1930" : "#6a8aa8",
                yearText
            );
            JLabel subLabel = new JLabel(subHtml);
            subLabel.setFont(FONT_SUB);

            textCol.add(titleLabel, BorderLayout.NORTH);
            textCol.add(subLabel,   BorderLayout.SOUTH);

            add(thumb,   BorderLayout.WEST);
            add(textCol, BorderLayout.CENTER);

            return this;
        }
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2 = (Graphics2D) g.create();
        int w = getWidth(), h = getHeight();
        g2.setPaint(new GradientPaint(0, 0, BG_TOP, 0, h, BG_BOTTOM));
        g2.fillRect(0, 0, w, h);
        g2.dispose();
    }

    private String escapeHtml(String s) {
        if (s == null) return "";
        return s.replace("&", "&amp;")
                .replace("<", "&lt;")
                .replace(">", "&gt;");
    }
}