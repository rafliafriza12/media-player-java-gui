import java.io.BufferedInputStream;
import java.io.FileInputStream;
import javazoom.jl.player.Player;

public class MediaPlayer {
    private MediaFile currentFile;
    private Player player;
    private Thread playerThread;
    private float volume = 0.8f;
    private boolean initialized = false;
    private boolean isPaused = false;
    private long pausePosition = 0;
    
    public MediaPlayer() {
        // No audio processor needed with JLayer
    }
    
    public void loadMedia(MediaFile file) {
        stop();
        this.currentFile = file;
        initialized = true;
    }
    
    public void play() {
        if (!initialized || currentFile == null) return;
        
        if (isPaused) {
            isPaused = false;
            // Implementasi resume akan lebih kompleks dengan JLayer
            // Untuk sederhananya, kita mulai dari awal saja
        }
        
        try {
            stop();
            
            playerThread = new Thread(() -> {
                try {
                    FileInputStream fis = new FileInputStream(currentFile.getFilePath());
                    BufferedInputStream bis = new BufferedInputStream(fis);
                    player = new Player(bis);
                    player.play();
                } catch (Exception e) {
                    System.err.println("Error playing media: " + e.getMessage());
                }
            });
            
            playerThread.start();
        } catch (Exception e) {
            System.err.println("Error starting playback: " + e.getMessage());
        }
    }
    
    public void pause() {
        isPaused = true;
        if (player != null) {
            // JLayer tidak mendukung pause secara native
            // Implementasi sederhana: stop player
            try {
                player.close();
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
    }
    
    public void setVolume(float volume) {
        // JLayer tidak mendukung pengaturan volume secara langsung
        this.volume = volume;
    }
    
    public int getCurrentPosition() {
        // JLayer tidak menyediakan informasi posisi current playback
        return 0;
    }
    
    public int getDuration() {
        // JLayer tidak menyediakan informasi durasi
        return currentFile != null ? currentFile.getDuration() : 0;
    }
    
    public boolean isPlaying() {
        return player != null && playerThread != null && playerThread.isAlive() && !isPaused;
    }
}