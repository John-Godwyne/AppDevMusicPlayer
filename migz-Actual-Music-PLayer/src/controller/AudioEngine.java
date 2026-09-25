package controller;

import javazoom.jl.player.Player;
import java.io.FileInputStream;

public class AudioEngine {
    private Player player;
    private Thread playThread;
    private FileInputStream fileInputStream;
    private boolean isPaused = false;
    private long pauseLocation = 0;
    private String currentFilePath;

    // Play a new song
    public void play(String filePath) {
        try {
            stop(); // Stop any currently playing song
            this.currentFilePath = filePath;
            this.fileInputStream = new FileInputStream(filePath);
            this.player = new Player(fileInputStream);
            
            playThread = new Thread(() -> {
                try {
                    player.play();
                } catch (Exception e) {
                    System.out.println("Error playing audio: " + e.getMessage());
                }
            });
            playThread.start();
            isPaused = false;
            System.out.println("Playing: " + filePath);
        } catch (Exception e) {
            System.out.println("Error starting audio: " + e.getMessage());
        }
    }

    // Pause the song
    public void pause() {
        if (player != null && !isPaused) {
            try {
                // JLayer doesn't have a native pause, so we calculate where we are and stop
                pauseLocation = fileInputStream.available();
                player.close();
                isPaused = true;
                System.out.println("Paused");
            } catch (Exception e) {
                System.out.println("Error pausing: " + e.getMessage());
            }
        }
    }

    // Resume the song from where it was paused
    public void resume() {
        if (isPaused && currentFilePath != null) {
            try {
                fileInputStream = new FileInputStream(currentFilePath);
                // Skip to the point where we paused
                fileInputStream.skip(fileInputStream.available() - pauseLocation);
                player = new Player(fileInputStream);
                
                playThread = new Thread(() -> {
                    try {
                        player.play();
                    } catch (Exception e) {
                        System.out.println("Error resuming audio: " + e.getMessage());
                    }
                });
                playThread.start();
                isPaused = false;
                System.out.println("Resumed");
            } catch (Exception e) {
                System.out.println("Error resuming: " + e.getMessage());
            }
        }
    }

    // Stop the song entirely
    public void stop() {
        if (player != null) {
            player.close();
            player = null;
            isPaused = false;
            pauseLocation = 0;
            System.out.println("Stopped");
        }
    }
    
    public boolean isPaused() {
        return isPaused;
    }
}