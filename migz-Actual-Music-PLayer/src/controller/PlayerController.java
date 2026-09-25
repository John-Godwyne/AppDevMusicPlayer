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
import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class PlayerController {
    private MainFrame view;
    private PlayerPanel panel;
    private Playlist playlist;
    private AudioEngine audioEngine;
    private List<SongChangeListener> listeners = new ArrayList<>();

    public PlayerController(MainFrame view, Playlist playlist) {
        this.view = view;
        this.panel = view.getPlayerPanel();
        this.playlist = playlist;
        this.audioEngine = new AudioEngine();

        initController();
    }

    // Register custom event listeners
    public void addSongChangeListener(SongChangeListener listener) {
        listeners.add(listener);
    }

    private void fireSongChangeEvent(Song song) {
        SongChangeEvent event = new SongChangeEvent(this, song);
        for (SongChangeListener listener : listeners) {
            listener.songChanged(event);
        }
    }

    private void initController() {
        // 1. Load songs from JPA Database into the Playlist Model
        List<Song> dbSongs = DatabaseManager.getAllSongs();
        for (Song s : dbSongs) {
            playlist.addSong(s);
            panel.getListModel().addElement(s.toString()); // Add to UI list
        }

        // 2. Handle Song Selection (Mouse Click on JList)
        panel.getSongList().addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                int index = panel.getSongList().getSelectedIndex();
                if (index != -1) {
                    Song selectedSong = playlist.getSongs().get(index);
                    // Fire custom event to update UI (Image & Lyrics)
                    fireSongChangeEvent(selectedSong); 
                }
            }
        });

        // 3. Handle Play Button
        panel.playButton.addActionListener(e -> {
            int index = panel.getSongList().getSelectedIndex();
            if (index != -1) {
                Song selectedSong = playlist.getSongs().get(index);
                if (audioEngine.isPaused()) {
                    audioEngine.resume();
                } else {
                    audioEngine.play(selectedSong.getFilePath());
                }
            } else {
                JOptionPane.showMessageDialog(view, "Please select a song first!");
            }
        });

        // 4. Handle Pause Button
        panel.pauseButton.addActionListener(e -> audioEngine.pause());

        // 5. Handle Stop Button
        panel.stopButton.addActionListener(e -> audioEngine.stop());
    }
}