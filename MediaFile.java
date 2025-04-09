import java.io.Serializable;
import java.io.File;
public class MediaFile implements Serializable {
    private static final long serialVersionUID = 1L;
    
    private String filePath;
    private String title;
    private String artist;
    private String album;
    private int duration;
    
    public MediaFile(String filePath) {
        this.filePath = filePath;
        extractMetadata();
    }
    
    private void extractMetadata() {
        // In a real implementation, we would use a library like JAudioTagger
        // to extract metadata from the file. Here we'll just use the filename.
        File file = new File(filePath);
        String fileName = file.getName();
        
        // Remove extension
        int dotIndex = fileName.lastIndexOf('.');
        if (dotIndex > 0) {
            fileName = fileName.substring(0, dotIndex);
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
        duration = 0; // Would be set after file is loaded
    }
    
    public String getFilePath() {
        return filePath;
    }
    
    public String getTitle() {
        return title;
    }
    
    public void setTitle(String title) {
        this.title = title;
    }
    
    public String getArtist() {
        return artist;
    }
    
    public void setArtist(String artist) {
        this.artist = artist;
    }
    
    public String getAlbum() {
        return album;
    }
    
    public void setAlbum(String album) {
        this.album = album;
    }
    
    public int getDuration() {
        return duration;
    }
    
    public void setDuration(int duration) {
        this.duration = duration;
    }
    
    public String getMetadata() {
        return artist + " - " + title + " (" + album + ")";
    }
    
    public String getFileType() {
        int dotIndex = filePath.lastIndexOf('.');
        if (dotIndex > 0 && dotIndex < filePath.length() - 1) {
            return filePath.substring(dotIndex + 1).toLowerCase();
        }
        return "";
    }
    
    @Override
    public String toString() {
        return artist + " - " + title;
    }
}