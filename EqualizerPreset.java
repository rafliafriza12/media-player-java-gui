/**
 * Equalizer Preset Value Object
 * Part of Business Type Model for audio processing
 * Encapsulates equalizer preset data and metadata
 */
public class EqualizerPreset {
    private String name;
    private String description;
    private float[] bandValues;
    private boolean isBuiltIn;
    
    public EqualizerPreset(String name, String description, float[] bandValues, boolean isBuiltIn) {
        this.name = name;
        this.description = description;
        this.bandValues = bandValues.clone(); // Defensive copy
        this.isBuiltIn = isBuiltIn;
    }
    
    public String getName() {
        return name;
    }
    
    public String getDescription() {
        return description;
    }
    
    public float[] getBandValues() {
        return bandValues.clone(); // Return defensive copy
    }
    
    public boolean isBuiltIn() {
        return isBuiltIn;
    }
    
    public int getNumberOfBands() {
        return bandValues.length;
    }
    
    @Override
    public String toString() {
        return name;
    }
    
    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        EqualizerPreset preset = (EqualizerPreset) obj;
        return name.equals(preset.name);
    }
    
    @Override
    public int hashCode() {
        return name.hashCode();
    }
    
    // Factory methods for common presets
    public static EqualizerPreset createFlat() {
        return new EqualizerPreset("Flat", "No equalization", 
            new float[]{0, 0, 0, 0, 0, 0, 0, 0, 0, 0}, true);
    }
    
    public static EqualizerPreset createRock() {
        return new EqualizerPreset("Rock", "Enhanced bass and treble for rock music",
            new float[]{3, 2, -1, -2, 1, 2, 3, 4, 5, 5}, true);
    }
    
    public static EqualizerPreset createPop() {
        return new EqualizerPreset("Pop", "Balanced response for pop music",
            new float[]{1, 2, 3, 2, 0, -1, 1, 2, 3, 2}, true);
    }
    
    public static EqualizerPreset createJazz() {
        return new EqualizerPreset("Jazz", "Warm midrange for jazz",
            new float[]{2, 1, 0, 1, 3, 3, 2, 1, 0, 1}, true);
    }
    
    public static EqualizerPreset createClassical() {
        return new EqualizerPreset("Classical", "Natural response for classical music",
            new float[]{2, 1, 0, 0, -1, -1, 0, 1, 2, 3}, true);
    }
    
    public static EqualizerPreset createBassBoost() {
        return new EqualizerPreset("Bass Boost", "Enhanced low frequencies",
            new float[]{6, 5, 4, 3, 2, 0, 0, 0, 0, 0}, true);
    }
    
    public static EqualizerPreset createTrebleBoost() {
        return new EqualizerPreset("Treble Boost", "Enhanced high frequencies",
            new float[]{0, 0, 0, 0, 0, 2, 3, 4, 5, 6}, true);
    }
    
    public static EqualizerPreset createVocal() {
        return new EqualizerPreset("Vocal", "Enhanced midrange for vocals",
            new float[]{-2, -1, 1, 3, 4, 4, 3, 1, -1, -2}, true);
    }
}