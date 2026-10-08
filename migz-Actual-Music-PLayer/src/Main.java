import controller.PlayerController;
import events.SongChangeEvent;
import events.SongChangeListener;
import model.Song;
import persistence.DatabaseManager;
import view.MainFrame;
import java.io.IOException;
import java.nio.file.*;
import java.util.*;
import javax.swing.*;
import java.awt.*;
import java.io.File;

public class Main {
    public static void main(String[] args) {
        
        // 1. Set a modern Dark Look and Feel (Nimbus + Dark Overrides)
        try {
            UIManager.setLookAndFeel("javax.swing.plaf.nimbus.NimbusLookAndFeel");
            
            UIManager.put("control", new Color(45, 45, 45));
            UIManager.put("info", new Color(45, 45, 45));
            UIManager.put("nimbusBase", new Color(30, 30, 30));
            UIManager.put("nimbusLightBackground", new Color(30, 30, 30));
            UIManager.put("text", new Color(230, 230, 230));
            UIManager.put("nimbusSelectedText", Color.WHITE);
            UIManager.put("nimbusSelectionBackground", new Color(0, 120, 215));
        } catch (Exception e) {
            System.out.println("Could not set Look and Feel: " + e.getMessage());
        }

        // 2. Initialize JPA Database
        DatabaseManager.init();

        // 3. FORCE REFRESH: Clear old data and seed the new songs every time
        DatabaseManager.clearAllSongs(); 
        seedDatabase();

        // 4. Start GUI
        SwingUtilities.invokeLater(() -> {
            MainFrame view = new MainFrame();
            PlayerController controller = new PlayerController(view);

            // 5. Add Custom Event Listener
            controller.addSongChangeListener(new SongChangeListener() {
                @Override
                public void songChanged(SongChangeEvent event) {
                    Song song = event.getSong();

                    // Update Now Playing label
                    view.playerPanel.nowPlayingLabel.setText("♪ " + song.getTitle() + " — " + song.getArtist());

                    // Update Image5
                    if (song.getImagePath() != null && new File(song.getImagePath()).exists()) {
                        ImageIcon icon = new ImageIcon(song.getImagePath());
                        view.playerPanel.imagePanel.setImage(icon.getImage());
                    } else {
                        view.playerPanel.imagePanel.setPlaceholder("Image Not Found");
                    }

                    // Update Lyrics
                    view.playerPanel.lyricsArea.setText(song.getLyrics());
                    view.playerPanel.lyricsArea.setCaretPosition(0); // Scroll to top
                }
            });

            view.setVisible(true);
        });
    }

    private static String findAsset(String dir, String base, String ext) {
    if (new File(dir + base + ext).exists()) return dir + base + ext;
    for (String part : base.split(" - ")) {
        String p = dir + part.trim() + ext;
        if (new File(p).exists()) return p;
    }
    return dir + base + ext; }

   private static void seedDatabase() {
    Map<String, String[]> info = new HashMap<>();
    try {
        for (String line : Files.readAllLines(Path.of("resources/artists.txt"))) {
            String[] p = line.split("\\|", 3);
            if (p.length >= 2) info.put(p[0].trim(), p);
        }
    } catch (IOException e) {
        System.out.println("No artists.txt found, using defaults.");
    }

    File[] wavs = new File("resources/audio").listFiles((d, n) -> n.endsWith(".wav"));
    if (wavs == null) return;
    Arrays.sort(wavs);

    int count = 0;
    for (File wav : wavs) {
        String title = wav.getName().replace(".wav", "");
        String[] p = info.get(title);
        String lyricsPath = "resources/lyrics/" + title + ".txt";

        Song song = new Song(
            title,
            p != null ? p[1].trim() : "Unknown Artist",
            "resources/audio/" + title + ".wav",
            findAsset("resources/images/", title, ".jpg"),
            new File(lyricsPath).exists()
                ? persistence.LyricsLoader.loadLyrics(lyricsPath)
                : "No lyrics available."
        );
        if (p != null && p.length == 3) song.setGenre(p[2].trim());

        DatabaseManager.saveSong(song);
        count++;
    }
    System.out.println("Database seeded with " + count + " songs.");
}}