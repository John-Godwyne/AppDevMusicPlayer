package controller;

import java.io.File;
import javax.sound.sampled.*;

public class AudioEngine {
    private Clip clip;
    private long pausePosition = 0;
    private boolean isPaused = false;
    
    // Volume control: 0.0 (silent) to 1.0 (max)
    private float currentVolume = 0.8f; // Default 80%

    public void play(String filePath) {
        try {
            stop();
            File audioFile = new File(filePath);
            AudioInputStream audioStream = AudioSystem.getAudioInputStream(audioFile);
            clip = AudioSystem.getClip();
            clip.open(audioStream);
            applyVolume(); // Apply saved volume when starting a new song
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
            applyVolume();
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

    // ================= VOLUME CONTROL =================
    public void setVolume(float volume) {
        // Clamp volume between 0.0 and 1.0
        if (volume < 0f) volume = 0f;
        if (volume > 1f) volume = 1f;
        currentVolume = volume;
        applyVolume();
    }

    public float getVolume() {
        return currentVolume;
    }

    private void applyVolume() {
        if (clip == null) return;
        try {
            if (clip.isControlSupported(FloatControl.Type.MASTER_GAIN)) {
                FloatControl gainControl = (FloatControl) clip.getControl(FloatControl.Type.MASTER_GAIN);
                
                // Convert linear 0.0–1.0 to decibels
                float dB;
                if (currentVolume == 0f) {
                    dB = gainControl.getMinimum();
                } else {
                    dB = (float) (Math.log10(currentVolume) * 20.0);
                }
                
                // Clamp to the control's supported range
                dB = Math.max(gainControl.getMinimum(), Math.min(gainControl.getMaximum(), dB));
                gainControl.setValue(dB);
            }
        } catch (Exception e) {
            System.out.println("Volume control not supported: " + e.getMessage());
        }
    }
}