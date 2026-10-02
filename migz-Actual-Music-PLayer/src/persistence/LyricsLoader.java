package persistence;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

public class LyricsLoader {
    public static String loadLyrics(String filePath) {
        try {
            return new String(Files.readAllBytes(Paths.get(filePath)));
        } catch (IOException e) {
            return "Lyrics not available.\n\n(Could not load: " + filePath + ")";
        }
    }
}