/**
 * File Metadata value object
 * Part of Business Type Model for file information
 */
public class FileMetadata {
    private String fileName;
    private String format;
    private long fileSize;
    private long lastModified;
    private boolean exists;
    private boolean readable;
    
    public FileMetadata(String fileName, String format, long fileSize, 
                       long lastModified, boolean exists, boolean readable) {
        this.fileName = fileName;
        this.format = format;
        this.fileSize = fileSize;
        this.lastModified = lastModified;
        this.exists = exists;
        this.readable = readable;
    }
    
    // Getters
    public String getFileName() { return fileName; }
    public String getFormat() { return format; }
    public long getFileSize() { return fileSize; }
    public long getLastModified() { return lastModified; }
    public boolean exists() { return exists; }
    public boolean isReadable() { return readable; }
    
    public String getFormattedFileSize() {
        if (fileSize < 1024) return fileSize + " B";
        if (fileSize < 1024 * 1024) return String.format("%.1f KB", fileSize / 1024.0);
        return String.format("%.1f MB", fileSize / (1024.0 * 1024.0));
    }
    
    @Override
    public String toString() {
        return String.format("%s [%s, %s]", fileName, format.toUpperCase(), getFormattedFileSize());
    }
}