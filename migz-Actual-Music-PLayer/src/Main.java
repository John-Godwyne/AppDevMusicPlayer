import controller.PlayerController;
import events.SongChangeEvent;
import events.SongChangeListener;
import model.Song;
import persistence.DatabaseManager;
import view.MainFrame;

import javax.swing.*;
import java.awt.*;
import java.io.File;

public class Main {
    public static void main(String[] args) {

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

        DatabaseManager.init();
        DatabaseManager.clearAllSongs();
        seedDatabase();

        SwingUtilities.invokeLater(() -> {
            MainFrame view = new MainFrame();
            PlayerController controller = new PlayerController(view);

            // ---- Custom event listener for updating the playe    r UI ----
            controller.addSongChangeListener(new SongChangeListener() {
                @Override
                public void songChanged(SongChangeEvent event) {
                    Song song = event.getSong();

                    view.playerPanel.nowPlayingLabel.setText("♪ " + song.getTitle() + " — " + song.getArtist());

                    if (song.getImagePath() != null && new File(song.getImagePath()).exists()) {
                        ImageIcon icon = new ImageIcon(song.getImagePath());
                        view.playerPanel.imagePanel.setImage(icon.getImage());
                    } else {
                        view.playerPanel.imagePanel.setPlaceholder("Image Not Found");
                    }

                    view.playerPanel.lyricsArea.setText(song.getLyrics());
                    view.playerPanel.lyricsArea.setCaretPosition(0);
                }
            });

            // ---- NEW: Category click -> play song + switch to Player tab ----
            view.categoryPanel.setSongSelectedListener(song -> {
                controller.playSongByObject(song);
                view.switchToPlayerTab();
            });

            view.setVisible(true);
        });
    }

    private static void seedDatabase() {
        System.out.println("Scanning folders for songs...");
        java.util.List<Song> scanned = persistence.SongScanner.scanAll();
        for (Song s : scanned) {
            DatabaseManager.saveSong(s);
        }
        System.out.println("Database seeded with " + scanned.size() + " songs.");
    }
}