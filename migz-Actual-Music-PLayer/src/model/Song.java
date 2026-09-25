package model;

public class Song {
    private String title;
    private String artist;
    private String filePath;
    private String imagePath;
    private String lyrics;

    public Song(String title, String artist, String filePath, String imagePath, String lyrics) {
        this.title = title;
        this.artist = artist;
        this.filePath = filePath;
        this.imagePath = imagePath;
        this.lyrics = lyrics;
    }

    // Getters
    public String getTitle() { return title; }
    public String getArtist() { return artist; }
    public String getFilePath() { return filePath; }
    public String getImagePath() { return imagePath; }
    public String getLyrics() { return lyrics; }

    @Override
    public String toString() {
        return title + " - " + artist;
    }
}