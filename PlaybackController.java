import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

/**
 * Playback Controller Component Implementation
 * Coordinates between audio player and playlist manager
 * Implements IPlaybackController interface
 */
public class PlaybackController implements IPlaybackController {
    
    private IAudioPlayer audioPlayer;
    private IPlaylistManager playlistManager;
    private List<IPlaybackListener> listeners;
    private RepeatMode repeatMode;
    private boolean shuffleMode;
    private Timer progressTimer;
    private MediaFile currentFile;
    
    public PlaybackController() {
        this.audioPlayer = new AudioPlayer();
        this.playlistManager = new PlaylistManager();
        this.listeners = new ArrayList<>();
        this.repeatMode = RepeatMode.NONE;
        this.shuffleMode = false;
        this.currentFile = null;
        
        initializeProgressTimer();
    }
    
    /**
     * Constructor with dependency injection for testing
     */
    public PlaybackController(IAudioPlayer audioPlayer, IPlaylistManager playlistManager) {
        this.audioPlayer = audioPlayer;
        this.playlistManager = playlistManager;
        this.listeners = new ArrayList<>();
        this.repeatMode = RepeatMode.NONE;
        this.shuffleMode = false;
        this.currentFile = null;
        
        initializeProgressTimer();
    }
    
    private void initializeProgressTimer() {
        progressTimer = new Timer(true); // Daemon timer
    }
    
    @Override
    public void play() {
        if (currentFile == null && !playlistManager.isEmpty()) {
            currentFile = playlistManager.getCurrentFile();
        }
        
        if (currentFile == null) {
            throw new IllegalStateException("No media available for playback");
        }
        
        try {
            if (!audioPlayer.isMediaLoaded() || !currentFile.equals(getCurrentLoadedFile())) {
                if (!audioPlayer.loadMedia(currentFile)) {
                    notifyError(currentFile, "Failed to load media file");
                    return;
                }
            }
            
            audioPlayer.play();
            notifyPlaybackStarted(currentFile);
            startProgressReporting();
            
        } catch (Exception e) {
            notifyError(currentFile, "Error starting playback: " + e.getMessage());
        }
    }
    
    @Override
    public void loadAndPlay(MediaFile file) {
        // Precondition check
        if (file == null) {
            throw new IllegalArgumentException("MediaFile cannot be null");
        }
        
        currentFile = file;
        
        // Update playlist current index if file is in playlist
        for (int i = 0; i < playlistManager.size(); i++) {
            if (file.equals(playlistManager.getFileAt(i))) {
                playlistManager.setCurrentIndex(i);
                break;
            }
        }
        
        play();
    }
    
    @Override
    public void pause() {
        if (!audioPlayer.isPlaying()) {
            return;
        }
        
        try {
            audioPlayer.pause();
            stopProgressReporting();
            notifyPlaybackPaused(currentFile);
        } catch (Exception e) {
            notifyError(currentFile, "Error pausing playback: " + e.getMessage());
        }
    }
    
    @Override
    public void stop() {
        try {
            audioPlayer.stop();
            stopProgressReporting();
            if (currentFile != null) {
                notifyPlaybackStopped(currentFile);
            }
        } catch (Exception e) {
            if (currentFile != null) {
                notifyError(currentFile, "Error stopping playback: " + e.getMessage());
            }
        }
    }
    
    @Override
    public void next() {
        if (playlistManager.isEmpty()) {
            return;
        }
        
        MediaFile nextFile = null;
        
        if (shuffleMode) {
            // In shuffle mode, get a random file
            if (playlistManager.size() > 1) {
                int randomIndex;
                do {
                    randomIndex = (int) (Math.random() * playlistManager.size());
                } while (randomIndex == playlistManager.getCurrentIndex() && playlistManager.size() > 1);
                
                playlistManager.setCurrentIndex(randomIndex);
                nextFile = playlistManager.getCurrentFile();
            } else {
                nextFile = playlistManager.getCurrentFile();
            }
        } else {
            nextFile = playlistManager.getNextFile();
        }
        
        if (nextFile != null) {
            loadAndPlay(nextFile);
        }
    }
    
    @Override
    public void previous() {
        if (playlistManager.isEmpty()) {
            return;
        }
        
        MediaFile prevFile = playlistManager.getPreviousFile();
        if (prevFile != null) {
            loadAndPlay(prevFile);
        }
    }
    
    @Override
    public void setVolume(float volume) {
        // Precondition check
        if (volume < 0.0f || volume > 1.0f) {
            throw new IllegalArgumentException("Volume must be between 0.0 and 1.0");
        }
        
        audioPlayer.setVolume(volume);
        notifyVolumeChanged(volume);
    }
    
    @Override
    public int getCurrentPosition() {
        return audioPlayer.getCurrentPosition();
    }
    
    @Override
    public int getDuration() {
        return audioPlayer.getDuration();
    }
    
    @Override
    public void addPlaybackListener(IPlaybackListener listener) {
        // Precondition check
        if (listener == null) {
            throw new IllegalArgumentException("Listener cannot be null");
        }
        
        if (!listeners.contains(listener)) {
            listeners.add(listener);
        }
    }
    
    @Override
    public void removePlaybackListener(IPlaybackListener listener) {
        if (listener != null) {
            listeners.remove(listener);
        }
    }
    
