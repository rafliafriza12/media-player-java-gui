import java.io.*;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class Playlist {
    private List<MediaFile> mediaFiles;
    private int currentIndex;
    private List<PlaylistEventListener> listeners;
    private boolean shuffleMode;
    private boolean repeatMode;
    private List<Integer> shuffleOrder;
    
    public Playlist() {
        mediaFiles = new ArrayList<>();
        listeners = new ArrayList<>();
        currentIndex = 0;
        shuffleMode = false;
        repeatMode = false;
        shuffleOrder = new ArrayList<>();
    }
    
    public void addPlaylistEventListener(PlaylistEventListener listener) {
        listeners.add(listener);
    }
    
    public void removePlaylistEventListener(PlaylistEventListener listener) {
        listeners.remove(listener);
    }
    
    public void addFile(MediaFile file) {
        mediaFiles.add(file);
        updateShuffleOrder();
        for (PlaylistEventListener listener : listeners) {
            listener.onFileAdded(file);
        }
    }
    
    public void addFiles(List<MediaFile> files) {
        mediaFiles.addAll(files);
        updateShuffleOrder();
        for (MediaFile file : files) {
            for (PlaylistEventListener listener : listeners) {
                listener.onFileAdded(file);
            }
        }
    }
    
    public void removeFile(MediaFile file) {
        int index = mediaFiles.indexOf(file);
        if (index != -1) {
            mediaFiles.remove(index);
            updateShuffleOrder();
            if (index < currentIndex) {
                currentIndex--;
            }
            if (currentIndex >= mediaFiles.size()) {
                currentIndex = mediaFiles.size() - 1;
            }
            for (PlaylistEventListener listener : listeners) {
                listener.onFileRemoved(file);
            }
        }
    }
    
    public void clear() {
        mediaFiles.clear();
        shuffleOrder.clear();
        currentIndex = 0;
        for (PlaylistEventListener listener : listeners) {
            listener.onPlaylistCleared();
        }
    }
    
    public MediaFile getNextFile() {
        if (mediaFiles.isEmpty()) {
            return null;
        }
        
        if (shuffleMode) {
            currentIndex = (currentIndex + 1) % shuffleOrder.size();
            int actualIndex = shuffleOrder.get(currentIndex);
            MediaFile file = mediaFiles.get(actualIndex);
            notifyCurrentFileChanged(file);
            return file;
        } else {
            currentIndex = (currentIndex + 1) % mediaFiles.size();
            MediaFile file = mediaFiles.get(currentIndex);
            notifyCurrentFileChanged(file);
            return file;
        }
    }
    
    public MediaFile getPreviousFile() {
        if (mediaFiles.isEmpty()) {
            return null;
        }
        
        if (shuffleMode) {
            currentIndex = (currentIndex - 1 + shuffleOrder.size()) % shuffleOrder.size();
            int actualIndex = shuffleOrder.get(currentIndex);
            MediaFile file = mediaFiles.get(actualIndex);
            notifyCurrentFileChanged(file);
            return file;
        } else {
            currentIndex = (currentIndex - 1 + mediaFiles.size()) % mediaFiles.size();
            MediaFile file = mediaFiles.get(currentIndex);
            notifyCurrentFileChanged(file);
            return file;
        }
    }
    
    public MediaFile getCurrentFile() {
        if (mediaFiles.isEmpty()) {
            return null;
        }
        
        if (shuffleMode && !shuffleOrder.isEmpty()) {
            int actualIndex = shuffleOrder.get(currentIndex);
            return mediaFiles.get(actualIndex);
        }
        
        return mediaFiles.get(currentIndex);
    }
    
    public void setCurrentIndex(int index) {
        if (index >= 0 && index < mediaFiles.size()) {
            currentIndex = index;
            notifyCurrentFileChanged(getCurrentFile());
        }
    }
    
    public void setShuffle(boolean shuffle) {
        this.shuffleMode = shuffle;
        if (shuffle) {
            updateShuffleOrder();
        }
    }
    
    public boolean isShuffle() {
        return shuffleMode;
    }
    
    public void setRepeat(boolean repeat) {
        this.repeatMode = repeat;
    }
    
    public boolean isRepeat() {
        return repeatMode;
    }
    
    private void updateShuffleOrder() {
        shuffleOrder.clear();
        for (int i = 0; i < mediaFiles.size(); i++) {
            shuffleOrder.add(i);
        }
        Collections.shuffle(shuffleOrder);
    }
    
    private void notifyCurrentFileChanged(MediaFile file) {
        for (PlaylistEventListener listener : listeners) {
            listener.onCurrentFileChanged(file);
        }
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
            Collections.swap(mediaFiles, index, index - 1);
            updateShuffleOrder();
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
            Collections.swap(mediaFiles, index, index + 1);
            updateShuffleOrder();
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
            for (PlaylistEventListener listener : listeners) {
                listener.onPlaylistSaved(filename);
            }
        } catch (IOException e) {
            System.err.println("Error saving playlist: " + e.getMessage());
        }
    }
    
    @SuppressWarnings("unchecked")
    public void load(String filename) {
        try (ObjectInputStream in = new ObjectInputStream(new FileInputStream(filename))) {
            mediaFiles = (List<MediaFile>) in.readObject();
            currentIndex = 0;
            updateShuffleOrder();
            for (PlaylistEventListener listener : listeners) {
                listener.onPlaylistLoaded(filename);
            }
        } catch (IOException | ClassNotFoundException e) {
            System.err.println("Error loading playlist: " + e.getMessage());
        }
    }
    
    public int size() {
        return mediaFiles.size();
    }
    
    public List<MediaFile> getAllFiles() {
        return new ArrayList<>(mediaFiles);
    }
}
