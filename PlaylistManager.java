import java.io.*;
import java.util.*;

/**
 * Playlist Manager Component Implementation
 * Implements IPlaylistManager interface with concrete playlist operations
 */
public class PlaylistManager implements IPlaylistManager {
    private List<MediaFile> mediaFiles;
    private int currentIndex;
    private Random random;
    
    public PlaylistManager() {
        mediaFiles = new ArrayList<>();
        currentIndex = -1;
        random = new Random();
    }
    
    @Override
    public void addFile(MediaFile file) {
        // Precondition check
        if (file == null) {
            throw new IllegalArgumentException("MediaFile cannot be null");
        }
        
        // Avoid duplicates
        if (!mediaFiles.contains(file)) {
            mediaFiles.add(file);
            // Set as current if it's the first file
            if (mediaFiles.size() == 1) {
                currentIndex = 0;
            }
        }
    }
    
    @Override
    public boolean removeFile(MediaFile file) {
        if (file == null) {
            return false;
        }
        
        int index = mediaFiles.indexOf(file);
        if (index == -1) {
            return false;
        }
        
        mediaFiles.remove(index);
        
        // Adjust current index
        if (index < currentIndex) {
            currentIndex--;
        } else if (index == currentIndex) {
            if (currentIndex >= mediaFiles.size()) {
                currentIndex = mediaFiles.size() - 1;
            }
        }
        
        // Reset index if playlist becomes empty
        if (mediaFiles.isEmpty()) {
            currentIndex = -1;
        }
        
        return true;
    }
    
    @Override
    public MediaFile getNextFile() {
        if (mediaFiles.isEmpty()) {
            return null;
        }
        
        currentIndex = (currentIndex + 1) % mediaFiles.size();
        return mediaFiles.get(currentIndex);
    }
    
    @Override
    public MediaFile getPreviousFile() {
        if (mediaFiles.isEmpty()) {
            return null;
        }
        
        currentIndex = (currentIndex - 1 + mediaFiles.size()) % mediaFiles.size();
        return mediaFiles.get(currentIndex);
    }
    
    @Override
    public MediaFile getCurrentFile() {
        if (mediaFiles.isEmpty() || currentIndex < 0 || currentIndex >= mediaFiles.size()) {
            return null;
        }
        return mediaFiles.get(currentIndex);
    }
    
    @Override
    public MediaFile getFileAt(int index) {
        // Precondition check
        if (index < 0 || index >= mediaFiles.size()) {
            throw new IndexOutOfBoundsException("Index out of bounds: " + index);
        }
        return mediaFiles.get(index);
    }
    
    @Override
    public boolean moveUp(MediaFile file) {
        if (file == null) {
            return false;
        }
        
        int index = mediaFiles.indexOf(file);
        if (index <= 0) {
            return false; // Can't move up first item or non-existent item
        }
        
        Collections.swap(mediaFiles, index, index - 1);
        
        // Adjust current index if needed
        if (index == currentIndex) {
            currentIndex--;
        } else if (index - 1 == currentIndex) {
            currentIndex++;
        }
        
        return true;
    }
    
    @Override
    public boolean moveDown(MediaFile file) {
        if (file == null) {
            return false;
        }
        
        int index = mediaFiles.indexOf(file);
        if (index == -1 || index >= mediaFiles.size() - 1) {
            return false; // Can't move down last item or non-existent item
        }
        
        Collections.swap(mediaFiles, index, index + 1);
        
        // Adjust current index if needed
        if (index == currentIndex) {
            currentIndex++;
        } else if (index + 1 == currentIndex) {
            currentIndex--;
        }
        
        return true;
    }
    
    @Override
    public void clear() {
        mediaFiles.clear();
        currentIndex = -1;
    }
    
    @Override
    public int size() {
        return mediaFiles.size();
    }
    
    @Override
    public boolean isEmpty() {
        return mediaFiles.isEmpty();
    }
    
