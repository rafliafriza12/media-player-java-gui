/**
 * Enumeration for repeat modes
 * Part of Business Type Model for playback control
 */
public enum RepeatMode {
    NONE("No Repeat"),
    ONE("Repeat One"),
    ALL("Repeat All");
    
    private final String displayName;
    
    RepeatMode(String displayName) {
        this.displayName = displayName;
    }
    
    public String getDisplayName() {
        return displayName;
    }
    
    @Override
    public String toString() {
        return displayName;
    }
    
    /**
     * Get next repeat mode in cycle
     * @return next repeat mode
     */
    public RepeatMode next() {
        switch (this) {
            case NONE: return ONE;
            case ONE: return ALL;
            case ALL: return NONE;
            default: return NONE;
        }
    }
}