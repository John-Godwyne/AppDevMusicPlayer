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
    
    // We use @Transient here so JPA doesn't try to save a massive string to the DB, 
    // though you can remove it if you want lyrics saved in the DB.
    @Transient 
    private String lyrics;

    // Empty constructor required by JPA
    public Song() {}

    public Song(String title, String artist, String filePath, String imagePath) {
        this.title = title;
        this.artist = artist;
        this.filePath = filePath;
        this.imagePath = imagePath;
    }

    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }

    public String getArtist() { return artist; }
    public void setArtist(String artist) { this.artist = artist; }

    public String getFilePath() { return filePath; }
    public void setFilePath(String filePath) { this.filePath = filePath; }

    public String getImagePath() { return imagePath; }
    public void setImagePath(String imagePath) { this.imagePath = imagePath; }

    public String getLyrics() { return lyrics; }
    public void setLyrics(String lyrics) { this.lyrics = lyrics; }

    @Override
    public String toString() {
        return title + " - " + artist;
    }
}