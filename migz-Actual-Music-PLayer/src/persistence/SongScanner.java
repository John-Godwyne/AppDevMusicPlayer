package persistence;

import model.Song;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class SongScanner {

    /**
     * Explicit mapping: { audio filename, image filename, lyrics filename }
     * Use `null` for image/lyrics if you want the scanner to auto-search.
     */
    private static final String[][] SONG_MAP = {
        { "5 Seconds of Summer - She Looks So Perfect.wav",    "She Looks So Perfect.jpg",              "5 Seconds of Summer - She Looks So Perfect.txt" },
        { "A Man Without Love.wav",                            "A Man Without Love.jpg",                "A Man Without Love.txt" },
        { "Ain't In LA.wav",                                   "Ain't In LA.jpg",                       "Ain't In LA.txt" },
        { "Ariana Grande - Everyday.wav",                      "Everyday.jpg",                          "Ariana Grande - Everyday.txt" },
        { "Ariana Grande - hate that i made you love me.wav",  "hate that i made you love me.jpg",      "Ariana Grande - hate that i made you love me.txt" },
        { "Ariana Grande - twilight zone.wav",                 "twilight zone.jpg",                     "Ariana Grande - twilight zone.txt" },
        { "Asado.wav",                                         "Asado.jpg",                             "Asado.txt" },
        { "Full Moon Full Life.wav",                           "Full Moon Full Life.jpg",               "Full Moon Full Life.txt" },
        { "Green Day - Wake Me Up When September Ends.wav",    "Wake Me Up When September Ends.jpg",    "Green Day - Wake Me Up When September Ends.txt" },
        { "Halsey - Colors.wav",                               "Colors.jpg",                            "Halsey - Colors.txt" },
        { "HEY JUNE! - Realidad.wav",                          "Realidad.jpg",                          "HEY JUNE! - Realidad.txt" },
        { "Human ft. SF-A2 Miki.wav",                          "Human ft. SF-A2 Miki.jpg",              "Human ft. SF-A2 Miki.txt" },
        { "Isang Pag-Ibig.wav",                                "Isang Pag-Ibig.jpg",                    "Isang Pag-Ibig.txt" },
        { "It's Going Down Now - Azumi Takahashi.wav",         "It's Going Down Now.jpg",               "It's Going Down Now - Azumi Takahashi.txt" },
        { "Janine Berdin - Friendship Over.wav",               "Friendship Over.jpg",                   "Janine Berdin - Friendship Over.txt" },
        { "John Michael Howell & ZVC - Elements.wav",          "Elements.jpg",                          "John Michael Howell & ZVC - Elements.txt" },
        { "Kalendaryo - nicole.wav",                           "Kalendaryo.jpg",                        "Kalendaryo - nicole.txt" },
        { "Maki - habangbuhay pansamantala.wav",               "Panaginip.jpg",                         "Maki - habangbuhay pansamantala.txt" },
        { "Mariah Deborah - Na Para Bang.wav",                 "Na Para Bang.jpg",                      "Mariah Deborah - Na Para Bang.txt" },
        { "Multo.wav",                                         "Multo.jpg",                             "Multo.txt" },
        { "Mundo.wav",                                         "Mundo.jpg",                             "Mundo.txt" },
        { "Panaginip - nicole.wav",                            "Panaginip.jpg",                         "Panaginip - nicole.txt" },
        { "Pompeii.wav",                                       "Pompeii.jpg",                           "Pompeii.txt" },
        { "SHANNI - Teleserye.wav",                            "Teleserye.jpg",                         "SHANNI - Teleserye.txt" },
        { "the cure.wav",                                      "The Cure.jpg",                          "the cure.txt" }
    };

    private static final String AUDIO_DIR  = "resources/audio/";
    private static final String IMAGE_DIR  = "resources/images/";
    private static final String LYRICS_DIR = "resources/lyrics/";

    public static List<Song> scanAll() {
        List<Song> songs = new ArrayList<>();

        // Cache folder listings once for the fuzzy-fallback lookups
        File[] lyricFiles = new File(LYRICS_DIR).listFiles((d, n) -> n.toLowerCase().endsWith(".txt"));
        File[] imageFiles = new File(IMAGE_DIR).listFiles((d, n) ->
                n.toLowerCase().endsWith(".jpg") || n.toLowerCase().endsWith(".jpeg") || n.toLowerCase().endsWith(".png"));

        for (String[] entry : SONG_MAP) {
            String audioFile  = entry[0];
            String imageHint  = entry[1];
            String lyricsHint = entry[2];

            String audioPath = AUDIO_DIR + audioFile;
            if (!new File(audioPath).exists()) {
                System.out.println("⚠ Missing audio: " + audioPath);
                continue;
            }

            // ---------- Derive title & artist from filename ----------
            String base = audioFile.substring(0, audioFile.lastIndexOf('.'));
            String title = base;
            String artist = "Unknown Artist";
            int sep = base.indexOf(" - ");
            if (sep > 0) {
                artist = base.substring(0, sep).trim();
                title  = base.substring(sep + 3).trim();
            }

            // ---------- Image (exact, then fuzzy) ----------
            File imageFile = resolveImage(imageHint, base, title, imageFiles);
            String imagePath = (imageFile != null) ? imageFile.getPath() : null;
            if (imagePath == null) {
                System.out.println("⚠ No image matched for: [" + base + "]");
            }

            // ---------- Lyrics (exact, then fuzzy) ----------
            File lyricsFile = resolveLyrics(lyricsHint, base, title, lyricFiles);
            String lyrics;
            if (lyricsFile != null) {
                lyrics = LyricsLoader.loadLyrics(lyricsFile.getPath());
            } else {
                lyrics = "Lyrics not available for this track.";
                System.out.println("⚠ No lyrics matched for: [" + base + "]");
            }

            songs.add(new Song(title, artist, audioPath, imagePath, lyrics));
        }

        return songs;
    }

    // --------------------------------------------------------
    // Image resolution: try hint, then folder scan
    // --------------------------------------------------------
    private static File resolveImage(String hint, String base, String title, File[] allImages) {
        // 1. Exact hint
        if (hint != null && !hint.isEmpty()) {
            File f = new File(IMAGE_DIR + hint);
            if (f.exists()) return f;
        }
        // 2. Fuzzy: any image whose name is contained in base or title, or vice-versa
        return fuzzyMatch(allImages, base, title);
    }

    // --------------------------------------------------------
    // Lyrics resolution: try hint, then folder scan
    // --------------------------------------------------------
    private static File resolveLyrics(String hint, String base, String title, File[] allLyrics) {
        // 1. Exact hint
        if (hint != null && !hint.isEmpty()) {
            File f = new File(LYRICS_DIR + hint);
            if (f.exists()) return f;
        }
        // 2. Fuzzy: any .txt whose base name is contained in base or title (or vice-versa)
        return fuzzyMatch(allLyrics, base, title);
    }

    // --------------------------------------------------------
    // Case-insensitive fuzzy match by substring
    // --------------------------------------------------------
    private static File fuzzyMatch(File[] candidates, String base, String title) {
        if (candidates == null) return null;
        String baseL  = base.toLowerCase(Locale.ROOT);
        String titleL = title.toLowerCase(Locale.ROOT);

        // Pass 1 — try longest common substring (matches "Wake Me Up When September Ends" inside the full name)
        File best = null;
        int bestLen = 0;
        for (File f : candidates) {
            String name = stripExt(f.getName()).toLowerCase(Locale.ROOT);

            // Direct containment either way
            if (name.equals(baseL) || name.equals(titleL)) return f;
            if (baseL.contains(name) && name.length() > bestLen) {
                best = f; bestLen = name.length();
            }
            if (name.contains(titleL) && titleL.length() > bestLen) {
                best = f; bestLen = titleL.length();
            }
        }
        return best;
    }

    private static String stripExt(String name) {
        int dot = name.lastIndexOf('.');
        return (dot > 0) ? name.substring(0, dot) : name;
    }
}