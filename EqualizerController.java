import java.util.ArrayList;
import java.util.List;

/**
 * Equalizer Controller Component Implementation
 * Implements IEqualizerController interface
 * Provides 10-band graphic equalizer functionality
 */
public class EqualizerController implements IEqualizerController {
    
    private static final int NUMBER_OF_BANDS = 10;
    private static final float[] BAND_FREQUENCIES = {
        60, 170, 310, 600, 1000, 3000, 6000, 12000, 14000, 16000
    };
    
    private float[] bandValues;
    private boolean enabled;
    private String currentPresetName;
    private List<EqualizerPreset> customPresets;
    private EqualizerPreset[] builtInPresets;
    
    public EqualizerController() {
        bandValues = new float[NUMBER_OF_BANDS];
        enabled = false;
        currentPresetName = "Flat";
        customPresets = new ArrayList<>();
        
        initializeBuiltInPresets();
        resetEqualizer();
    }
    
    private void initializeBuiltInPresets() {
        builtInPresets = new EqualizerPreset[] {
            EqualizerPreset.createFlat(),
            EqualizerPreset.createRock(),
            EqualizerPreset.createPop(),
            EqualizerPreset.createJazz(),
            EqualizerPreset.createClassical(),
            EqualizerPreset.createBassBoost(),
            EqualizerPreset.createTrebleBoost(),
            EqualizerPreset.createVocal()
        };
    }
    
    @Override
    public void setBand(int band, float value) {
        // Precondition checks
        if (band < 0 || band >= NUMBER_OF_BANDS) {
            throw new IllegalArgumentException("Invalid band index: " + band);
        }
        if (value < -20.0f || value > 20.0f) {
            throw new IllegalArgumentException("Band value must be between -20.0 and +20.0 dB");
        }
        
        bandValues[band] = value;
        currentPresetName = "Custom";
        
        // In a real implementation, this would apply the EQ setting to audio processing
        applyEqualizationToAudio();
    }
    
    @Override
    public float getBand(int band) {
        // Precondition check
        if (band < 0 || band >= NUMBER_OF_BANDS) {
            throw new IllegalArgumentException("Invalid band index: " + band);
        }
        
        return bandValues[band];
    }
    
    @Override
    public void resetEqualizer() {
        for (int i = 0; i < NUMBER_OF_BANDS; i++) {
            bandValues[i] = 0.0f;
        }
        currentPresetName = "Flat";
        
        if (enabled) {
            applyEqualizationToAudio();
        }
    }
    
    @Override
    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
        
