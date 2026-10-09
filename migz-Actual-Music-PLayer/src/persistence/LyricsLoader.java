package persistence;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

public class LyricsLoader {
    public static String loadLyrics(String filePath) {
        try {
            String content = new String(Files.readAllBytes(Paths.get(filePath)));
            if (content.trim().isEmpty()) {
                return "This song has no lyrics file content yet.";
            }
            return content;
        } catch (IOException e) {
            return "Lyrics not available.\n\n(Could not load: " + filePath + ")";
        }
    }
}