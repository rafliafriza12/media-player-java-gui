/**
 * Playback Controller Interface - System Interface
 * Coordinates between audio player and playlist manager
 * Main controller interface for the media player system
 */
public interface IPlaybackController {
    
    /**
     * Start playback of current file
     * Precondition: playlist must have at least one file
     * Postcondition: playback is started for current or first file
     */
    void play();
    
    /**
     * Load and immediately play specified file
     * Precondition: file must not be null and must be valid
     * Postcondition: file is loaded and playback started
     */
    void loadAndPlay(MediaFile file);
    
    /**
     * Pause current playback
     * Precondition: playback must be active
     * Postcondition: playback is paused
     */
    void pause();
    
    /**
     * Stop current playback
     * Postcondition: playback is stopped completely
     */
    void stop();
    
    /**
     * Move to next track in playlist
     * Precondition: playlist must not be empty
     * Postcondition: next track is loaded and played
     */
    void next();
    
    /**
     * Move to previous track in playlist
     * Precondition: playlist must not be empty
     * Postcondition: previous track is loaded and played
     */
    void previous();
    
    /**
     * Set playback volume
     * Precondition: volume must be between 0.0 and 1.0
     * Postcondition: volume is applied to current playback
     */
    void setVolume(float volume);
    
    /**
     * Get current playback position
     * Postcondition: returns position in seconds
     */
    int getCurrentPosition();
    
    /**
     * Get duration of current track
     * Postcondition: returns duration in seconds or 0 if no track
     */
    int getDuration();
    
    /**
     * Add playback event listener
     * Precondition: listener must not be null
     * Postcondition: listener is added to notification list
     */
    void addPlaybackListener(IPlaybackListener listener);
    
    /**
     * Remove playback event listener
     * Precondition: listener must not be null
     * Postcondition: listener is removed from notification list
     */
    void removePlaybackListener(IPlaybackListener listener);
    
    /**
     * Check if currently playing
     * Postcondition: returns true if audio is playing
     */
    boolean isPlaying();
    
    /**
     * Get current file being played
     * Postcondition: returns current MediaFile or null
     */
    MediaFile getCurrentFile();
    
    /**
     * Get playlist manager
     * Postcondition: returns the playlist manager instance
     */
    IPlaylistManager getPlaylistManager();
    
    /**
     * Set repeat mode
     * Postcondition: repeat mode is updated
     */
    void setRepeatMode(RepeatMode mode);
    
    /**
     * Get current repeat mode
     * Postcondition: returns current repeat mode
     */
    RepeatMode getRepeatMode();
    
    /**
     * Set shuffle mode
     * Postcondition: shuffle mode is updated
     */
    void setShuffleMode(boolean shuffle);
    
    /**
     * Check if shuffle mode is enabled
     * Postcondition: returns true if shuffle is enabled
     */
    boolean isShuffleMode();
}