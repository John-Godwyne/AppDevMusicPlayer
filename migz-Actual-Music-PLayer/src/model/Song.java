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
    private String genre = "Unknown";
    private String imagePath;
    
    // CHANGED: Use TEXT column to allow long lyrics (up to 65,000 chars)
    @Column(columnDefinition = "TEXT")
    private String lyrics;

    public Song() {} 

    public Song(String title, String artist, String filePath, String imagePath, String lyrics) {
        this.title = title;
        this.artist = artist;
        this.filePath = filePath;
        this.imagePath = imagePath;
        this.lyrics = lyrics;
    }

    public Long getId() { return id; }
    public String getTitle() { return title; }
    public String getArtist() { return artist; }
    public String getFilePath() { return filePath; }
    public String getImagePath() { return imagePath; }
    public String getLyrics() { return lyrics; }
    public String getGenre() { return genre; }
    public void setGenre(String genre) { this.genre = genre; }
    
    public void setLyrics(String lyrics) { this.lyrics = lyrics; }

    @Override
    public String toString() { return title + " - " + artist; }
}