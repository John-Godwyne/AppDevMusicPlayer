package controller;

import events.SongChangeEvent;
import events.SongChangeListener;
import model.Playlist;
import model.Song;
import persistence.DatabaseManager;
import view.MainFrame;
import view.PlayerPanel;

import javax.swing.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.List;

public class PlayerController {
    private MainFrame view;
    private PlayerPanel panel;
    private Playlist playlist;
    private AudioEngine audioEngine;
    private List<SongChangeListener> listeners = new ArrayList<>();

    private int currentIndex = -1;
    private Timer progressTimer;
    private boolean userIsDraggingSlider = false;
    private boolean hasAutoAdvanced = false;

    public PlayerController(MainFrame view) {
        this.view = view;
        this.panel = view.playerPanel;
        this.playlist = new Playlist();
        this.audioEngine = new AudioEngine();

        List<Song> dbSongs = DatabaseManager.getAllSongs();
        for (Song s : dbSongs) {
            playlist.addSong(s);
            panel.listModel.addElement(s.toString());
        }

        initController();

        // Volume slider
        panel.volumeSlider.addChangeListener(e -> {
            int value = panel.volumeSlider.getValue();
            float volume = value / 100f;
            audioEngine.setVolume(volume);
            panel.volumeLabel.setText(value + "%");
        });

        // NEW: Sort dropdown
        panel.sortComboBox.addActionListener(e -> applySort());
        applySort();   // <-- initial sort on startup

        initProgressTimer();
    }

    public void addSongChangeListener(SongChangeListener listener) {
        listeners.add(listener);
    }

    private void fireSongChangeEvent(Song song, SongChangeEvent.Type type) {
        SongChangeEvent event = new SongChangeEvent(this, song, type);
        for (SongChangeListener listener : listeners) {
            listener.songChanged(event);
        }
    }

    

    private void loadAndPlay(int index) {
        if (index < 0 || index >= playlist.getSongs().size()) return;
        currentIndex = index;
        hasAutoAdvanced = false;
        panel.songList.setSelectedIndex(index);
        Song song = playlist.getSongs().get(index);

        String lyricsPath = "resources/lyrics/" + song.getTitle() + ".txt";
        song.setLyrics(persistence.LyricsLoader.loadLyrics(lyricsPath));

        audioEngine.play(song.getFilePath());
        panel.setPlaying(true);
        panel.playButton.setText("[||] Pause");
        fireSongChangeEvent(song, SongChangeEvent.Type.TRACK_CHANGED);
        fireSongChangeEvent(song, SongChangeEvent.Type.PLAYING);
    }

        /**
     * Called from the Category panel when a song is clicked.
     * Finds the song in the current playlist and plays it by index.
     */
    public void playSongByObject(Song song) {
        if (song == null) return;

        List<Song> songs = playlist.getSongs();
        for (int i = 0; i < songs.size(); i++) {
            if (songs.get(i).getId() != null
                    && songs.get(i).getId().equals(song.getId())) {
                loadAndPlay(i);
                return;
            }
        }

        // Fallback if IDs are not available: match by title + artist
        for (int i = 0; i < songs.size(); i++) {
            Song s = songs.get(i);
            if (s.getTitle().equals(song.getTitle())
                    && s.getArtist().equals(song.getArtist())) {
                loadAndPlay(i);
                return;
            }
        }
    }

