import java.util.ArrayList;
import java.util.List;

public class PlaybackController {
    private MediaPlayer mediaPlayer;
    private Playlist currentPlaylist;
    private MediaFile currentFile;
    private List<PlaybackListener> listeners;
    private boolean isPlaying;
    private boolean autoPlay;
    
    public PlaybackController() {
        mediaPlayer = new MediaPlayer();
        currentPlaylist = new Playlist();
        listeners = new ArrayList<>();
        isPlaying = false;
        autoPlay = true;
        
        // Listen to media player events
        mediaPlayer.addListener(new MediaPlayerEventListener() {
            @Override
            public void onFileLoaded(MediaFile file) {
                currentFile = file;
            }
            
            @Override
            public void onPlaybackStarted() {
                isPlaying = true;
                notifyPlay();
            }
            
            @Override
            public void onPlaybackPaused() {
                isPlaying = false;
                notifyPause();
            }
            
            @Override
            public void onPlaybackStopped() {
                isPlaying = false;
                notifyStop();
            }
            
            @Override
            public void onPlaybackCompleted() {
                isPlaying = false;
                notifyComplete();
                if (autoPlay) {
                    next();
                }
            }
            
            @Override
            public void onVolumeChanged(float volume) {}
            
            @Override
            public void onPositionChanged(int position, int duration) {
                notifyProgress(position, duration);
            }
            
            @Override
            public void onError(String error) {}
        });
    }
    
    public void play() {
        if (currentFile == null && currentPlaylist.size() > 0) {
            currentFile = currentPlaylist.getCurrentFile();
        }
        
        if (currentFile != null) {
            mediaPlayer.loadMedia(currentFile);
            mediaPlayer.play();
        }
    }
    
    public void loadAndPlay(MediaFile file) {
        currentFile = file;
        mediaPlayer.loadMedia(file);
        mediaPlayer.play();
    }
    
    public void pause() {
        mediaPlayer.pause();
    }
    
    public void stop() {
        mediaPlayer.stop();
    }
    
    public void next() {
        MediaFile nextFile = currentPlaylist.getNextFile();
        if (nextFile != null) {
            currentFile = nextFile;
            if (autoPlay && isPlaying) {
                loadAndPlay(currentFile);
            } else {
                mediaPlayer.loadMedia(currentFile);
            }
        }
    }
    
    public void previous() {
        MediaFile prevFile = currentPlaylist.getPreviousFile();
        if (prevFile != null) {
            currentFile = prevFile;
            if (autoPlay && isPlaying) {
                loadAndPlay(currentFile);
            } else {
                mediaPlayer.loadMedia(currentFile);
            }
        }
    }
    
    public void setVolume(float volume) {
        mediaPlayer.setVolume(volume);
    }
    
    public float getVolume() {
        return mediaPlayer.getVolume();
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
    
    public void removePlaybackListener(PlaybackListener listener) {
        listeners.remove(listener);
    }
    
    public boolean isPlaying() {
        return mediaPlayer.isPlaying();
    }
    
    public MediaFile getCurrentFile() {
        return currentFile;
    }

    public Playlist getPlaylist() {
        return currentPlaylist;
    }
    
    public MediaPlayer getMediaPlayer() {
        return mediaPlayer;
    }
    
    public void setAutoPlay(boolean autoPlay) {
        this.autoPlay = autoPlay;
    }
    
    public boolean isAutoPlay() {
        return autoPlay;
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
