import java.io.BufferedInputStream;
import java.io.FileInputStream;
import javazoom.jl.player.Player;

/**
 * Audio Player Component Implementation
 * Implements IAudioPlayer interface using JLayer for MP3 playback
 */
public class AudioPlayer implements IAudioPlayer {
    private MediaFile currentFile;
    private Player player;
    private Thread playerThread;
    private float volume = 0.8f;
    private boolean isPlaying = false;
    private boolean isPaused = false;
    private boolean mediaLoaded = false;
    private long pausePosition = 0;
    
    public AudioPlayer() {
        // Initialize player state
    }
    
    @Override
    public boolean loadMedia(MediaFile file) {
        // Precondition check
        if (file == null) {
            throw new IllegalArgumentException("MediaFile cannot be null");
        }
        
        if (file.isCorrupted()) {
            return false;
        }
        
        stop(); // Stop current playback if any
        this.currentFile = file;
        this.mediaLoaded = true;
        this.isPaused = false;
        this.pausePosition = 0;
        
        return true;
    }
    
    @Override
    public void play() {
        // Precondition check
        if (!mediaLoaded || currentFile == null) {
            throw new IllegalStateException("No media loaded for playback");
        }
        
        if (isPlaying) {
            return; // Already playing
        }
        
        if (isPaused) {
            isPaused = false;
            // Note: JLayer doesn't support resume, so we restart from beginning
        }
        
        try {
            stop(); // Ensure clean state
            
            playerThread = new Thread(() -> {
                try {
                    FileInputStream fis = new FileInputStream(currentFile.getFilePath());
                    BufferedInputStream bis = new BufferedInputStream(fis);
                    player = new Player(bis);
                    
                    isPlaying = true;
                    player.play();
                    isPlaying = false; // Will be set to false when playback ends naturally
                    
                } catch (Exception e) {
                    System.err.println("Error during playback: " + e.getMessage());
                    isPlaying = false;
                } finally {
                    if (player != null) {
                        try {
                            player.close();
                        } catch (Exception e) {
                            // Ignore cleanup errors
                        }
                    }
                }
            });
            
            playerThread.setDaemon(true);
            playerThread.start();
            
        } catch (Exception e) {
            System.err.println("Error starting playback: " + e.getMessage());
            isPlaying = false;
        }
    }
    
    @Override
    public void pause() {
        if (!isPlaying) {
            return;
        }
        
        isPaused = true;
        isPlaying = false;
        
        if (player != null) {
            try {
                player.close();
            } catch (Exception e) {
                // Ignore errors during pause
            }
        }
        
        if (playerThread != null) {
            playerThread.interrupt();
        }
    }
    
    @Override
    public void stop() {
        isPlaying = false;
        isPaused = false;
        pausePosition = 0;
        
        if (player != null) {
            try {
                player.close();
            } catch (Exception e) {
                // Ignore cleanup errors
            }
            player = null;
        }
        
        if (playerThread != null && playerThread.isAlive()) {
            playerThread.interrupt();
            try {
                playerThread.join(1000);
            } catch (InterruptedException e) {
                // Ignore interruption
            }
            playerThread = null;
        }
    }
    
    @Override
    public void setVolume(float volume) {
        // Precondition check
        if (volume < 0.0f || volume > 1.0f) {
            throw new IllegalArgumentException("Volume must be between 0.0 and 1.0");
        }
        
        this.volume = volume;
        // Note: JLayer doesn't support runtime volume control
        // In a real implementation, you would use a different audio library
    }
    
    @Override
    public int getCurrentPosition() {
        // JLayer doesn't provide position information
        // In a real implementation, you would track this manually or use a different library
        return 0;
    }
    
    @Override
    public int getDuration() {
        return currentFile != null ? currentFile.getDuration() : 0;
    }
    
    @Override
    public boolean isPlaying() {
        return isPlaying && !isPaused;
    }
    
    @Override
    public boolean isPaused() {
        return isPaused;
    }
    
    @Override
    public float getVolume() {
        return volume;
    }
    
    @Override
    public boolean isMediaLoaded() {
        return mediaLoaded && currentFile != null && !currentFile.isCorrupted();
    }
    
    /**
     * Get currently loaded media file
     * @return current MediaFile or null if none loaded
     */
    public MediaFile getCurrentFile() {
        return currentFile;
    }
    
    /**
     * Check if player supports the given file format
     * @param format file format to check
     * @return true if format is supported
     */
    public boolean supportsFormat(String format) {
        // Currently only MP3 is fully supported with JLayer
        return "mp3".equalsIgnoreCase(format);
    }
}