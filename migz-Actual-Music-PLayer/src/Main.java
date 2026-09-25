import controller.PlayerController;
import events.SongChangeEvent;
import events.SongChangeListener;
import model.Playlist;
import model.Song;
import persistence.DatabaseManager;
import view.MainFrame;

import javax.swing.*;
import java.io.File;
import java.nio.file.Files;
import java.nio.file.Paths;

public class Main {
    public static void main(String[] args) {
        // 1. Initialize JPA Database
        DatabaseManager.init();

        // 2. Seed Database with dummy data if it's empty (For testing!)
        if (DatabaseManager.getAllSongs().isEmpty()) {
            seedDatabase();
        }

        // 3. Start the Swing GUI
        SwingUtilities.invokeLater(() -> {
            MainFrame view = new MainFrame();
            Playlist playlist = new Playlist("My Favorites");
            
            PlayerController controller = new PlayerController(view, playlist);

            // 4. Add Custom Event Listener to update Lyrics and Image
            controller.addSongChangeListener(new SongChangeListener() {
                @Override
                public void songChanged(SongChangeEvent event) {
                    Song song = event.getSong();
                    
                    // Update Image
                    if (song.getImagePath() != null && new File(song.getImagePath()).exists()) {
                        ImageIcon icon = new ImageIcon(song.getImagePath());
                        // Scale image to fit the label
                        java.awt.Image img = icon.getImage().getScaledInstance(300, 300, java.awt.Image.SCALE_SMOOTH);
                        view.getPlayerPanel().getImageLabel().setIcon(new ImageIcon(img));
                        view.getPlayerPanel().getImageLabel().setText("");
                    } else {
                        view.getPlayerPanel().getImageLabel().setIcon(null);
                        view.getPlayerPanel().getImageLabel().setText("No Image Found");
                    }

                    // Update Lyrics
                    view.getPlayerPanel().getLyricsArea().setText(song.getLyrics());
                }
            });

            view.setVisible(true);
        });
    }

    // Helper method to put 5 songs in the DB so you can test immediately
    private static void seedDatabase() {
        // NOTE: You need to create these folders and put dummy files in them!
        DatabaseManager.saveSong(new Song("Song One", "Artist A", "src/resources/audio/song1.mp3", "src/resources/images/cover1.jpg"));
        DatabaseManager.saveSong(new Song("Song Two", "Artist B", "src/resources/audio/song2.mp3", "src/resources/images/cover2.jpg"));
        DatabaseManager.saveSong(new Song("Song Three", "Artist C", "src/resources/audio/song3.mp3", "src/resources/images/cover3.jpg"));
        DatabaseManager.saveSong(new Song("Song Four", "Artist D", "src/resources/audio/song4.mp3", "src/resources/images/cover4.jpg"));
        DatabaseManager.saveSong(new Song("Song Five", "Artist E", "src/resources/audio/song5.mp3", "src/resources/images/cover5.jpg"));
        System.out.println("Database seeded with 5 songs.");
    }
}