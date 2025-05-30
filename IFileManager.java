import java.util.List;

/**
 * File Management Interface - System Interface
 * Provides contract for file operations and format validation
 * Preconditions and postconditions follow Design by Contract principles
 */
public interface IFileManager {
    
    /**
     * Scan directory for supported media files
     * Precondition: path must be valid directory path
     * Postcondition: returns list of MediaFile objects for supported formats
     */
    List<MediaFile> scanDirectory(String path);
    
    /**
     * Open single media file
     * Precondition: path must be valid file path
     * Postcondition: returns MediaFile object if format supported, null otherwise
     */
    MediaFile openFile(String path);
    
    /**
     * Check if file format is supported
     * Precondition: filename must not be null
     * Postcondition: returns true if format is supported
     */
    boolean isSupportedFormat(String filename);
    
    /**
     * Get list of supported file formats
     * Postcondition: returns array of supported extensions
     */
    String[] getSupportedFormats();
    
    /**
     * Validate file integrity
     * Precondition: file must exist
     * Postcondition: returns true if file is not corrupted
     */
    boolean validateFile(String filePath);
    
    /**
     * Get file metadata without loading entire file
     * Precondition: file path must be valid
     * Postcondition: returns basic file information
     */
    FileMetadata getQuickMetadata(String filePath);
}