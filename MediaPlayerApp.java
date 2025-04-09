import javax.swing.SwingUtilities;

public class MediaPlayerApp {
    private PlayerUI ui;
    private PlaybackController controller;
    
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            MediaPlayerApp app = new MediaPlayerApp();
            app.initializeUI();
        });
    }
    
    public void initializeUI() {
        controller = new PlaybackController();
        ui = new PlayerUI(controller);
        ui.initComponents();
        ui.setVisible(true);
    }
}