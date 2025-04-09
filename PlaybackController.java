// PlaybackController.java
import java.util.ArrayList;
import java.util.List;

public class PlaybackController {
    private MediaPlayer mediaPlayer;
    private Playlist currentPlaylist;
    private MediaFile currentFile;
    private List<PlaybackListener> listeners;
    private boolean isPlaying;
    
    public PlaybackController() {
        mediaPlayer = new MediaPlayer();
        currentPlaylist = new Playlist();
        listeners = new ArrayList<>();
        isPlaying = false;
    }
    
    public void play() {
        if (currentFile == null && currentPlaylist.size() > 0) {
            currentFile = currentPlaylist.getCurrentFile();
        }
        
        if (currentFile != null) {
            mediaPlayer.loadMedia(currentFile);
            mediaPlayer.play();
            isPlaying = true;
            notifyPlay();
        }
    }
    
    public void loadAndPlay(MediaFile file) {
        currentFile = file;
        mediaPlayer.loadMedia(file);
        mediaPlayer.play();
        isPlaying = true;
        notifyPlay();
    }
    
    public void pause() {
        mediaPlayer.pause();
        isPlaying = false;
        notifyPause();
    }
    
    public void stop() {
        mediaPlayer.stop();
        isPlaying = false;
        notifyStop();
    }
    
    public void next() {
        MediaFile nextFile = currentPlaylist.getNextFile();
        if (nextFile != null) {
            currentFile = nextFile;
            loadAndPlay(currentFile);
        }
    }
    
    public void previous() {
        MediaFile prevFile = currentPlaylist.getPreviousFile();
        if (prevFile != null) {
            currentFile = prevFile;
            loadAndPlay(currentFile);
        }
    }
    
    public void setVolume(float volume) {
        mediaPlayer.setVolume(volume);
    }
    
    public int getCurrentPosition() {
        return mediaPlayer.getCurrentPosition();
    }
    
    public int getDuration() {
        return mediaPlayer.getDuration();
    }
    
    public void addPlaybackListener(PlaybackListener listener) {
        listeners.add(listener);
    }
    
    public boolean isPlaying() {
        return isPlaying;
    }
    
    public MediaFile getCurrentFile() {
        return currentFile;
    }

    public Playlist getPlaylist() {
        return currentPlaylist;
    }
    
    private void notifyPlay() {
        for (PlaybackListener listener : listeners) {
            listener.onPlay();
        }
    }
    
    private void notifyPause() {
        for (PlaybackListener listener : listeners) {
            listener.onPause();
        }
    }
    
    private void notifyStop() {
        for (PlaybackListener listener : listeners) {
            listener.onStop();
        }
    }
    
    private void notifyComplete() {
        for (PlaybackListener listener : listeners) {
            listener.onComplete();
        }
    }
    
    private void notifyProgress(int position, int duration) {
        for (PlaybackListener listener : listeners) {
            listener.onProgress(position, duration);
        }
    }
}