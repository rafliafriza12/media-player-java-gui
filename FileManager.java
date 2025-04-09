import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class FileManager {
private final String[] SUPPORTED_FORMATS = {"mp3", "wav", "aiff", "au", "mp4", "flac", "ogg"};
    
    public List<MediaFile> scanDirectory(String path) {
        List<MediaFile> result = new ArrayList<>();
        File directory = new File(path);
        
        if (directory.exists() && directory.isDirectory()) {
            File[] files = directory.listFiles();
            if (files != null) {
                for (File file : files) {
                    if (file.isFile() && isSupportedFormat(file.getName())) {
                        result.add(new MediaFile(file.getAbsolutePath()));
                    }
                }
            }
        }
        
        return result;
    }
    
    public MediaFile openFile(String path) {
        return new MediaFile(path);
    }
    
    public boolean isSupportedFormat(String filename) {
        // Dengan JLayer, kita hanya mendukung MP3
        return filename.toLowerCase().endsWith(".mp3");
    }
}