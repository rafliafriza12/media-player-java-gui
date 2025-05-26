import javax.sound.sampled.AudioFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class AudioProcessor {
    private float[] bands;
    private boolean equalizerEnabled;
    private List<EqualizerListener> listeners;
    private Map<String, float[]> presets;
    private String currentPreset;
    
    public AudioProcessor() {
        bands = new float[10];
        listeners = new ArrayList<>();
        presets = new HashMap<>();
        equalizerEnabled = false;
        currentPreset = "Flat";
        initializePresets();
        resetEqualizer();
    }
    
    private void initializePresets() {
        presets.put("Flat", new float[]{0, 0, 0, 0, 0, 0, 0, 0, 0, 0});
        presets.put("Rock", new float[]{3, 2, -1, -2, 1, 2, 3, 4, 4, 3});
        presets.put("Pop", new float[]{-1, 2, 3, 4, 2, 0, -1, -1, 2, 3});
        presets.put("Jazz", new float[]{2, 1, 0, 1, 2, 2, 1, 1, 2, 3});
        presets.put("Classical", new float[]{3, 2, 0, 0, 0, 0, 0, 1, 2, 3});
        presets.put("Bass Boost", new float[]{4, 3, 2, 1, 0, 0, 0, 0, 0, 0});
        presets.put("Treble Boost", new float[]{0, 0, 0, 0, 0, 0, 1, 2, 3, 4});
    }
    
    public void addEqualizerListener(EqualizerListener listener) {
        listeners.add(listener);
    }
    
    public void removeEqualizerListener(EqualizerListener listener) {
        listeners.remove(listener);
    }
    
    public void applyEqualizer(byte[] audioData, AudioFormat format) {
        if (!equalizerEnabled) return;
        // DSP implementation would go here
    }
    
    public void setBand(int band, float value) {
        if (band >= 0 && band < bands.length) {
            bands[band] = Math.max(-12.0f, Math.min(12.0f, value));
            for (EqualizerListener listener : listeners) {
                listener.onBandChanged(band, bands[band]);
            }
        }
    }
    
    public float getBand(int band) {
        if (band >= 0 && band < bands.length) {
            return bands[band];
        }
        return 0.0f;
    }
    
    public void setPreset(String presetName) {
        if (presets.containsKey(presetName)) {
            float[] presetValues = presets.get(presetName);
            System.arraycopy(presetValues, 0, bands, 0, bands.length);
            currentPreset = presetName;
            for (EqualizerListener listener : listeners) {
                listener.onPresetChanged(presetName);
            }
        }
    }
    
    public String getCurrentPreset() {
        return currentPreset;
    }
    
    public String[] getAvailablePresets() {
        return presets.keySet().toArray(new String[0]);
    }
    
    public void setEnabled(boolean enabled) {
        this.equalizerEnabled = enabled;
        for (EqualizerListener listener : listeners) {
            listener.onEqualizerEnabled(enabled);
        }
    }
    
    public boolean isEnabled() {
        return equalizerEnabled;
    }
    
    public void resetEqualizer() {
        for (int i = 0; i < bands.length; i++) {
            bands[i] = 0.0f;
        }
        currentPreset = "Flat";
        for (EqualizerListener listener : listeners) {
            listener.onEqualizerReset();
        }
    }
    
    public int getBandCount() {
        return bands.length;
    }
    
    public String getBandFrequency(int band) {
        String[] frequencies = {"32Hz", "64Hz", "125Hz", "250Hz", "500Hz", 
                              "1kHz", "2kHz", "4kHz", "8kHz", "16kHz"};
        if (band >= 0 && band < frequencies.length) {
            return frequencies[band];
        }
        return "";
    }
}
