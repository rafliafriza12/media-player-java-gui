import javax.swing.SwingUtilities;
import javax.swing.UIManager;

public class MediaPlayerApp {
    private PlayerUI ui;
    private PlaybackController controller;
    
    public static void main(String[] args) {
        // Set system look and feel
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeel());
        } catch (Exception e) {
            e.printStackTrace();
        }
        
        SwingUtilities.invokeLater(() -> {
            MediaPlayerApp app = new MediaPlayerApp();
            app.initializeApplication();
        });
    }
    
    public void initializeApplication() {
        controller = new PlaybackController();
        ui = new PlayerUI(controller);
        ui.initComponents();
        ui.setVisible(true);
        
        // Show welcome message
        showWelcomeMessage();
    }
    
    private void showWelcomeMessage() {
        javax.swing.JOptionPane.showMessageDialog(null,
                "Welcome to Java Media Player Pro!\n\n" +
                "To get started:\n" +
                "1. Use File > Open File to add music\n" +
                "2. Or use File > Open Directory to add a folder\n" +
                "3. Double-click songs in the playlist to play\n" +
                "4. Use View > Show Equalizer for audio controls\n\n" +
                "Enjoy your music!",
                "Welcome", javax.swing.JOptionPane.INFORMATION_MESSAGE);
    }
}
