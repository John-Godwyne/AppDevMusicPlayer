import controller.PlayerController;
import events.SongChangeEvent;
import events.SongChangeListener;
import model.Song;
import persistence.FileManager;
import view.MainFrame;

import javax.swing.*;
import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class Main {
    public static void main(String[] args) {
        
        // 1. Create dummy data if file doesn't exist
        seedDataIfEmpty();

        // 2. Start GUI
        SwingUtilities.invokeLater(() -> {
            MainFrame view = new MainFrame();
            PlayerController controller = new PlayerController(view);

            // 3. Add Custom Event Listener (Updates UI when song changes)
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

    private static void seedDataIfEmpty() {
        File file = new File("songs_data.txt");
        if (!file.exists()) {
            List<Song> dummySongs = new ArrayList<>();
            // NOTE: These paths must exist on your computer!
            dummySongs.add(new Song("Song 1", "Artist A", "src/audio/song1.wav", "src/images/cover1.jpg", "Lyrics for song 1..."));
            dummySongs.add(new Song("Song 2", "Artist B", "src/audio/song2.wav", "src/images/cover2.jpg", "Lyrics for song 2..."));
            dummySongs.add(new Song("Song 3", "Artist C", "src/audio/song3.wav", "src/images/cover3.jpg", "Lyrics for song 3..."));
            dummySongs.add(new Song("Song 4", "Artist D", "src/audio/song4.wav", "src/images/cover4.jpg", "Lyrics for song 4..."));
            dummySongs.add(new Song("Song 5", "Artist E", "src/audio/song5.wav", "src/images/cover5.jpg", "Lyrics for song 5..."));
            FileManager.saveSongs(dummySongs);
        }
    }
}