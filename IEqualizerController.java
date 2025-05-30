/**
 * Equalizer Controller Interface - Business Interface
 * Provides contract for audio equalization operations
 * Demonstrates advanced audio processing interface
 */
public interface IEqualizerController {
    
    /**
     * Set equalizer band value
     * Precondition: band must be valid (0 <= band < getNumberOfBands())
     * Precondition: value must be between -20.0 and +20.0 dB
     * Postcondition: band frequency response is adjusted
     */
    void setBand(int band, float value);
    
    /**
     * Get equalizer band value
     * Precondition: band must be valid (0 <= band < getNumberOfBands())
     * Postcondition: returns current band value in dB
     */
    float getBand(int band);
    
    /**
     * Reset all equalizer bands to flat response
     * Postcondition: all bands are set to 0.0 dB
     */
    void resetEqualizer();
    
    /**
     * Enable or disable equalizer
     * Postcondition: equalizer processing is enabled/disabled
     */
    void setEnabled(boolean enabled);
    
    /**
     * Check if equalizer is enabled
     * Postcondition: returns true if equalizer is active
     */
    boolean isEnabled();
    
    /**
     * Get number of equalizer bands
     * Postcondition: returns total number of frequency bands
     */
    int getNumberOfBands();
    
    /**
     * Get frequency range for specific band
     * Precondition: band must be valid
     * Postcondition: returns center frequency in Hz
     */
    float getBandFrequency(int band);
    
    /**
     * Apply preset equalizer settings
     * Precondition: preset must not be null
     * Postcondition: equalizer bands are set according to preset
     */
    void applyPreset(EqualizerPreset preset);
    
    /**
     * Get current preset name
     * Postcondition: returns name of current preset or "Custom"
     */
    String getCurrentPresetName();
    
    /**
     * Get all available presets
     * Postcondition: returns array of available presets
     */
    EqualizerPreset[] getAvailablePresets();
    
    /**
     * Save current settings as custom preset
     * Precondition: name must not be null or empty
     * Postcondition: preset is saved for future use
     */
    boolean savePreset(String name, float[] bandValues);
}