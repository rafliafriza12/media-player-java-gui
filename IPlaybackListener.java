/**
 * Playback Listener Interface - Observer Interface
 * Defines contract for receiving playback events
 * Implements Observer pattern for loose coupling
 */
public interface IPlaybackListener {
    
    /**
     * Called when playback starts
     * @param file the media file that started playing
     */
    void onPlaybackStarted(MediaFile file);
    
    /**
     * Called when playback is paused
     * @param file the media file that was paused
     */
    void onPlaybackPaused(MediaFile file);
    
    /**
     * Called when playback is stopped
     * @param file the media file that was stopped
     */
    void onPlaybackStopped(MediaFile file);
    
    /**
     * Called when playback completes naturally
     * @param file the media file that completed
     */
    void onPlaybackCompleted(MediaFile file);
    
    /**
     * Called periodically during playback to report progress
     * @param file current media file
     * @param position current position in seconds
     * @param duration total duration in seconds
     */
    void onPlaybackProgress(MediaFile file, int position, int duration);
    
    /**
     * Called when an error occurs during playback
     * @param file the media file that caused the error
     * @param error error message
     */
    void onPlaybackError(MediaFile file, String error);
    
    /**
     * Called when volume changes
     * @param volume new volume level (0.0 to 1.0)
     */
    void onVolumeChanged(float volume);
    
    /**
     * Called when repeat mode changes
     * @param mode new repeat mode
     */
    void onRepeatModeChanged(RepeatMode mode);
    
    /**
     * Called when shuffle mode changes
     * @param enabled true if shuffle is now enabled
     */
    void onShuffleModeChanged(boolean enabled);
}