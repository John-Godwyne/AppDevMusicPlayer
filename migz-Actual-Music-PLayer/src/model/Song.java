package model;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Transient;

@Entity
public class Song {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    private String title;
    private String artist;
    private String filePath;
    private String imagePath;
    
    @Transient // We don't want a massive string in the DB
    private String lyrics;

    public Song() {} // JPA requires an empty constructor

    public Song(String title, String artist, String filePath, String imagePath, String lyrics) {
        this.title = title;
        this.artist = artist;
        this.filePath = filePath;
        this.imagePath = imagePath;
        this.lyrics = lyrics;
    }

    // Getters and Setters (Generate these quickly in VS Code: Right-click -> Source Action -> Generate Getters and Setters)
    public Long getId() { return id; }
    public String getTitle() { return title; }
    public String getArtist() { return artist; }
    public String getFilePath() { return filePath; }
    public String getImagePath() { return imagePath; }
    public String getLyrics() { return lyrics; }

    @Override
    public String toString() { return title + " - " + artist; }
}