import javax.swing.*;
import javax.swing.filechooser.FileNameExtensionFilter;
import java.awt.*;
import java.awt.event.*;
import java.io.File;
import java.util.List;

/**
 * Player User Interface Component
 * Implements IUserInterface and IPlaybackListener
 * Provides Swing-based GUI for the media player
 */
public class PlayerUI implements IUserInterface, IPlaybackListener {
    
    // Main components
    private JFrame mainFrame;
    private IPlaybackController controller;
    private IFileManager fileManager;
    
    // Control components
    private JButton playButton, pauseButton, stopButton, nextButton, prevButton;
    private JButton repeatButton, shuffleButton;
    private JSlider volumeSlider, progressSlider;
    private JList<MediaFile> playlistView;
    private DefaultListModel<MediaFile> playlistModel;
    
    // Display components
    private JLabel currentTimeLabel, totalTimeLabel, statusLabel;
    private JLabel currentTrackLabel, currentArtistLabel;
    private JLabel playlistInfoLabel;
    
    // Menu components
    private JMenuBar menuBar;
    private JMenu fileMenu, playlistMenu, viewMenu;
    private JMenuItem openFileItem, openDirItem, savePlaylistItem, loadPlaylistItem, exitItem;
    private JMenuItem clearPlaylistItem, shufflePlaylistItem;
    
    // State variables
    private boolean updatingProgress = false;
    
    public PlayerUI(IPlaybackController controller, IFileManager fileManager) {
        this.controller = controller;
        this.fileManager = fileManager;
        this.controller.addPlaybackListener(this);
        
        initializeComponents();
        setupEventHandlers();
        updatePlaylistDisplay();
    }
    
    private void initializeComponents() {
        // Initialize main frame
        mainFrame = new JFrame("Radio Labi-Labi - Media Player");
        mainFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        mainFrame.setSize(900, 700);
        mainFrame.setLayout(new BorderLayout());
        
        // Create menu bar
        createMenuBar();
        
        // Create main panels
        createControlPanel();
        createDisplayPanel();
        createPlaylistPanel();
        createStatusPanel();
        
        // Set initial states
        setControlsEnabled(false);
        updatePlaybackStatus("Ready");
    }
    
    private void createMenuBar() {
        menuBar = new JMenuBar();
        
        // File Menu
        fileMenu = new JMenu("File");
        openFileItem = new JMenuItem("Open File...");
        openDirItem = new JMenuItem("Open Directory...");
        savePlaylistItem = new JMenuItem("Save Playlist...");
        loadPlaylistItem = new JMenuItem("Load Playlist...");
        exitItem = new JMenuItem("Exit");
        
        fileMenu.add(openFileItem);
        fileMenu.add(openDirItem);
        fileMenu.addSeparator();
        fileMenu.add(savePlaylistItem);
        fileMenu.add(loadPlaylistItem);
        fileMenu.addSeparator();
        fileMenu.add(exitItem);
        
        // Playlist Menu
        playlistMenu = new JMenu("Playlist");
        clearPlaylistItem = new JMenuItem("Clear Playlist");
        shufflePlaylistItem = new JMenuItem("Shuffle Playlist");
        
        playlistMenu.add(clearPlaylistItem);
        playlistMenu.add(shufflePlaylistItem);
        
        menuBar.add(fileMenu);
        menuBar.add(playlistMenu);
        
        mainFrame.setJMenuBar(menuBar);
    }
    
    private void createControlPanel() {
        JPanel controlPanel = new JPanel(new BorderLayout());
        
        // Playback controls
        JPanel playbackPanel = new JPanel(new FlowLayout());
        prevButton = new JButton("⏮");
        playButton = new JButton("▶");
        pauseButton = new JButton("⏸");
        stopButton = new JButton("⏹");
        nextButton = new JButton("⏭");
        
        // Mode controls
        repeatButton = new JButton("🔁");
        shuffleButton = new JButton("🔀");
        
        playbackPanel.add(prevButton);
        playbackPanel.add(playButton);
        playbackPanel.add(pauseButton);
        playbackPanel.add(stopButton);
        playbackPanel.add(nextButton);
        playbackPanel.add(Box.createHorizontalStrut(20));
        playbackPanel.add(repeatButton);
        playbackPanel.add(shuffleButton);
        
        // Volume control
        JPanel volumePanel = new JPanel(new FlowLayout());
        volumePanel.add(new JLabel("🔊"));
        volumeSlider = new JSlider(0, 100, 80);
        volumeSlider.setPreferredSize(new Dimension(100, 25));
        volumePanel.add(volumeSlider);
        
        controlPanel.add(playbackPanel, BorderLayout.CENTER);
        controlPanel.add(volumePanel, BorderLayout.EAST);
        
        mainFrame.add(controlPanel, BorderLayout.NORTH);
    }
    
