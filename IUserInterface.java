/**
 * User Interface Interface - System Interface
 * Provides contract for user interface operations
 * Defines the presentation layer contract
 */
public interface IUserInterface {
    
    /**
     * Initialize and show the user interface
     * Postcondition: UI is visible and ready for user interaction
     */
    void show();
    
    /**
     * Hide the user interface
     * Postcondition: UI is hidden from user
     */
    void hide();
    
    /**
     * Update playlist display
     * Postcondition: playlist view reflects current playlist state
     */
    void updatePlaylistDisplay();
    
    /**
     * Update playback status display
     * Precondition: status must not be null
     * Postcondition: status is displayed to user
     */
    void updatePlaybackStatus(String status);
    
    /**
     * Update progress display
     * Precondition: position and duration must be non-negative
     * Postcondition: progress is visually updated
     */
    void updateProgress(int position, int duration);
    
    /**
     * Update volume display
     * Precondition: volume must be between 0.0 and 1.0
     * Postcondition: volume level is visually updated
     */
    void updateVolumeDisplay(float volume);
    
    /**
     * Update current track display
     * Postcondition: current track information is displayed
     */
    void updateCurrentTrackDisplay(MediaFile file);
    
    /**
     * Show error message to user
     * Precondition: message must not be null
     * Postcondition: error is displayed to user
     */
    void showError(String message);
    
    /**
     * Show information message to user
     * Precondition: message must not be null
     * Postcondition: information is displayed to user
     */
    void showInfo(String message);
    
    /**
     * Enable or disable playback controls
     * Postcondition: controls are enabled/disabled based on parameter
     */
    void setControlsEnabled(boolean enabled);
    
    /**
     * Update repeat mode display
     * Postcondition: repeat mode is visually indicated
     */
    void updateRepeatModeDisplay(RepeatMode mode);
    
    /**
     * Update shuffle mode display
     * Postcondition: shuffle mode is visually indicated
     */
    void updateShuffleModeDisplay(boolean enabled);
}