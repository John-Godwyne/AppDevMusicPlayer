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
        // Volume slider: update audio volume + display % when dragged
        panel.volumeSlider.addChangeListener(e -> {
            int value = panel.volumeSlider.getValue();
            float volume = value / 100f;
            audioEngine.setVolume(volume);
            panel.volumeLabel.setText(value + "%");
        });
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
        panel.songList.setSelectedIndex(index);
        Song song = playlist.getSongs().get(index);
        
        // Load lyrics from file dynamically
        String lyricsPath = "resources/lyrics/" + song.getTitle() + ".txt";
        song.setLyrics(persistence.LyricsLoader.loadLyrics(lyricsPath));
        
        audioEngine.play(song.getFilePath());
        panel.playButton.setText("⏸ Pause"); // Reflect current playing state
        fireSongChangeEvent(song, SongChangeEvent.Type.TRACK_CHANGED);
        fireSongChangeEvent(song, SongChangeEvent.Type.PLAYING);
    }

    private void initController() {
        // Click a song in the list -> load & play it
        panel.songList.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                int index = panel.songList.getSelectedIndex();
                if (index != -1) {
                    loadAndPlay(index);
                }
            }
        });

        // ============ COMBINED PLAY / PAUSE TOGGLE ============
        panel.playButton.addActionListener(e -> {
            // If nothing loaded, select the highlighted song
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
                // PAUSED -> resume, switch label to Pause
                audioEngine.resume();
                panel.playButton.setText("⏸ Pause");
                fireSongChangeEvent(current, SongChangeEvent.Type.PLAYING);
            } else if (audioEngine.isPlaying()) {
                // PLAYING -> pause, switch label to Play
                audioEngine.pause();
                panel.playButton.setText("▶ Play");
                fireSongChangeEvent(current, SongChangeEvent.Type.PAUSED);
            } else {
                // STOPPED -> play from beginning, switch label to Pause
                audioEngine.play(current.getFilePath());
                panel.playButton.setText("⏸ Pause");
                fireSongChangeEvent(current, SongChangeEvent.Type.PLAYING);
            }
        });

        // Stop button
        panel.stopButton.addActionListener(e -> {
            audioEngine.stop();
            panel.playButton.setText("▶ Play"); // Reset toggle label
            if (currentIndex != -1) {
                fireSongChangeEvent(playlist.getSongs().get(currentIndex), SongChangeEvent.Type.STOPPED);
            }
        });

        // Next / Prev
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

        // Progress slider: seek when user drags & releases
        panel.progressSlider.addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                userIsDraggingSlider = true;
            }

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