public interface PlaybackListener {
    void onPlay();
    void onPause();
    void onStop();
    void onComplete();
    void onProgress(int position, int duration);
}