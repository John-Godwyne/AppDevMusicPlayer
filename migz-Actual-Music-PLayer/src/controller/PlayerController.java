package controller;

import events.SongChangeEvent;
import events.SongChangeListener;
import model.Playlist;
import model.Song;
import persistence.FileManager;
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

    public PlayerController(MainFrame view) {
        this.view = view;
        this.panel = view.playerPanel;
        this.playlist = new Playlist();
        this.audioEngine = new AudioEngine();

        // Load saved songs from file
        List<Song> savedSongs = FileManager.loadSongs();
        for (Song s : savedSongs) {
            playlist.addSong(s);
            panel.listModel.addElement(s.toString());
        }

        initController();
    }

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
        // Song Selection
        panel.songList.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                int index = panel.songList.getSelectedIndex();
                if (index != -1) {
                    fireSongChangeEvent(playlist.getSongs().get(index));
                }
            }
        });

        // Buttons
        panel.playButton.addActionListener(e -> {
            int index = panel.songList.getSelectedIndex();
            if (index != -1) {
                Song selected = playlist.getSongs().get(index);
                if (audioEngine.isPaused()) {
                    audioEngine.resume();
                } else {
                    audioEngine.play(selected.getFilePath());
                }
            } else {
                JOptionPane.showMessageDialog(view, "Select a song first!");
            }
        });

        panel.pauseButton.addActionListener(e -> audioEngine.pause());
        panel.stopButton.addActionListener(e -> audioEngine.stop());
    }
}