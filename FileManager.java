import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class FileManager {
    private final String[] SUPPORTED_FORMATS = {"mp3", "wav", "aiff", "au", "mp4", "flac", "ogg"};
    private List<MediaLibraryListener> listeners;
    
    public FileManager() {
        listeners = new ArrayList<>();
    }
    
    public void addMediaLibraryListener(MediaLibraryListener listener) {
        listeners.add(listener);
    }
    
    public void removeMediaLibraryListener(MediaLibraryListener listener) {
        listeners.remove(listener);
    }
    
    public List<MediaFile> scanDirectory(String path) {
        return scanDirectory(path, false);
    }
    
    public List<MediaFile> scanDirectory(String path, boolean recursive) {
        List<MediaFile> result = new ArrayList<>();
        File directory = new File(path);
        
        if (directory.exists() && directory.isDirectory()) {
            scanDirectoryRecursive(directory, result, recursive, 0);
        }
        
        notifyLibraryScanned(result.size());
        notifyScanCompleted();
        return result;
    }
    
    private void scanDirectoryRecursive(File directory, List<MediaFile> result, 
                                       boolean recursive, int depth) {
        try {
            File[] files = directory.listFiles();
            if (files != null) {
                // Sort files for consistent ordering
                Arrays.sort(files, (f1, f2) -> f1.getName().compareToIgnoreCase(f2.getName()));
                
                for (File file : files) {
                    if (file.isFile() && isSupportedFormat(file.getName())) {
                        MediaFile mediaFile = new MediaFile(file.getAbsolutePath());
                        result.add(mediaFile);
                        notifyFileFound(mediaFile);
                    } else if (file.isDirectory() && recursive && depth < 10) {
                        // Limit recursion depth to prevent infinite loops
                        scanDirectoryRecursive(file, result, recursive, depth + 1);
                    }
                }
            }
        } catch (Exception e) {
            notifyScanError("Error scanning directory: " + e.getMessage());
        }
    }
    
    public MediaFile openFile(String path) {
        if (isSupportedFormat(path)) {
            return new MediaFile(path);
        }
        return null;
    }
    
    public boolean isSupportedFormat(String filename) {
        if (filename == null || filename.isEmpty()) {
            return false;
        }
        
        String extension = getFileExtension(filename).toLowerCase();
        for (String format : SUPPORTED_FORMATS) {
            if (format.equals(extension)) {
                return true;
            }
        }
        return false;
    }
    
    public String[] getSupportedFormats() {
        return SUPPORTED_FORMATS.clone();
    }
    
    private String getFileExtension(String filename) {
        int dotIndex = filename.lastIndexOf('.');
        if (dotIndex > 0 && dotIndex < filename.length() - 1) {
            return filename.substring(dotIndex + 1);
        }
        return "";
    }
    
    public long getFileSize(String filePath) {
        File file = new File(filePath);
        return file.length();
    }
    
    public String getFileSizeFormatted(String filePath) {
        long size = getFileSize(filePath);
        if (size < 1024) return size + " B";
        if (size < 1024 * 1024) return String.format("%.1f KB", size / 1024.0);
        if (size < 1024 * 1024 * 1024) return String.format("%.1f MB", size / (1024.0 * 1024.0));
        return String.format("%.1f GB", size / (1024.0 * 1024.0 * 1024.0));
    }
    
    private void notifyLibraryScanned(int fileCount) {
        for (MediaLibraryListener listener : listeners) {
            listener.onLibraryScanned(fileCount);
        }
    }
    
    private void notifyFileFound(MediaFile file) {
        for (MediaLibraryListener listener : listeners) {
            listener.onFileFound(file);
        }
    }
    
    private void notifyScanCompleted() {
        for (MediaLibraryListener listener : listeners) {
            listener.onScanCompleted();
        }
    }
    
    private void notifyScanError(String error) {
        for (MediaLibraryListener listener : listeners) {
            listener.onScanError(error);
        }
    }
}