    private void initController() {
        panel.songList.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                int index = panel.songList.getSelectedIndex();
                if (index != -1) loadAndPlay(index);
            }
        });

        panel.playButton.addActionListener(e -> {
            if (currentIndex == -1) {
                int index = panel.songList.getSelectedIndex();
                if (index != -1) {
                    loadAndPlay(index);
                } else {
                    JOptionPane.showMessageDialog(view, "Select a song first!");
                }
                return;
            }

            Song current = playlist.getSongs().get(currentIndex);

            if (audioEngine.isPaused()) {
                audioEngine.resume();
                panel.setPlaying(true);
                panel.playButton.setText("[||] Pause");
                fireSongChangeEvent(current, SongChangeEvent.Type.PLAYING);
            } else if (audioEngine.isPlaying()) {
                audioEngine.pause();
                panel.setPlaying(false);
                panel.playButton.setText("[>] Play");
                fireSongChangeEvent(current, SongChangeEvent.Type.PAUSED);
            } else {
                audioEngine.play(current.getFilePath());
                panel.setPlaying(true);
                panel.playButton.setText("[||] Pause");
                fireSongChangeEvent(current, SongChangeEvent.Type.PLAYING);
            }
        });

        panel.stopButton.addActionListener(e -> {
            audioEngine.stop();
            panel.playButton.setText("▶ Play");
            panel.setPlaying(false);
            if (currentIndex != -1) {
                fireSongChangeEvent(playlist.getSongs().get(currentIndex), SongChangeEvent.Type.STOPPED);
            }
        });

        panel.nextButton.addActionListener(e -> {
            if (playlist.getSongs().isEmpty()) return;
            int nextIndex = (currentIndex + 1) % playlist.getSongs().size();
            loadAndPlay(nextIndex);
        });

        panel.prevButton.addActionListener(e -> {
            if (playlist.getSongs().isEmpty()) return;
            int prevIndex = (currentIndex - 1 + playlist.getSongs().size()) % playlist.getSongs().size();
            loadAndPlay(prevIndex);
        });

        panel.progressSlider.addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) { userIsDraggingSlider = true; }

            @Override
            public void mouseReleased(MouseEvent e) {
                long duration = audioEngine.getDurationMicros();
                int percent = panel.progressSlider.getValue();
                long seekPos = (long) (duration * (percent / 100.0));
                audioEngine.seekMicros(seekPos);
                userIsDraggingSlider = false;
            }
        });
    }

    // ============ SORT LOGIC ============
    private void applySort() {
        String choice = (String) panel.sortComboBox.getSelectedItem();
        if (choice == null) return;

        // Remember which song was playing so we can find it again after sorting
        Long playingId = (currentIndex != -1 && currentIndex < playlist.getSongs().size())
                ? playlist.getSongs().get(currentIndex).getId() : null;

                switch (choice) {
            case "Title (A-Z)":
                playlist.getSongs().sort((a, b) -> a.getTitle().compareToIgnoreCase(b.getTitle()));
                break;
            case "Title (Z-A)":
                playlist.getSongs().sort((a, b) -> b.getTitle().compareToIgnoreCase(a.getTitle()));
                break;
            case "Artist (A-Z)":
                playlist.getSongs().sort((a, b) -> a.getArtist().compareToIgnoreCase(b.getArtist()));
                break;
            case "Artist (Z-A)":
                playlist.getSongs().sort((a, b) -> b.getArtist().compareToIgnoreCase(a.getArtist()));
                break;
            case "Year (Old-New)":
                playlist.getSongs().sort((a, b) -> Integer.compare(
                    a.getReleaseYear() == null ? 0 : a.getReleaseYear(),
                    b.getReleaseYear() == null ? 0 : b.getReleaseYear()));
                break;
            case "Year (New-Old)":
                playlist.getSongs().sort((a, b) -> Integer.compare(
                    b.getReleaseYear() == null ? 0 : b.getReleaseYear(),
                    a.getReleaseYear() == null ? 0 : a.getReleaseYear()));
                break;
        }

        // Rebuild the list UI
        panel.listModel.clear();
        for (Song s : playlist.getSongs()) panel.listModel.addElement(s.toString());

        // Re-find the currently playing song in the new ordering
        if (playingId != null) {
            for (int i = 0; i < playlist.getSongs().size(); i++) {
                if (playingId.equals(playlist.getSongs().get(i).getId())) {
                    currentIndex = i;
                    panel.songList.setSelectedIndex(i);
                    break;
                }
            }
        }
    }

    private void initProgressTimer() {
        progressTimer = new Timer(250, e -> {
            if (audioEngine.isPlaying() && !userIsDraggingSlider) {
                long duration = audioEngine.getDurationMicros();
                long position = audioEngine.getPositionMicros();
                if (duration > 0) {
                    int percent = (int) ((position * 100) / duration);
                    panel.progressSlider.setValue(percent);
                    panel.timeLabel.setText(formatTime(position) + " / " + formatTime(duration));
                }
            }
        });
        progressTimer.start();
    }

    private String formatTime(long micros) {
        long totalSec = micros / 1_000_000;
        long min = totalSec / 60;
        long sec = totalSec % 60;
        return String.format("%02d:%02d", min, sec);
    }
}