    private void createDisplayPanel() {
        JPanel displayPanel = new JPanel(new BorderLayout());
        
        // Current track info
        JPanel trackInfoPanel = new JPanel(new GridBagLayout());
        trackInfoPanel.setBorder(BorderFactory.createTitledBorder("Now Playing"));
        GridBagConstraints gbc = new GridBagConstraints();
        
        gbc.gridx = 0; gbc.gridy = 0; gbc.anchor = GridBagConstraints.WEST;
        trackInfoPanel.add(new JLabel("Track:"), gbc);
        gbc.gridx = 1; gbc.weightx = 1.0; gbc.fill = GridBagConstraints.HORIZONTAL;
        currentTrackLabel = new JLabel("No track selected");
        trackInfoPanel.add(currentTrackLabel, gbc);
        
        gbc.gridx = 0; gbc.gridy = 1; gbc.weightx = 0;
        trackInfoPanel.add(new JLabel("Artist:"), gbc);
        gbc.gridx = 1; gbc.weightx = 1.0;
        currentArtistLabel = new JLabel("Unknown Artist");
        trackInfoPanel.add(currentArtistLabel, gbc);
        
        // Progress panel
        JPanel progressPanel = new JPanel(new BorderLayout());
        progressSlider = new JSlider(0, 100, 0);
        progressSlider.setEnabled(false);
        
        JPanel timePanel = new JPanel(new BorderLayout());
        currentTimeLabel = new JLabel("00:00");
        totalTimeLabel = new JLabel("00:00");
        timePanel.add(currentTimeLabel, BorderLayout.WEST);
        timePanel.add(totalTimeLabel, BorderLayout.EAST);
        
        progressPanel.add(progressSlider, BorderLayout.CENTER);
        progressPanel.add(timePanel, BorderLayout.SOUTH);
        
        displayPanel.add(trackInfoPanel, BorderLayout.NORTH);
        displayPanel.add(progressPanel, BorderLayout.SOUTH);
        
        mainFrame.add(displayPanel, BorderLayout.CENTER);
    }
    
    private void createPlaylistPanel() {
        JPanel playlistPanel = new JPanel(new BorderLayout());
        playlistPanel.setBorder(BorderFactory.createTitledBorder("Playlist"));
        
        // Playlist info
        playlistInfoLabel = new JLabel("0 tracks, 00:00");
        playlistPanel.add(playlistInfoLabel, BorderLayout.NORTH);
        
        // Playlist view
        playlistModel = new DefaultListModel<>();
        playlistView = new JList<>(playlistModel);
        playlistView.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        playlistView.setCellRenderer(new PlaylistCellRenderer());
        
        JScrollPane scrollPane = new JScrollPane(playlistView);
        scrollPane.setPreferredSize(new Dimension(300, 200));
        playlistPanel.add(scrollPane, BorderLayout.CENTER);
        
        // Playlist controls
        JPanel playlistControlPanel = new JPanel(new FlowLayout());
        JButton moveUpButton = new JButton("↑");
        JButton moveDownButton = new JButton("↓");
        JButton removeButton = new JButton("Remove");
        
        playlistControlPanel.add(moveUpButton);
        playlistControlPanel.add(moveDownButton);
        playlistControlPanel.add(removeButton);
        
        playlistPanel.add(playlistControlPanel, BorderLayout.SOUTH);
        
        mainFrame.add(playlistPanel, BorderLayout.EAST);
        
        // Playlist control event handlers
        moveUpButton.addActionListener(e -> moveSelectedUp());
        moveDownButton.addActionListener(e -> moveSelectedDown());
        removeButton.addActionListener(e -> removeSelected());
    }
    
