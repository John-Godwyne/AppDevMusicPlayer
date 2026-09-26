package controller;

import java.io.File;
import javax.sound.sampled.*;

public class AudioEngine {
    private Clip clip;
    private long pausePosition = 0;
    private boolean isPaused = false;

    public void play(String filePath) {
        try {
            stop();
            File audioFile = new File(filePath);
            AudioInputStream audioStream = AudioSystem.getAudioInputStream(audioFile);
            clip = AudioSystem.getClip();
            clip.open(audioStream);
            clip.start();
            isPaused = false;
        } catch (Exception e) {
            System.out.println("Error playing audio. Make sure it is a .wav file! " + e.getMessage());
        }
    }

    public void pause() {
        if (clip != null && clip.isRunning()) {
            pausePosition = clip.getMicrosecondPosition();
            clip.stop();
            isPaused = true;
        }
    }

    public void resume() {
        if (clip != null && isPaused) {
            clip.setMicrosecondPosition(pausePosition);
            clip.start();
            isPaused = false;
        }
    }

    public void stop() {
        if (clip != null && clip.isOpen()) {
            clip.stop();
            clip.close();
            isPaused = false;
            pausePosition = 0;
        }
    }

    public boolean isPaused() { return isPaused; }

    // NEW: for the progress slider
    public boolean isPlaying() {
        return clip != null && clip.isRunning();
    }

    public long getPositionMicros() {
        return clip != null ? clip.getMicrosecondPosition() : 0;
    }

    public long getDurationMicros() {
        return clip != null ? clip.getMicrosecondLength() : 0;
    }

    public void seekMicros(long micros) {
        if (clip != null) {
            boolean wasRunning = clip.isRunning();
            clip.stop();
            clip.setMicrosecondPosition(micros);
            if (wasRunning) clip.start();
        }
    }
}