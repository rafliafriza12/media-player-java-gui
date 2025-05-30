import java.util.List;

/**
 * Playlist Manager Interface - Business Interface
 * Provides contract for playlist operations and management
 * Follows Design by Contract principles
 */
public interface IPlaylistManager {
    
    /**
     * Add media file to playlist
     * Precondition: file must not be null
     * Postcondition: file is added to playlist, size increases by 1
     */
    void addFile(MediaFile file);
    
    /**
     * Remove media file from playlist
     * Precondition: file must exist in playlist
     * Postcondition: file is removed, size decreases by 1
     */
    boolean removeFile(MediaFile file);
    
    /**
     * Get next file in playlist
     * Precondition: playlist must not be empty
     * Postcondition: returns next file and advances current index
     */
    MediaFile getNextFile();
    
    /**
     * Get previous file in playlist
     * Precondition: playlist must not be empty
     * Postcondition: returns previous file and moves back current index
     */
    MediaFile getPreviousFile();
    
    /**
     * Get current file without changing position
     * Postcondition: returns current file or null if playlist empty
     */
    MediaFile getCurrentFile();
    
    /**
     * Get file at specific index
     * Precondition: index must be valid (0 <= index < size)
     * Postcondition: returns file at specified index
     */
    MediaFile getFileAt(int index);
    
    /**
     * Move file up in playlist order
     * Precondition: file must exist and not be at top
     * Postcondition: file position moved up by one
     */
    boolean moveUp(MediaFile file);
    
    /**
     * Move file down in playlist order
     * Precondition: file must exist and not be at bottom
     * Postcondition: file position moved down by one
     */
    boolean moveDown(MediaFile file);
    
    /**
     * Clear all files from playlist
     * Postcondition: playlist is empty (size = 0)
     */
    void clear();
    
    /**
     * Get playlist size
     * Postcondition: returns number of files in playlist
     */
    int size();
    
    /**
     * Check if playlist is empty
     * Postcondition: returns true if no files in playlist
     */
    boolean isEmpty();
    
    /**
     * Get all files in playlist
     * Postcondition: returns immutable list of all files
     */
    List<MediaFile> getAllFiles();
    
    /**
     * Set current file index
     * Precondition: index must be valid or -1 for no selection
     * Postcondition: current index is updated
     */
    void setCurrentIndex(int index);
    
    /**
     * Get current file index
     * Postcondition: returns current index or -1 if none selected
     */
    int getCurrentIndex();
    
    /**
     * Shuffle playlist order
     * Postcondition: files are randomly reordered
     */
    void shuffle();
    
    /**
     * Save playlist to file
     * Precondition: filename must not be null or empty
     * Postcondition: playlist is saved to specified file
     */
    boolean save(String filename);
    
    /**
     * Load playlist from file
     * Precondition: filename must be valid playlist file
     * Postcondition: playlist is loaded, replacing current content
     */
    boolean load(String filename);
}