    private void createStatusPanel() {
        JPanel statusPanel = new JPanel(new BorderLayout());
        statusLabel = new JLabel("Ready");
        statusLabel.setBorder(BorderFactory.createLoweredBevelBorder());
        statusPanel.add(statusLabel, BorderLayout.CENTER);
        
        mainFrame.add(statusPanel, BorderLayout.SOUTH);
    }
    
    private void setupEventHandlers() {
        // Menu event handlers
        openFileItem.addActionListener(e -> openFile());
        openDirItem.addActionListener(e -> openDirectory());
        savePlaylistItem.addActionListener(e -> savePlaylist());
        loadPlaylistItem.addActionListener(e -> loadPlaylist());
        exitItem.addActionListener(e -> exitApplication());
        
        clearPlaylistItem.addActionListener(e -> clearPlaylist());
        shufflePlaylistItem.addActionListener(e -> shufflePlaylist());
        
        // Control event handlers
        playButton.addActionListener(e -> controller.play());
        pauseButton.addActionListener(e -> controller.pause());
        stopButton.addActionListener(e -> controller.stop());
        nextButton.addActionListener(e -> controller.next());
        prevButton.addActionListener(e -> controller.previous());
        
        repeatButton.addActionListener(e -> toggleRepeatMode());
        shuffleButton.addActionListener(e -> toggleShuffleMode());
        
        volumeSlider.addChangeListener(e -> {
            if (!volumeSlider.getValueIsAdjusting()) {
                controller.setVolume(volumeSlider.getValue() / 100f);
            }
        });
        
        progressSlider.addChangeListener(e -> {
            if (!updatingProgress && progressSlider.getValueIsAdjusting()) {
                // Seeking not implemented with JLayer
                // Would need different audio library for this feature
            }
        });
        
        // Playlist event handlers
        playlistView.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (e.getClickCount() == 2) {
                    int index = playlistView.getSelectedIndex();
                    if (index != -1) {
                        MediaFile selectedFile = playlistModel.getElementAt(index);
                        controller.getPlaylistManager().setCurrentIndex(index);
                        controller.loadAndPlay(selectedFile);
                    }
                }
            }
        });
        
        // Window event handlers
        mainFrame.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                exitApplication();
            }
        });
    }
    
    private void openFile() {
        JFileChooser fileChooser = new JFileChooser();
        FileNameExtensionFilter filter = new FileNameExtensionFilter(
                "Audio files", fileManager.getSupportedFormats());
        fileChooser.setFileFilter(filter);
        
        int result = fileChooser.showOpenDialog(mainFrame);
        if (result == JFileChooser.APPROVE_OPTION) {
            File selectedFile = fileChooser.getSelectedFile();
            MediaFile mediaFile = fileManager.openFile(selectedFile.getPath());
            
            if (mediaFile != null) {
                controller.getPlaylistManager().addFile(mediaFile);
                updatePlaylistDisplay();
                controller.loadAndPlay(mediaFile);
            } else {
                showError("Failed to open file: " + selectedFile.getName());
            }
        }
    }
    
    private void openDirectory() {
        JFileChooser dirChooser = new JFileChooser();
        dirChooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
        
        int result = dirChooser.showOpenDialog(mainFrame);
        if (result == JFileChooser.APPROVE_OPTION) {
            File selectedDir = dirChooser.getSelectedFile();
            List<MediaFile> files = fileManager.scanDirectory(selectedDir.getPath());
            
            for (MediaFile file : files) {
                controller.getPlaylistManager().addFile(file);
            }
            updatePlaylistDisplay();
            showInfo("Added " + files.size() + " files to playlist");
        }
    }
    
    private void savePlaylist() {
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setSelectedFile(new File("playlist.rll"));
        
        int result = fileChooser.showSaveDialog(mainFrame);
        if (result == JFileChooser.APPROVE_OPTION) {
            String filename = fileChooser.getSelectedFile().getPath();
            if (controller.getPlaylistManager().save(filename)) {
                showInfo("Playlist saved successfully");
            } else {
                showError("Failed to save playlist");
            }
        }
    }
    
    private void loadPlaylist() {
        JFileChooser fileChooser = new JFileChooser();
        int result = fileChooser.showOpenDialog(mainFrame);
        if (result == JFileChooser.APPROVE_OPTION) {
            String filename = fileChooser.getSelectedFile().getPath();
            if (controller.getPlaylistManager().load(filename)) {
                updatePlaylistDisplay();
                showInfo("Playlist loaded successfully");
            } else {
                showError("Failed to load playlist");
            }
        }
    }
    
    private void clearPlaylist() {
        controller.stop();
        controller.getPlaylistManager().clear();
        updatePlaylistDisplay();
        updateCurrentTrackDisplay(null);
    }
    
    private void shufflePlaylist() {
        controller.getPlaylistManager().shuffle();
        updatePlaylistDisplay();
    }
    
    private void moveSelectedUp() {
        int index = playlistView.getSelectedIndex();
        if (index > 0) {
            MediaFile file = playlistModel.getElementAt(index);
            if (controller.getPlaylistManager().moveUp(file)) {
                updatePlaylistDisplay();
                playlistView.setSelectedIndex(index - 1);
            }
        }
    }
    
    private void moveSelectedDown() {
        int index = playlistView.getSelectedIndex();
        if (index != -1 && index < playlistModel.getSize() - 1) {
            MediaFile file = playlistModel.getElementAt(index);
            if (controller.getPlaylistManager().moveDown(file)) {
                updatePlaylistDisplay();
                playlistView.setSelectedIndex(index + 1);
            }
        }
    }
    
    private void removeSelected() {
        int index = playlistView.getSelectedIndex();
        if (index != -1) {
            MediaFile file = playlistModel.getElementAt(index);
            controller.getPlaylistManager().removeFile(file);
            updatePlaylistDisplay();
        }
    }
    
    private void toggleRepeatMode() {
        RepeatMode currentMode = controller.getRepeatMode();
        controller.setRepeatMode(currentMode.next());
    }
    
    private void toggleShuffleMode() {
        controller.setShuffleMode(!controller.isShuffleMode());
    }
    
    private void exitApplication() {
        controller.stop();
        if (controller instanceof PlaybackController) {
            ((PlaybackController) controller).dispose();
        }
        System.exit(0);
    }
    
    private String formatTime(int seconds) {
        int minutes = seconds / 60;
        seconds = seconds % 60;
        return String.format("%02d:%02d", minutes, seconds);
    }
    
    // IUserInterface implementation
    @Override
    public void show() {
        mainFrame.setVisible(true);
    }
    
    @Override
    public void hide() {
        mainFrame.setVisible(false);
    }
    
    @Override
    public void updatePlaylistDisplay() {
        SwingUtilities.invokeLater(() -> {
            playlistModel.clear();
            IPlaylistManager playlist = controller.getPlaylistManager();
            
            for (int i = 0; i < playlist.size(); i++) {
                playlistModel.addElement(playlist.getFileAt(i));
            }
            
            // Highlight current track
            int currentIndex = playlist.getCurrentIndex();
            if (currentIndex >= 0 && currentIndex < playlistModel.getSize()) {
                playlistView.setSelectedIndex(currentIndex);
            }
            
            // Update playlist info
            if (playlist instanceof PlaylistManager) {
                PlaylistManager pm = (PlaylistManager) playlist;
                String info = String.format("%d tracks, %s", 
                    playlist.size(), pm.getFormattedTotalDuration());
                playlistInfoLabel.setText(info);
            } else {
                playlistInfoLabel.setText(playlist.size() + " tracks");
            }
        });
    }
    
    @Override
    public void updatePlaybackStatus(String status) {
        SwingUtilities.invokeLater(() -> statusLabel.setText(status));
    }
    
    @Override
    public void updateProgress(int position, int duration) {
        SwingUtilities.invokeLater(() -> {
            updatingProgress = true;
            
            if (duration > 0) {
                int progress = (position * 100) / duration;
                progressSlider.setValue(progress);
            }
            
            currentTimeLabel.setText(formatTime(position));
            totalTimeLabel.setText(formatTime(duration));
            
            updatingProgress = false;
        });
    }
    
    @Override
    public void updateVolumeDisplay(float volume) {
        SwingUtilities.invokeLater(() -> {
            volumeSlider.setValue((int) (volume * 100));
        });
    }
    
    @Override
    public void updateCurrentTrackDisplay(MediaFile file) {
        SwingUtilities.invokeLater(() -> {
            if (file != null) {
                currentTrackLabel.setText(file.getTitle());
                currentArtistLabel.setText(file.getArtist());
                setControlsEnabled(true);
            } else {
                currentTrackLabel.setText("No track selected");
                currentArtistLabel.setText("Unknown Artist");
                setControlsEnabled(false);
            }
        });
    }
    
    @Override
    public void showError(String message) {
        SwingUtilities.invokeLater(() -> {
            JOptionPane.showMessageDialog(mainFrame, message, "Error", JOptionPane.ERROR_MESSAGE);
        });
    }
    
    @Override
    public void showInfo(String message) {
        SwingUtilities.invokeLater(() -> {
            JOptionPane.showMessageDialog(mainFrame, message, "Information", JOptionPane.INFORMATION_MESSAGE);
        });
    }
    
    @Override
    public void setControlsEnabled(boolean enabled) {
        SwingUtilities.invokeLater(() -> {
            playButton.setEnabled(enabled);
            pauseButton.setEnabled(enabled);
            stopButton.setEnabled(enabled);
            nextButton.setEnabled(enabled && controller.getPlaylistManager().size() > 1);
            prevButton.setEnabled(enabled && controller.getPlaylistManager().size() > 1);
            progressSlider.setEnabled(enabled);
        });
    }
    
    @Override
    public void updateRepeatModeDisplay(RepeatMode mode) {
        SwingUtilities.invokeLater(() -> {
            switch (mode) {
                case NONE:
                    repeatButton.setText("🔁");
                    repeatButton.setToolTipText("No Repeat");
                    break;
                case ONE:
                    repeatButton.setText("🔂");
                    repeatButton.setToolTipText("Repeat One");
                    break;
                case ALL:
                    repeatButton.setText("🔁");
                    repeatButton.setToolTipText("Repeat All");
                    break;
            }
        });
    }
    
    @Override
    public void updateShuffleModeDisplay(boolean enabled) {
        SwingUtilities.invokeLater(() -> {
            if (enabled) {
                shuffleButton.setText("🔀");
                shuffleButton.setBackground(Color.LIGHT_GRAY);
            } else {
                shuffleButton.setText("🔀");
                shuffleButton.setBackground(null);
            }
            shuffleButton.setToolTipText(enabled ? "Shuffle On" : "Shuffle Off");
        });
    }
    
    // IPlaybackListener implementation
    @Override
    public void onPlaybackStarted(MediaFile file) {
        updatePlaybackStatus("Playing: " + file.getTitle());
        updateCurrentTrackDisplay(file);
    }
    
    @Override
    public void onPlaybackPaused(MediaFile file) {
        updatePlaybackStatus("Paused: " + file.getTitle());
    }
    
    @Override
    public void onPlaybackStopped(MediaFile file) {
        updatePlaybackStatus("Stopped");
        updateProgress(0, 0);
    }
    
    @Override
    public void onPlaybackCompleted(MediaFile file) {
        updatePlaybackStatus("Completed: " + file.getTitle());
    }
    
    @Override
    public void onPlaybackProgress(MediaFile file, int position, int duration) {
        updateProgress(position, duration);
    }
    
    @Override
    public void onPlaybackError(MediaFile file, String error) {
        updatePlaybackStatus("Error");
        showError("Playback error: " + error);
    }
    
    @Override
    public void onVolumeChanged(float volume) {
        updateVolumeDisplay(volume);
    }
    
    @Override
    public void onRepeatModeChanged(RepeatMode mode) {
        updateRepeatModeDisplay(mode);
    }
    
    @Override
    public void onShuffleModeChanged(boolean enabled) {
        updateShuffleModeDisplay(enabled);
    }
    
    // Custom cell renderer for playlist
    private class PlaylistCellRenderer extends DefaultListCellRenderer {
        @Override
        public Component getListCellRendererComponent(JList<?> list, Object value,
                int index, boolean isSelected, boolean cellHasFocus) {
            super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
            
            if (value instanceof MediaFile) {
                MediaFile file = (MediaFile) value;
                setText(String.format("<html><b>%s</b><br><small>%s - %s</small></html>",
                    file.getTitle(), file.getArtist(), file.getFormattedFileSize()));
                
                // Highlight current playing track
                if (index == controller.getPlaylistManager().getCurrentIndex()) {
                    setBackground(isSelected ? Color.BLUE : Color.YELLOW);
                }
            }
            
            return this;
        }
    }
}