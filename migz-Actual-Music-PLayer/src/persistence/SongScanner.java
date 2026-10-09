package persistence;

import model.Song;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class SongScanner {

    /**
     * Explicit mapping: { audio, image, lyrics, year, genre, composer }
     */
    private static final String[][] SONG_MAP = {
        // ---------- Audio ---------------------------------------- Image ---------------------------------- Lyrics ----------------------------------------------------- Year   Genre       Composer ------------------------
        { "She Looks So Perfect.wav",    "She Looks So Perfect.jpg",              "She Looks So Perfect.txt",     "2014", "Pop Rock",   "5 Seconds of Summer" },
        { "A Man Without Love.wav",                            "A Man Without Love.jpg",                "A Man Without Love.txt",                             "1968", "Pop",        "Engelbert Humperdinck" },
        { "Ain't In LA.wav",                                   "Ain't In LA.jpg",                       "Ain't In LA.txt",                                    "2024", "Pop",        "Adéla" },
        { "Everyday.wav",                      "Everyday.jpg",                          "Everyday.txt",                       "2016", "Pop",        "Ariana Grande" },
        { "hate that i made you love me.wav",  "hate that i made you love me.jpg",      "hate that i made you love me.txt",   "2024", "Pop",        "Ariana Grande" },
        { "twilight zone.wav",                 "twilight zone.jpg",                     "twilight zone.txt",                  "2025", "Pop",        "Ariana Grande" },
        { "Asado.wav",                                         "Asado.jpg",                             "Asado.txt",                                          "2024", "OPM",        "The Red Strings" },
        { "Full Moon Full Life.wav",                           "Full Moon Full Life.jpg",               "Full Moon Full Life.txt",                            "2024", "J-Rock",     "Azumi Takahashi" },
        { "Wake Me Up When September Ends.wav",    "Wake Me Up When September Ends.jpg",    "Wake Me Up When September Ends.txt",     "2004", "Rock",       "Green Day" },
        { "Halsey - Colors.wav",                               "Colors.jpg",                            "Halsey - Colors.txt",                                "2015", "Indie Pop",  "Halsey" },
        { "Realidad.wav",                          "Realidad.jpg",                          "Realidad.txt",                           "2025", "OPM",        "HEY JUNE!" },
        { "Human ft. SF-A2 Miki.wav",                          "Human ft. SF-A2 Miki.jpg",              "Human ft. SF-A2 Miki.txt",                           "2022", "Vocaloid",   "PinocchioP" },
        { "Isang Pag-Ibig.wav",                                "Isang Pag-Ibig.jpg",                    "Isang Pag-Ibig.txt",                                 "2018", "OPM",        "IV of Spades" },
        { "It's Going Down Now.wav",         "It's Going Down Now.jpg",               "It's Going Down Now.txt",          "2024", "J-Rock",     "Lotus Juice" },
        { "Friendship Over.wav",               "Friendship Over.jpg",                   "Friendship Over.txt",                "2024", "OPM",        "Janine Berdin" },
        { "Elements.wav",          "Elements.jpg",                          "Elements.txt",           "2023", "Pop",        "John Michael Howell" },
        { "Kalendaryo - nicole.wav",                           "Kalendaryo.jpg",                        "Kalendaryo - nicole.txt",                            "2024", "OPM",        "nicole" },
        { "habangbuhay pansamantala.wav",               "Panaginip.jpg",                         "habangbuhay pansamantala.txt",                "2024", "OPM",        "Maki" },
        { "Na Para Bang.wav",                 "Na Para Bang.jpg",                      "Na Para Bang.txt",                  "2024", "OPM",        "Mariah Deborah" },
        { "Multo.wav",                                         "Multo.jpg",                             "Multo.txt",                                          "2024", "OPM",        "Cup of Joe" },
        { "Mundo.wav",                                         "Mundo.jpg",                             "Mundo.txt",                                          "2018", "OPM",        "IV of Spades" },
        { "Panaginip.wav",                            "Panaginip.jpg",                         "Panaginip.txt",                             "2024", "OPM",        "nicole" },
        { "Pompeii.wav",                                       "Pompeii.jpg",                           "Pompeii.txt",                                        "2013", "Indie Pop",  "Bastille" },
        { "Teleserye.wav",                            "Teleserye.jpg",                         "Teleserye.txt",                             "2024", "OPM",        "SHANNI" },
        { "the cure.wav",                                      "The Cure.jpg",                          "the cure.txt",                                       "2023", "Pop",        "Olivia Rodrigo" }
    };

    private static final String AUDIO_DIR  = "resources/audio/";
    private static final String IMAGE_DIR  = "resources/images/";
    private static final String LYRICS_DIR = "resources/lyrics/";

    public static List<Song> scanAll() {
        List<Song> songs = new ArrayList<>();

        File[] lyricFiles = new File(LYRICS_DIR).listFiles((d, n) -> n.toLowerCase().endsWith(".txt"));
        File[] imageFiles = new File(IMAGE_DIR).listFiles((d, n) ->
                n.toLowerCase().endsWith(".jpg") || n.toLowerCase().endsWith(".jpeg") || n.toLowerCase().endsWith(".png"));

        for (String[] entry : SONG_MAP) {
            String audioFile  = entry[0];
            String imageHint  = entry[1];
            String lyricsHint = entry[2];
            int    year       = safeInt(entry[3]);
            String genre      = entry[4];
            String composer   = entry[5];

            String audioPath = AUDIO_DIR + audioFile;
            if (!new File(audioPath).exists()) {
                System.out.println("⚠ Missing audio: " + audioPath);
                continue;
            }

                        String base = audioFile.substring(0, audioFile.lastIndexOf('.'));
            String title = base;
            String artist = null;

            int sep = base.indexOf(" - ");
            if (sep > 0) {
                // Format: "Artist - Title"
                artist = base.substring(0, sep).trim();
                title  = base.substring(sep + 3).trim();
            }

            // -------- Artist fallback chain --------
            // 1) If the file had "Artist - Title", keep the artist.
            // 2) Otherwise use the composer from the map.
            // 3) If composer is missing/Unknown too, use the title itself.
            
            if (artist == null || artist.isEmpty()) {
                if (composer != null && !composer.equalsIgnoreCase("Unknown")
                        && !composer.isEmpty()) {
                    artist = composer;
                } else {
                    artist = title;   // last resort — never show "Unknown Artist"
                }
            }

            File imageFile = resolveImage(imageHint, base, title, imageFiles);
            String imagePath = (imageFile != null) ? imageFile.getPath() : null;
            if (imagePath == null) {
                System.out.println("⚠ No image matched for: [" + base + "]");
            }

            File lyricsFile = resolveLyrics(lyricsHint, base, title, lyricFiles);
            String lyrics;
            if (lyricsFile != null) {
                lyrics = LyricsLoader.loadLyrics(lyricsFile.getPath());
            } else {
                lyrics = "Lyrics not available for this track.";
                System.out.println("⚠ No lyrics matched for: [" + base + "]");
            }

            songs.add(new Song(title, artist, audioPath, imagePath, lyrics, year, genre, composer));
        }

        return songs;
    }

    private static File resolveImage(String hint, String base, String title, File[] allImages) {
        if (hint != null && !hint.isEmpty()) {
            File f = new File(IMAGE_DIR + hint);
            if (f.exists()) return f;
        }
        return fuzzyMatch(allImages, base, title);
    }

    private static File resolveLyrics(String hint, String base, String title, File[] allLyrics) {
        if (hint != null && !hint.isEmpty()) {
            File f = new File(LYRICS_DIR + hint);
            if (f.exists()) return f;
        }
        return fuzzyMatch(allLyrics, base, title);
    }

    private static File fuzzyMatch(File[] candidates, String base, String title) {
        if (candidates == null) return null;
        String baseL  = base.toLowerCase(Locale.ROOT);
        String titleL = title.toLowerCase(Locale.ROOT);

        File best = null;
        int bestLen = 0;
        for (File f : candidates) {
            String name = stripExt(f.getName()).toLowerCase(Locale.ROOT);
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

    private static int safeInt(String s) {
        try { return Integer.parseInt(s.trim()); } catch (Exception e) { return 0; }
    }
}