import javax.swing.SwingUtilities;
import javax.swing.UIManager;

/**
 * Main Application Class
 * Entry point for Radio Labi-Labi Media Player
 * Implements component assembly and dependency injection
 */
public class MediaPlayerApp {
    
    private IUserInterface userInterface;
    private IPlaybackController playbackController;
    private IFileManager fileManager;
    
    public static void main(String[] args) {
        // Set system look and feel - Compatible version
        try {
            // Use system property to get system look and feel
            String systemLF = System.getProperty("swing.systemlaf");
            if (systemLF == null) {
                // Default system look and feel names for different platforms
                String os = System.getProperty("os.name").toLowerCase();
                if (os.contains("windows")) {
                    systemLF = "com.sun.java.swing.plaf.windows.WindowsLookAndFeel";
                } else if (os.contains("mac")) {
                    systemLF = "com.apple.laf.AquaLookAndFeel";
                } else {
                    // Linux/Unix - use Metal as fallback
                    systemLF = "javax.swing.plaf.metal.MetalLookAndFeel";
                }
            }
            
            UIManager.setLookAndFeel(systemLF);
            System.out.println("Using look and feel: " + systemLF);
            
        } catch (Exception e) {
            System.err.println("Could not set system look and feel: " + e.getMessage());
            // Use default Metal look and feel
            try {
                UIManager.setLookAndFeel("javax.swing.plaf.metal.MetalLookAndFeel");
            } catch (Exception ex) {
                System.err.println("Could not set Metal look and feel: " + ex.getMessage());
                // Continue with default look and feel
            }
        }
        
        // Create and start application
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                try {
                    MediaPlayerApp app = new MediaPlayerApp();
                    app.initialize();
                    app.start();
                } catch (Exception e) {
                    System.err.println("Error starting application: " + e.getMessage());
                    e.printStackTrace();
                }
            }
        });
    }
    
    /**
     * Initialize application components
     * Demonstrates dependency injection and component assembly
     */
    public void initialize() {
        System.out.println("========================================");
        System.out.println("  Radio Labi-Labi Media Player v2.0");
        System.out.println("  Component-Based Software Engineering");
        System.out.println("========================================");
        System.out.println("Initializing components...");
        
        // Create core business components
        System.out.println("Creating FileManager component...");
        fileManager = new FileManager();
        
        // Create playback controller with its dependencies
        System.out.println("Creating AudioPlayer component...");
        IAudioPlayer audioPlayer = new AudioPlayer();
        
        System.out.println("Creating PlaylistManager component...");
        IPlaylistManager playlistManager = new PlaylistManager();
        
        System.out.println("Creating PlaybackController component...");
        playbackController = new PlaybackController(audioPlayer, playlistManager);
        
        // Create user interface with its dependencies
        System.out.println("Creating UserInterface component...");
        userInterface = new PlayerUI(playbackController, fileManager);
        
        System.out.println("========================================");
        System.out.println("Components initialized successfully!");
        System.out.println("Architecture: Component-Based Design");
        System.out.println("Interfaces: 7 specialized interfaces");
        System.out.println("Patterns: Observer, Dependency Injection");
        
        // Display supported formats
        System.out.println("Supported formats: " + 
            String.join(", ", fileManager.getSupportedFormats()));
        System.out.println("========================================");
    }
    
    /**
     * Start the application
     */
    public void start() {
        System.out.println("Starting Radio Labi-Labi Media Player...");
        userInterface.show();
        System.out.println("Application GUI launched successfully!");
        System.out.println("Ready for component-based media playback.");
    }
    
    /**
     * Get the playback controller instance
     * @return playback controller
     */
    public IPlaybackController getPlaybackController() {
        return playbackController;
    }
    
    /**
     * Get the file manager instance
     * @return file manager
     */
    public IFileManager getFileManager() {
        return fileManager;
    }
    
    /**
     * Get the user interface instance
     * @return user interface
     */
    public IUserInterface getUserInterface() {
        return userInterface;
    }
    
    /**
     * Shutdown application gracefully
     */
    public void shutdown() {
        System.out.println("Shutting down Radio Labi-Labi Media Player...");
        
        // Stop playback if active
        if (playbackController != null && playbackController.isPlaying()) {
            playbackController.stop();
        }
        
        // Dispose UI resources
        if (userInterface != null) {
            userInterface.hide();
        }
        
        System.out.println("Application shutdown complete.");
    }
}