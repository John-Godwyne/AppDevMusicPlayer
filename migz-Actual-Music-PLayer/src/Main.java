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

    private static void seedDatabase() {
        DatabaseManager.saveSong(new Song(
            "A Man Without Love", "Engelbert Humperdinck",
            "resources/audio/A Man Without Love.wav",
            "resources/images/A Man Without Love.jpg",
            persistence.LyricsLoader.loadLyrics("resources/lyrics/A Man Without Love.txt")
        ));
        
        DatabaseManager.saveSong(new Song(
            "Human ft. SF-A2 Miki", "PinocchioP",
            "resources/audio/Human ft. SF-A2 Miki.wav",
            "resources/images/Human ft. SF-A2 Miki.jpg",
            persistence.LyricsLoader.loadLyrics("resources/lyrics/Human ft. SF-A2 Miki.txt")
        ));
        
        DatabaseManager.saveSong(new Song(
            "Isang Pag-Ibig", "APO Hiking Society",
            "resources/audio/Isang Pag-Ibig.wav",
            "resources/images/Isang Pag-Ibig.jpg",
            persistence.LyricsLoader.loadLyrics("resources/lyrics/Isang Pag-Ibig.txt")
        ));
        
        DatabaseManager.saveSong(new Song(
            "Multo", "Cup of Joe",
            "resources/audio/Multo.wav",
            "resources/images/Multo.jpg",
            persistence.LyricsLoader.loadLyrics("resources/lyrics/Multo.txt")
        ));
        
        DatabaseManager.saveSong(new Song(
            "Pompeii", "Bastille",
            "resources/audio/Pompeii.wav",
            "resources/images/Pompeii.jpg",
            persistence.LyricsLoader.loadLyrics("resources/lyrics/Pompeii.txt")
        ));
        
        System.out.println("Database seeded with 5 songs.");
    }
}