    @Override
    public List<MediaFile> getAllFiles() {
        return new ArrayList<>(mediaFiles); // Return defensive copy
    }
    
    @Override
    public void setCurrentIndex(int index) {
        // Precondition check
        if (index < -1 || index >= mediaFiles.size()) {
            throw new IndexOutOfBoundsException("Invalid index: " + index);
        }
        this.currentIndex = index;
    }
    
    @Override
    public int getCurrentIndex() {
        return currentIndex;
    }
    
    @Override
    public void shuffle() {
        if (mediaFiles.size() <= 1) {
            return;
        }
        
        MediaFile currentFile = getCurrentFile();
        Collections.shuffle(mediaFiles, random);
        
        // Update current index to maintain current file if possible
        if (currentFile != null) {
            currentIndex = mediaFiles.indexOf(currentFile);
        }
    }
    
    @Override
    public boolean save(String filename) {
        // Precondition check
        if (filename == null || filename.trim().isEmpty()) {
            throw new IllegalArgumentException("Filename cannot be null or empty");
        }
        
        try (ObjectOutputStream out = new ObjectOutputStream(new FileOutputStream(filename))) {
            out.writeObject(mediaFiles);
            out.writeInt(currentIndex);
            return true;
        } catch (IOException e) {
            System.err.println("Error saving playlist: " + e.getMessage());
            return false;
        }
    }
    
    @Override
    @SuppressWarnings("unchecked")
    public boolean load(String filename) {
        // Precondition check
        if (filename == null || filename.trim().isEmpty()) {
            throw new IllegalArgumentException("Filename cannot be null or empty");
        }
        
        try (ObjectInputStream in = new ObjectInputStream(new FileInputStream(filename))) {
            mediaFiles = (List<MediaFile>) in.readObject();
            currentIndex = in.readInt();
            
            // Validate loaded data
            if (currentIndex >= mediaFiles.size()) {
                currentIndex = mediaFiles.size() - 1;
            }
            if (mediaFiles.isEmpty()) {
                currentIndex = -1;
            }
            
            return true;
        } catch (IOException | ClassNotFoundException e) {
            System.err.println("Error loading playlist: " + e.getMessage());
            return false;
        }
    }
    
    /**
     * Get total duration of all files in playlist
     * @return total duration in seconds
     */
    public int getTotalDuration() {
        return mediaFiles.stream().mapToInt(MediaFile::getDuration).sum();
    }
    
    /**
     * Get formatted total duration
     * @return formatted duration string
     */
    public String getFormattedTotalDuration() {
        int totalSeconds = getTotalDuration();
        int hours = totalSeconds / 3600;
        int minutes = (totalSeconds % 3600) / 60;
        int seconds = totalSeconds % 60;
        
        if (hours > 0) {
            return String.format("%d:%02d:%02d", hours, minutes, seconds);
        } else {
            return String.format("%02d:%02d", minutes, seconds);
        }
    }
    
    /**
     * Find files by artist
     * @param artist artist name to search for
     * @return list of matching files
     */
    public List<MediaFile> findByArtist(String artist) {
        if (artist == null || artist.trim().isEmpty()) {
            return new ArrayList<>();
        }
        
        List<MediaFile> result = new ArrayList<>();
        String searchArtist = artist.toLowerCase().trim();
        
        for (MediaFile file : mediaFiles) {
            if (file.getArtist().toLowerCase().contains(searchArtist)) {
                result.add(file);
            }
        }
        
        return result;
    }
    
    /**
     * Find files by title
     * @param title title to search for
     * @return list of matching files
     */
    public List<MediaFile> findByTitle(String title) {
        if (title == null || title.trim().isEmpty()) {
            return new ArrayList<>();
        }
        
        List<MediaFile> result = new ArrayList<>();
        String searchTitle = title.toLowerCase().trim();
        
        for (MediaFile file : mediaFiles) {
            if (file.getTitle().toLowerCase().contains(searchTitle)) {
                result.add(file);
            }
        }
        
        return result;
    }

}