/**
 * Audio Player Interface - Business Interface
 * Provides contract for audio playback operations
 * Follows Design by Contract with explicit preconditions and postconditions
 */
public interface IAudioPlayer {
    
    /**
     * Load media file for playback
     * Precondition: file must not be null and must be valid MediaFile
     * Postcondition: player is ready to play the loaded file
     */
    boolean loadMedia(MediaFile file);
    
    /**
     * Start or resume playback
     * Precondition: media must be loaded
     * Postcondition: playback is active (isPlaying() returns true)
     */
    void play();
    
    /**
     * Pause current playback
     * Precondition: playback must be active
     * Postcondition: playback is paused (isPlaying() returns false)
     */
    void pause();
    
    /**
     * Stop playback completely
     * Postcondition: playback is stopped, position reset to beginning
     */
    void stop();
    
    /**
     * Set playback volume
     * Precondition: volume must be between 0.0 and 1.0
     * Postcondition: playback volume is adjusted
     */
    void setVolume(float volume);
    
    /**
     * Get current playback position in seconds
     * Postcondition: returns current position or 0 if not playing
     */
    int getCurrentPosition();
    
    /**
     * Get total duration of current media in seconds
     * Postcondition: returns duration or 0 if no media loaded
     */
    int getDuration();
    
    /**
     * Check if currently playing
     * Postcondition: returns true if audio is currently playing
     */
    boolean isPlaying();
    
    /**
     * Check if player is paused
     * Postcondition: returns true if player is in paused state
     */
    boolean isPaused();
    
    /**
     * Get current volume level
     * Postcondition: returns volume between 0.0 and 1.0
     */
    float getVolume();
    
    /**
     * Check if media is loaded and ready to play
     * Postcondition: returns true if media is loaded successfully
     */
    boolean isMediaLoaded();
}