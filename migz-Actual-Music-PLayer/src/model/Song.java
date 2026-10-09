package model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;

@Entity
public class Song {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String title;
    private String artist;
    private String filePath;
    private String imagePath;

    private Integer releaseYear;
    private String genre;
    private String composer;

    @Column(columnDefinition = "TEXT")
    private String lyrics;

    public Song() {}

    public Song(String title, String artist, String filePath, String imagePath, String lyrics) {
        this(title, artist, filePath, imagePath, lyrics, 0, "Unknown", "Unknown");
    }

    public Song(String title, String artist, String filePath, String imagePath, String lyrics,
                int releaseYear, String genre, String composer) {
        this.title = title;
        this.artist = artist;
        this.filePath = filePath;
        this.imagePath = imagePath;
        this.lyrics = lyrics;
        this.releaseYear = releaseYear;
        this.genre = genre;
        this.composer = composer;
    }

    public Long getId() { return id; }
    public String getTitle() { return title; }
    public String getArtist() { return artist; }
    public String getFilePath() { return filePath; }
    public String getImagePath() { return imagePath; }
    public String getLyrics() { return lyrics; }
    public Integer getReleaseYear() { return releaseYear; }
    public String getGenre() { return genre; }
    public String getComposer() { return composer; }

    public void setLyrics(String lyrics) { this.lyrics = lyrics; }

        @Override
    public String toString() {
        // Never show "Unknown Artist" — fall back to composer, then title
        String displayArtist = (artist == null || artist.isEmpty()
                || artist.equalsIgnoreCase("Unknown Artist"))
                ? (composer != null && !composer.isEmpty()
                    && !composer.equalsIgnoreCase("Unknown")
                        ? composer
                        : title)
                : artist;

        return title + "  —  " + displayArtist;
    }
}