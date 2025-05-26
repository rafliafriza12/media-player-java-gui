// Enhanced MediaPlayer.java
import java.io.BufferedInputStream;
import java.io.FileInputStream;
import javazoom.jl.player.Player;
import java.util.ArrayList;
import java.util.List;

public class MediaPlayer {
    private MediaFile currentFile;
    private Player player;
    private Thread playerThread;
    private float volume = 0.8f;
    private boolean initialized = false;
    private boolean isPaused = false;
    private long pausePosition = 0;
    private List<MediaPlayerEventListener> listeners;
    private AudioProcessor audioProcessor;
    
    public MediaPlayer() {
        listeners = new ArrayList<>();
        audioProcessor = new AudioProcessor();
    }
    
    public void addListener(MediaPlayerEventListener listener) {
        listeners.add(listener);
    }
    
    public void removeListener(MediaPlayerEventListener listener) {
        listeners.remove(listener);
    }
    
    private void notifyListeners(String event) {
        for (MediaPlayerEventListener listener : listeners) {
            switch (event) {
                case "fileLoaded":
                    listener.onFileLoaded(currentFile);
                    break;
                case "playbackStarted":
                    listener.onPlaybackStarted();
                    break;
                case "playbackPaused":
                    listener.onPlaybackPaused();
                    break;
                case "playbackStopped":
                    listener.onPlaybackStopped();
                    break;
                case "playbackCompleted":
                    listener.onPlaybackCompleted();
                    break;
            }
        }
    }
    
    public void loadMedia(MediaFile file) {
        stop();
        this.currentFile = file;
        initialized = true;
        notifyListeners("fileLoaded");
    }
    
    public void play() {
        if (!initialized || currentFile == null) return;
        
        if (isPaused) {
            isPaused = false;
        }
        
        try {
            stop();
            
            playerThread = new Thread(() -> {
                try {
                    FileInputStream fis = new FileInputStream(currentFile.getFilePath());
                    BufferedInputStream bis = new BufferedInputStream(fis);
                    player = new Player(bis);
                    notifyListeners("playbackStarted");
                    player.play();
                    notifyListeners("playbackCompleted");
                } catch (Exception e) {
                    for (MediaPlayerEventListener listener : listeners) {
                        listener.onError("Error playing media: " + e.getMessage());
                    }
                }
            });
            
            playerThread.start();
        } catch (Exception e) {
            for (MediaPlayerEventListener listener : listeners) {
                listener.onError("Error starting playback: " + e.getMessage());
            }
        }
    }
    
    public void pause() {
        isPaused = true;
        if (player != null) {
            try {
                player.close();
                notifyListeners("playbackPaused");
            } catch (Exception e) {
                // Ignore
            }
        }
    }
    
    public void stop() {
        if (player != null) {
            try {
                player.close();
            } catch (Exception e) {
                // Ignore
            }
            player = null;
        }
        
        if (playerThread != null && playerThread.isAlive()) {
            playerThread.interrupt();
            try {
                playerThread.join(1000);
            } catch (InterruptedException e) {
                // Ignore
            }
        }
        
        isPaused = false;
        notifyListeners("playbackStopped");
    }
    
    public void setVolume(float volume) {
        this.volume = Math.max(0.0f, Math.min(1.0f, volume));
        for (MediaPlayerEventListener listener : listeners) {
            listener.onVolumeChanged(this.volume);
        }
    }
    
    public float getVolume() {
        return volume;
    }
    
    public int getCurrentPosition() {
        return 0; // JLayer limitation
    }
    
    public int getDuration() {
        return currentFile != null ? currentFile.getDuration() : 0;
    }
    
    public boolean isPlaying() {
        return player != null && playerThread != null && playerThread.isAlive() && !isPaused;
    }
    
    public AudioProcessor getAudioProcessor() {
        return audioProcessor;
    }
}
