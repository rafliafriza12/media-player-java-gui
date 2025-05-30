import java.io.Serializable;
import java.io.File;

/**
 * Media File entity representing audio files with metadata
 * Based on Business Type Model for media management
 */
public class MediaFile implements Serializable {
    private static final long serialVersionUID = 1L;
    
    private String filePath;
    private String title;
    private String artist;
    private String album;
    private String genre;
    private int duration;
    private long fileSize;
    private String format;
    private int year;
    private boolean isCorrupted;
    
    public MediaFile(String filePath) {
        this.filePath = filePath;
        this.isCorrupted = false;
        extractMetadata();
        calculateFileSize();
    }
    
    /**
     * Extract metadata from filename - simplified implementation
     * In real system, would use JAudioTagger library
     */
    private void extractMetadata() {
        File file = new File(filePath);
        String fileName = file.getName();
        
        // Extract format
        int dotIndex = fileName.lastIndexOf('.');
        if (dotIndex > 0) {
            format = fileName.substring(dotIndex + 1).toLowerCase();
            fileName = fileName.substring(0, dotIndex);
        } else {
            format = "unknown";
        }
        
        // Try to parse artist - title format
        int dashIndex = fileName.indexOf('-');
        if (dashIndex > 0) {
            artist = fileName.substring(0, dashIndex).trim();
            title = fileName.substring(dashIndex + 1).trim();
        } else {
            title = fileName;
            artist = "Unknown Artist";
        }
        
        album = "Unknown Album";
        genre = "Unknown Genre";
        year = 0;
        duration = 0; // Would be set after file analysis
    }
    
    private void calculateFileSize() {
        File file = new File(filePath);
        if (file.exists()) {
            fileSize = file.length();
        } else {
            fileSize = 0;
            isCorrupted = true;
        }
    }
    
    // Getters and Setters
    public String getFilePath() { return filePath; }
    
    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }
    
    public String getArtist() { return artist; }
    public void setArtist(String artist) { this.artist = artist; }
    
    public String getAlbum() { return album; }
    public void setAlbum(String album) { this.album = album; }
    
    public String getGenre() { return genre; }
    public void setGenre(String genre) { this.genre = genre; }
    
    public int getDuration() { return duration; }
    public void setDuration(int duration) { this.duration = duration; }
    
    public long getFileSize() { return fileSize; }
    
    public String getFormat() { return format; }
    
    public int getYear() { return year; }
    public void setYear(int year) { this.year = year; }
    
    public boolean isCorrupted() { return isCorrupted; }
    public void setCorrupted(boolean corrupted) { isCorrupted = corrupted; }
    
    public String getMetadata() {
        return String.format("%s - %s (%s) [%s]", artist, title, album, format.toUpperCase());
    }
    
    public String getFormattedFileSize() {
        if (fileSize < 1024) return fileSize + " B";
        if (fileSize < 1024 * 1024) return String.format("%.1f KB", fileSize / 1024.0);
        return String.format("%.1f MB", fileSize / (1024.0 * 1024.0));
    }
    
    public String getFormattedDuration() {
        int minutes = duration / 60;
        int seconds = duration % 60;
        return String.format("%02d:%02d", minutes, seconds);
    }
    
    @Override
    public String toString() {
        return String.format("%s - %s [%s]", artist, title, getFormattedDuration());
    }
    
    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        MediaFile mediaFile = (MediaFile) obj;
        return filePath.equals(mediaFile.filePath);
    }
    
    @Override
    public int hashCode() {
        return filePath.hashCode();
    }
}