        if (enabled) {
            applyEqualizationToAudio();
        } else {
            // Bypass equalizer processing
            bypassEqualizationToAudio();
        }
    }
    
    @Override
    public boolean isEnabled() {
        return enabled;
    }
    
    @Override
    public int getNumberOfBands() {
        return NUMBER_OF_BANDS;
    }
    
    @Override
    public float getBandFrequency(int band) {
        // Precondition check
        if (band < 0 || band >= NUMBER_OF_BANDS) {
            throw new IllegalArgumentException("Invalid band index: " + band);
        }
        
        return BAND_FREQUENCIES[band];
    }
    
    @Override
    public void applyPreset(EqualizerPreset preset) {
        // Precondition check
        if (preset == null) {
            throw new IllegalArgumentException("Preset cannot be null");
        }
        
        float[] presetValues = preset.getBandValues();
        if (presetValues.length != NUMBER_OF_BANDS) {
            throw new IllegalArgumentException("Preset has incorrect number of bands");
        }
        
        System.arraycopy(presetValues, 0, bandValues, 0, NUMBER_OF_BANDS);
        currentPresetName = preset.getName();
        
        if (enabled) {
            applyEqualizationToAudio();
        }
    }
    
    @Override
    public String getCurrentPresetName() {
        return currentPresetName;
    }
    
    @Override
    public EqualizerPreset[] getAvailablePresets() {
        List<EqualizerPreset> allPresets = new ArrayList<>();
        
        // Add built-in presets
        for (EqualizerPreset preset : builtInPresets) {
            allPresets.add(preset);
        }
        
        // Add custom presets
        allPresets.addAll(customPresets);
        
        return allPresets.toArray(new EqualizerPreset[0]);
    }
    
    @Override
    public boolean savePreset(String name, float[] bandValues) {
        // Precondition checks
        if (name == null || name.trim().isEmpty()) {
            throw new IllegalArgumentException("Preset name cannot be null or empty");
        }
        if (bandValues == null || bandValues.length != NUMBER_OF_BANDS) {
            throw new IllegalArgumentException("Band values must have exactly " + NUMBER_OF_BANDS + " elements");
        }
        
        // Check if name already exists in built-in presets
        for (EqualizerPreset preset : builtInPresets) {
            if (preset.getName().equalsIgnoreCase(name.trim())) {
                return false; // Cannot overwrite built-in preset
            }
        }
        
        // Remove existing custom preset with same name
        customPresets.removeIf(preset -> preset.getName().equalsIgnoreCase(name.trim()));
        
        // Create and add new custom preset
        EqualizerPreset newPreset = new EqualizerPreset(
            name.trim(), 
            "Custom user preset", 
            bandValues, 
            false
        );
        customPresets.add(newPreset);
        
        return true;
    }
    
    /**
     * Get current equalizer settings as array
     * @return copy of current band values
     */
    public float[] getCurrentSettings() {
        return bandValues.clone();
    }
    
    /**
     * Get band frequency range as formatted string
     * @param band band index
     * @return formatted frequency string
     */
    public String getFormattedBandFrequency(int band) {
        if (band < 0 || band >= NUMBER_OF_BANDS) {
            return "Invalid";
        }
        
        float freq = BAND_FREQUENCIES[band];
        if (freq < 1000) {
            return String.format("%.0f Hz", freq);
        } else {
            return String.format("%.1f kHz", freq / 1000);
        }
    }
    
    /**
     * Get band value as formatted string
     * @param band band index
     * @return formatted dB value
     */
    public String getFormattedBandValue(int band) {
        if (band < 0 || band >= NUMBER_OF_BANDS) {
            return "Invalid";
        }
        
        float value = bandValues[band];
        if (value > 0) {
            return String.format("+%.1f dB", value);
        } else if (value < 0) {
            return String.format("%.1f dB", value);
        } else {
            return "0.0 dB";
        }
    }
    
    /**
     * Check if current settings match a preset
     * @param preset preset to check against
     * @return true if settings match preset
     */
    public boolean matchesPreset(EqualizerPreset preset) {
        if (preset == null) {
            return false;
        }
        
        float[] presetValues = preset.getBandValues();
        if (presetValues.length != NUMBER_OF_BANDS) {
            return false;
        }
        
        for (int i = 0; i < NUMBER_OF_BANDS; i++) {
            if (Math.abs(bandValues[i] - presetValues[i]) > 0.1f) {
                return false;
            }
        }
        
        return true;
    }
    
    /**
     * Remove custom preset
     * @param presetName name of preset to remove
     * @return true if preset was removed
     */
    public boolean removeCustomPreset(String presetName) {
        if (presetName == null || presetName.trim().isEmpty()) {
            return false;
        }
        
        return customPresets.removeIf(preset -> 
            preset.getName().equalsIgnoreCase(presetName.trim()) && !preset.isBuiltIn());
    }
    
    /**
     * Get number of custom presets
     * @return count of custom presets
     */
    public int getCustomPresetCount() {
        return customPresets.size();
    }
    
    // Private methods for audio processing
    private void applyEqualizationToAudio() {
        if (!enabled) {
            return;
        }
        
        // In a real implementation, this would apply the EQ settings to the audio stream
        // For demonstration, we just log the action
        System.out.println("Applying equalizer settings: " + getCurrentPresetName());
        for (int i = 0; i < NUMBER_OF_BANDS; i++) {
            System.out.printf("Band %d (%s): %s%n", 
                i, getFormattedBandFrequency(i), getFormattedBandValue(i));
        }
    }
    
    private void bypassEqualizationToAudio() {
        // In a real implementation, this would bypass EQ processing
        System.out.println("Equalizer bypassed");
    }
    
    /**
     * Export current settings to string format
     * @return string representation of current settings
     */
    public String exportSettings() {
        StringBuilder sb = new StringBuilder();
        sb.append("Preset: ").append(currentPresetName).append("\n");
        sb.append("Enabled: ").append(enabled).append("\n");
        sb.append("Bands: ");
        
        for (int i = 0; i < NUMBER_OF_BANDS; i++) {
            if (i > 0) sb.append(",");
            sb.append(bandValues[i]);
        }
        
        return sb.toString();
    }
    
    /**
     * Import settings from string format
     * @param settings string representation of settings
     * @return true if import successful
     */
    public boolean importSettings(String settings) {
        if (settings == null || settings.trim().isEmpty()) {
            return false;
        }
        
        try {
            String[] lines = settings.split("\n");
            String bandsLine = null;
            
            for (String line : lines) {
                if (line.startsWith("Bands: ")) {
                    bandsLine = line.substring(7);
                    break;
                }
            }
            
            if (bandsLine == null) {
                return false;
            }
            
            String[] values = bandsLine.split(",");
            if (values.length != NUMBER_OF_BANDS) {
                return false;
            }
            
            float[] newValues = new float[NUMBER_OF_BANDS];
            for (int i = 0; i < NUMBER_OF_BANDS; i++) {
                newValues[i] = Float.parseFloat(values[i].trim());
                if (newValues[i] < -20.0f || newValues[i] > 20.0f) {
                    return false; // Invalid range
                }
            }
            
            // Import successful, apply settings
            System.arraycopy(newValues, 0, bandValues, 0, NUMBER_OF_BANDS);
            currentPresetName = "Custom";
            
            if (enabled) {
                applyEqualizationToAudio();
            }
            
            return true;
            
        } catch (NumberFormatException e) {
            return false;
        }
    }
}