    @Override
    public boolean isPlaying() {
        return audioPlayer.isPlaying();
    }
    
    @Override
    public MediaFile getCurrentFile() {
        return currentFile;
    }
    
    @Override
    public IPlaylistManager getPlaylistManager() {
        return playlistManager;
    }
    
    @Override
    public void setRepeatMode(RepeatMode mode) {
        if (mode != null && mode != this.repeatMode) {
            this.repeatMode = mode;
            notifyRepeatModeChanged(mode);
        }
    }
    
    @Override
    public RepeatMode getRepeatMode() {
        return repeatMode;
    }
    
    @Override
    public void setShuffleMode(boolean shuffle) {
        if (shuffle != this.shuffleMode) {
            this.shuffleMode = shuffle;
            
            if (shuffle) {
                playlistManager.shuffle();
            }
            
            notifyShuffleModeChanged(shuffle);
        }
    }
    
    @Override
    public boolean isShuffleMode() {
        return shuffleMode;
    }
    
    // Progress reporting methods
    private void startProgressReporting() {
        stopProgressReporting(); // Stop any existing timer
        
        progressTimer.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                if (audioPlayer.isPlaying() && currentFile != null) {
                    int position = audioPlayer.getCurrentPosition();
                    int duration = audioPlayer.getDuration();
                    notifyProgress(currentFile, position, duration);
                    
                    // Check if playback completed
                    if (duration > 0 && position >= duration) {
                        handlePlaybackCompletion();
                    }
                }
            }
        }, 0, 1000); // Update every second
    }
    
    private void stopProgressReporting() {
        if (progressTimer != null) {
            progressTimer.cancel();
            progressTimer = new Timer(true);
        }
    }
    
    private void handlePlaybackCompletion() {
        stopProgressReporting();
        notifyPlaybackCompleted(currentFile);
        
        // Handle repeat and next track logic
        switch (repeatMode) {
            case ONE:
                // Repeat current track
                play();
                break;
            case ALL:
                // Move to next track, or restart playlist if at end
                next();
                break;
            case NONE:
            default:
                // Move to next track if available
                if (playlistManager.getCurrentIndex() < playlistManager.size() - 1) {
                    next();
                } else {
                    stop();
                }
                break;
        }
    }
    
    private MediaFile getCurrentLoadedFile() {
        if (audioPlayer instanceof AudioPlayer) {
            return ((AudioPlayer) audioPlayer).getCurrentFile();
        }
        return null;
    }
    
    // Notification methods
    private void notifyPlaybackStarted(MediaFile file) {
        for (IPlaybackListener listener : listeners) {
            try {
                listener.onPlaybackStarted(file);
            } catch (Exception e) {
                System.err.println("Error notifying listener: " + e.getMessage());
            }
        }
    }
    
    private void notifyPlaybackPaused(MediaFile file) {
        for (IPlaybackListener listener : listeners) {
            try {
                listener.onPlaybackPaused(file);
            } catch (Exception e) {
                System.err.println("Error notifying listener: " + e.getMessage());
            }
        }
    }
    
    private void notifyPlaybackStopped(MediaFile file) {
        for (IPlaybackListener listener : listeners) {
            try {
                listener.onPlaybackStopped(file);
            } catch (Exception e) {
                System.err.println("Error notifying listener: " + e.getMessage());
            }
        }
    }
    
    private void notifyPlaybackCompleted(MediaFile file) {
        for (IPlaybackListener listener : listeners) {
            try {
                listener.onPlaybackCompleted(file);
            } catch (Exception e) {
                System.err.println("Error notifying listener: " + e.getMessage());
            }
        }
    }
    
    private void notifyProgress(MediaFile file, int position, int duration) {
        for (IPlaybackListener listener : listeners) {
            try {
                listener.onPlaybackProgress(file, position, duration);
            } catch (Exception e) {
                System.err.println("Error notifying listener: " + e.getMessage());
            }
        }
    }
    
    private void notifyError(MediaFile file, String error) {
        for (IPlaybackListener listener : listeners) {
            try {
                listener.onPlaybackError(file, error);
            } catch (Exception e) {
                System.err.println("Error notifying listener: " + e.getMessage());
            }
        }
    }
    
    private void notifyVolumeChanged(float volume) {
        for (IPlaybackListener listener : listeners) {
            try {
                listener.onVolumeChanged(volume);
            } catch (Exception e) {
                System.err.println("Error notifying listener: " + e.getMessage());
            }
        }
    }
    
    private void notifyRepeatModeChanged(RepeatMode mode) {
        for (IPlaybackListener listener : listeners) {
            try {
                listener.onRepeatModeChanged(mode);
            } catch (Exception e) {
                System.err.println("Error notifying listener: " + e.getMessage());
            }
        }
    }
    
    private void notifyShuffleModeChanged(boolean enabled) {
        for (IPlaybackListener listener : listeners) {
            try {
                listener.onShuffleModeChanged(enabled);
            } catch (Exception e) {
                System.err.println("Error notifying listener: " + e.getMessage());
            }
        }
    }
    
    /**
     * Cleanup resources
     */
    public void dispose() {
        stop();
        stopProgressReporting();
        listeners.clear();
    }
}