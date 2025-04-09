import java.io.*;
import java.util.ArrayList;
import java.util.List;

public class Playlist {
    private List<MediaFile> mediaFiles;
    private int currentIndex;
    
    public Playlist() {
        mediaFiles = new ArrayList<>();
        currentIndex = 0;
    }
    
    public void addFile(MediaFile file) {
        mediaFiles.add(file);
    }
    
    public void removeFile(MediaFile file) {
        int index = mediaFiles.indexOf(file);
        if (index != -1) {
            mediaFiles.remove(index);
            if (index < currentIndex) {
                currentIndex--;
            }
            if (currentIndex >= mediaFiles.size()) {
                currentIndex = mediaFiles.size() - 1;
            }
        }
    }
    
    public MediaFile getNextFile() {
        if (mediaFiles.isEmpty()) {
            return null;
        }
        
        currentIndex = (currentIndex + 1) % mediaFiles.size();
        return mediaFiles.get(currentIndex);
    }
    
    public MediaFile getPreviousFile() {
        if (mediaFiles.isEmpty()) {
            return null;
        }
        
        currentIndex = (currentIndex - 1 + mediaFiles.size()) % mediaFiles.size();
        return mediaFiles.get(currentIndex);
    }
    
    public MediaFile getCurrentFile() {
        if (mediaFiles.isEmpty()) {
            return null;
        }
        return mediaFiles.get(currentIndex);
    }
    
    public MediaFile getFileAt(int index) {
        if (index >= 0 && index < mediaFiles.size()) {
            return mediaFiles.get(index);
        }
        return null;
    }
    
    public void moveUp(MediaFile file) {
        int index = mediaFiles.indexOf(file);
        if (index > 0) {
            MediaFile temp = mediaFiles.get(index - 1);
            mediaFiles.set(index - 1, file);
            mediaFiles.set(index, temp);
            
            if (index == currentIndex) {
                currentIndex--;
            } else if (index - 1 == currentIndex) {
                currentIndex++;
            }
        }
    }
    
    public void moveDown(MediaFile file) {
        int index = mediaFiles.indexOf(file);
        if (index != -1 && index < mediaFiles.size() - 1) {
            MediaFile temp = mediaFiles.get(index + 1);
            mediaFiles.set(index + 1, file);
            mediaFiles.set(index, temp);
            
            if (index == currentIndex) {
                currentIndex++;
            } else if (index + 1 == currentIndex) {
                currentIndex--;
            }
        }
    }
    
    public void save(String filename) {
        try (ObjectOutputStream out = new ObjectOutputStream(new FileOutputStream(filename))) {
            out.writeObject(mediaFiles);
        } catch (IOException e) {
            System.err.println("Error saving playlist: " + e.getMessage());
        }
    }
    
    @SuppressWarnings("unchecked")
    public void load(String filename) {
        try (ObjectInputStream in = new ObjectInputStream(new FileInputStream(filename))) {
            mediaFiles = (List<MediaFile>) in.readObject();
            currentIndex = 0;
        } catch (IOException | ClassNotFoundException e) {
            System.err.println("Error loading playlist: " + e.getMessage());
        }
    }
    
    public int size() {
        return mediaFiles.size();
    }
}
