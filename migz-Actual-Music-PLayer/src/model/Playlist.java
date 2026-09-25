package model;

import java.util.ArrayList;
import java.util.List;

public class Playlist {
    private List<Song> songs;

    public Playlist() {
        this.songs = new ArrayList<>();
    }

    public void addSong(Song song) { this.songs.add(song); }
    public List<Song> getSongs() { return songs; }
}