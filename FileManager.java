import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * File Manager Component Implementation
 * Implements IFileManager interface with concrete file operations
 */
public class FileManager implements IFileManager {
    private final String[] SUPPORTED_FORMATS = {"mp3", "wav", "aiff", "au", "mp4", "flac", "ogg"};
    
    @Override
    public List<MediaFile> scanDirectory(String path) {
        // Precondition check
        if (path == null || path.trim().isEmpty()) {
            throw new IllegalArgumentException("Directory path cannot be null or empty");
        }
        
        List<MediaFile> result = new ArrayList<>();
        File directory = new File(path);
        
        if (directory.exists() && directory.isDirectory()) {
            File[] files = directory.listFiles();
            if (files != null) {
                for (File file : files) {
                    if (file.isFile() && isSupportedFormat(file.getName())) {
                        try {
                            MediaFile mediaFile = new MediaFile(file.getAbsolutePath());
                            if (!mediaFile.isCorrupted()) {
                                result.add(mediaFile);
                            }
                        } catch (Exception e) {
                            System.err.println("Error processing file: " + file.getName() + " - " + e.getMessage());
                        }
                    }
                }
            }
        }
        
        return result;
    }
    
    @Override
    public MediaFile openFile(String path) {
        // Precondition check
        if (path == null || path.trim().isEmpty()) {
            throw new IllegalArgumentException("File path cannot be null or empty");
        }
        
        File file = new File(path);
        if (!file.exists() || !file.isFile()) {
            return null;
        }
        
        if (!isSupportedFormat(file.getName())) {
            return null;
        }
        
        try {
            return new MediaFile(path);
        } catch (Exception e) {
            System.err.println("Error opening file: " + path + " - " + e.getMessage());
            return null;
        }
    }
    
    @Override
    public boolean isSupportedFormat(String filename) {
        // Precondition check
        if (filename == null) {
            return false;
        }
        
        String lowerFilename = filename.toLowerCase();
        for (String format : SUPPORTED_FORMATS) {
            if (lowerFilename.endsWith("." + format)) {
                return true;
            }
        }
        return false;
    }
    
    @Override
    public String[] getSupportedFormats() {
        return SUPPORTED_FORMATS.clone();
    }
    
    @Override
    public boolean validateFile(String filePath) {
        // Precondition check
        if (filePath == null || filePath.trim().isEmpty()) {
            return false;
        }
        
        File file = new File(filePath);
        return file.exists() && file.isFile() && file.canRead() && file.length() > 0;
    }
    
    @Override
    public FileMetadata getQuickMetadata(String filePath) {
        // Precondition check
        if (filePath == null || filePath.trim().isEmpty()) {
            throw new IllegalArgumentException("File path cannot be null or empty");
        }
        
        File file = new File(filePath);
        String fileName = file.getName();
        String format = "";
        
        int dotIndex = fileName.lastIndexOf('.');
        if (dotIndex > 0 && dotIndex < fileName.length() - 1) {
            format = fileName.substring(dotIndex + 1).toLowerCase();
        }
        
        return new FileMetadata(
            fileName,
            format,
            file.exists() ? file.length() : 0,
            file.exists() ? file.lastModified() : 0,
            file.exists(),
            file.exists() && file.canRead()
        );
    }
}