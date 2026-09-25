import controller.PlayerController;
import events.SongChangeEvent;
import events.SongChangeListener;
import model.Song;
import persistence.DatabaseManager;
import view.MainFrame;

import javax.swing.*;
import java.io.File;

public class Main {
    public static void main(String[] args) {
        
        // 1. Initialize JPA Database
        DatabaseManager.init();

        // 2. Seed DB with 5 songs if empty (MAKE SURE YOU HAVE THESE FILES)
        if (DatabaseManager.getAllSongs().isEmpty()) {
            seedDatabase();
        }

        // 3. Start GUI
        SwingUtilities.invokeLater(() -> {
            MainFrame view = new MainFrame();
            PlayerController controller = new PlayerController(view);

            // 4. Add Custom Event Listener (Updates UI when song changes)
            controller.addSongChangeListener(new SongChangeListener() {
                @Override
                public void songChanged(SongChangeEvent event) {
                    Song song = event.getSong();

                    // Update Image
                    if (song.getImagePath() != null && new File(song.getImagePath()).exists()) {
                        ImageIcon icon = new ImageIcon(song.getImagePath());
                        java.awt.Image img = icon.getImage().getScaledInstance(300, 300, java.awt.Image.SCALE_SMOOTH);
                        view.playerPanel.imageLabel.setIcon(new ImageIcon(img));
                        view.playerPanel.imageLabel.setText("");
                    } else {
                        view.playerPanel.imageLabel.setIcon(null);
                        view.playerPanel.imageLabel.setText("Image Not Found");
                    }

                    // Update Lyrics
                    view.playerPanel.lyricsArea.setText(song.getLyrics());
                }
            });

            view.setVisible(true);
        });
    }

    private static void seedDatabase() {
        // NOTE: You must put real .wav files and .jpg files in your src folder for these paths to work!
        DatabaseManager.saveSong(new Song("Song One", "Artist A", "src/audio/song1.wav", "src/images/cover1.jpg", "Lyrics for Song One..."));
        DatabaseManager.saveSong(new Song("Song Two", "Artist B", "src/audio/song2.wav", "src/images/cover2.jpg", "Lyrics for Song Two..."));
        DatabaseManager.saveSong(new Song("Song Three", "Artist C", "src/audio/song3.wav", "src/images/cover3.jpg", "Lyrics for Song Three..."));
        DatabaseManager.saveSong(new Song("Song Four", "Artist D", "src/audio/song4.wav", "src/images/cover4.jpg", "Lyrics for Song Four..."));
        DatabaseManager.saveSong(new Song("Song Five", "Artist E", "src/audio/song5.wav", "src/images/cover5.jpg", "Lyrics for Song Five..."));
        System.out.println("Database seeded with 5 songs.");
    }
}