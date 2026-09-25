package persistence;

import model.Song;
import java.io.*;
import java.util.ArrayList;
import java.util.List;

public class FileManager {
    private static final String FILE_NAME = "songs_data.txt";

    // Save songs to a text file
    public static void saveSongs(List<Song> songs) {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(FILE_NAME))) {
            for (Song s : songs) {
                writer.write(s.getTitle() + "|" + s.getArtist() + "|" + s.getFilePath() + "|" + s.getImagePath() + "|" + s.getLyrics());
                writer.newLine();
            }
        } catch (IOException e) {
            System.out.println("Error saving data: " + e.getMessage());
        }
    }

    // Load songs from a text file
    public static List<Song> loadSongs() {
        List<Song> songs = new ArrayList<>();
        File file = new File(FILE_NAME);
        if (!file.exists()) return songs;

        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            String line;
            while ((line = reader.readLine()) != null) {
                String[] parts = line.split("\\|");
                if (parts.length == 5) {
                    songs.add(new Song(parts[0], parts[1], parts[2], parts[3], parts[4]));
                }
            }
        } catch (IOException e) {
            System.out.println("Error loading data: " + e.getMessage());
        }
        return songs;
    }
}