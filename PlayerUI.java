import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.filechooser.FileNameExtensionFilter;
import java.awt.*;
import java.awt.event.*;
import java.io.File;
import java.util.List;

public class PlayerUI implements PlaybackListener, MediaPlayerEventListener, 
                               PlaylistEventListener, EqualizerListener {
    private JFrame mainFrame;
    private JButton playButton, pauseButton, stopButton, nextButton, prevButton;
    private JButton shuffleButton, repeatButton;
    private JSlider volumeSlider, progressSlider;
    private JList<MediaFile> playlistView;
    private DefaultListModel<MediaFile> playlistModel;
    private JLabel currentTimeLabel, totalTimeLabel, statusLabel, currentSongLabel;
    private JMenuBar menuBar;
    private JPanel equalizerPanel;
    private JSlider[] equalizerSliders;
    private JComboBox<String> presetComboBox;
    private JCheckBox equalizerEnabledCheckBox;
    private PlaybackController controller;
    private Timer progressTimer;
    private JProgressBar volumeProgressBar;
    
    // Colors for modern UI
    private final Color DARK_BG = new Color(40, 40, 40);
    private final Color MEDIUM_BG = new Color(60, 60, 60);
    private final Color LIGHT_BG = new Color(80, 80, 80);
    private final Color ACCENT_COLOR = new Color(0, 150, 255);
    private final Color TEXT_COLOR = Color.WHITE;
    
    public PlayerUI(PlaybackController controller) {
        this.controller = controller;
        controller.addPlaybackListener(this);
        controller.getMediaPlayer().addListener(this);
        controller.getPlaylist().addPlaylistEventListener(this);
        controller.getMediaPlayer().getAudioProcessor().addEqualizerListener(this);
        
        // Set dark theme
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeel());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    
    public void initComponents() {
        // Initialize main frame with modern design
        mainFrame = new JFrame("Java Media Player Pro");
        mainFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        mainFrame.setSize(1000, 700);
        mainFrame.setLocationRelativeTo(null);
        mainFrame.getContentPane().setBackground(DARK_BG);
        mainFrame.setLayout(new BorderLayout());
        
        createMenuBar();
        createTopPanel();
        createControlPanel();
        createMainContent();
        createStatusPanel();
        
        setupEventListeners();
        setupProgressTimer();
        
        // Apply modern styling
        applyModernStyling();
    }
    
    private void createMenuBar() {
        menuBar = new JMenuBar();
        menuBar.setBackground(DARK_BG);
        menuBar.setBorder(new EmptyBorder(5, 5, 5, 5));
        
        // File Menu
        JMenu fileMenu = new JMenu("File");
        fileMenu.setForeground(TEXT_COLOR);
        JMenuItem openFileItem = new JMenuItem("Open File");
        JMenuItem openDirItem = new JMenuItem("Open Directory");
        JMenuItem savePlaylistItem = new JMenuItem("Save Playlist");
        JMenuItem loadPlaylistItem = new JMenuItem("Load Playlist");
        JMenuItem exitItem = new JMenuItem("Exit");
        
        fileMenu.add(openFileItem);
        fileMenu.add(openDirItem);
        fileMenu.addSeparator();
        fileMenu.add(savePlaylistItem);
        fileMenu.add(loadPlaylistItem);
        fileMenu.addSeparator();
        fileMenu.add(exitItem);
        
        // View Menu
        JMenu viewMenu = new JMenu("View");
        viewMenu.setForeground(TEXT_COLOR);
        JMenuItem showEqualizerItem = new JMenuItem("Show Equalizer");
        JMenuItem togglePlaylistItem = new JMenuItem("Toggle Playlist");
        
        viewMenu.add(showEqualizerItem);
        viewMenu.add(togglePlaylistItem);
        
        // Tools Menu
        JMenu toolsMenu = new JMenu("Tools");
        toolsMenu.setForeground(TEXT_COLOR);
        JMenuItem settingsItem = new JMenuItem("Settings");
        JMenuItem aboutItem = new JMenuItem("About");
        
        toolsMenu.add(settingsItem);
        toolsMenu.add(aboutItem);
        
        menuBar.add(fileMenu);
        menuBar.add(viewMenu);
        menuBar.add(toolsMenu);
        
        mainFrame.setJMenuBar(menuBar);
        
        // Menu action listeners
        openFileItem.addActionListener(e -> openFile());
        openDirItem.addActionListener(e -> openDirectory());
        savePlaylistItem.addActionListener(e -> savePlaylist());
        loadPlaylistItem.addActionListener(e -> loadPlaylist());
        exitItem.addActionListener(e -> System.exit(0));
        showEqualizerItem.addActionListener(e -> toggleEqualizer());
        aboutItem.addActionListener(e -> showAbout());
    }
    
    private void createTopPanel() {
        JPanel topPanel = new JPanel(new BorderLayout());
        topPanel.setBackground(DARK_BG);
        topPanel.setBorder(new EmptyBorder(10, 15, 10, 15));
        
        // Current song info
        currentSongLabel = new JLabel("No song selected");
        currentSongLabel.setForeground(TEXT_COLOR);
        currentSongLabel.setFont(new Font("Arial", Font.BOLD, 16));
        
        topPanel.add(currentSongLabel, BorderLayout.CENTER);
        
        mainFrame.add(topPanel, BorderLayout.NORTH);
    }
    
    private void createControlPanel() {
        JPanel controlPanel = new JPanel();
        controlPanel.setLayout(new BoxLayout(controlPanel, BoxLayout.Y_AXIS));
        controlPanel.setBackground(MEDIUM_BG);
        controlPanel.setBorder(new EmptyBorder(15, 15, 15, 15));
        
        // Playback controls
        JPanel playbackPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 5));
        playbackPanel.setBackground(MEDIUM_BG);
        
        prevButton = createStyledButton("⏮", "Previous");
        playButton = createStyledButton("▶", "Play");
        pauseButton = createStyledButton("⏸", "Pause");
        stopButton = createStyledButton("⏹", "Stop");
        nextButton = createStyledButton("⏭", "Next");
        
        shuffleButton = createStyledButton("🔀", "Shuffle");
        repeatButton = createStyledButton("🔁", "Repeat");
        
        playbackPanel.add(shuffleButton);
        playbackPanel.add(prevButton);
        playbackPanel.add(playButton);
        playbackPanel.add(pauseButton);
        playbackPanel.add(stopButton);
        playbackPanel.add(nextButton);
        playbackPanel.add(repeatButton);
        
        // Progress controls
        JPanel progressPanel = new JPanel(new BorderLayout(10, 5));
        progressPanel.setBackground(MEDIUM_BG);
        
        currentTimeLabel = new JLabel("00:00");
        currentTimeLabel.setForeground(TEXT_COLOR);
        totalTimeLabel = new JLabel("00:00");
        totalTimeLabel.setForeground(TEXT_COLOR);
        
        progressSlider = new JSlider(0, 100, 0);
        progressSlider.setBackground(MEDIUM_BG);
        progressSlider.setForeground(ACCENT_COLOR);
        
        progressPanel.add(currentTimeLabel, BorderLayout.WEST);
        progressPanel.add(progressSlider, BorderLayout.CENTER);
        progressPanel.add(totalTimeLabel, BorderLayout.EAST);
        
        // Volume controls
        JPanel volumePanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        volumePanel.setBackground(MEDIUM_BG);
        
        JLabel volumeLabel = new JLabel("♪");
        volumeLabel.setForeground(TEXT_COLOR);
        volumeLabel.setFont(new Font("Arial", Font.BOLD, 14));
        
        volumeSlider = new JSlider(0, 100, 80);
        volumeSlider.setBackground(MEDIUM_BG);
        volumeSlider.setForeground(ACCENT_COLOR);
        volumeSlider.setPreferredSize(new Dimension(120, 25));
        
        volumePanel.add(volumeLabel);
        volumePanel.add(volumeSlider);
        
        controlPanel.add(playbackPanel);
        controlPanel.add(Box.createVerticalStrut(10));
        controlPanel.add(progressPanel);
        controlPanel.add(Box.createVerticalStrut(10));
        controlPanel.add(volumePanel);
        
        mainFrame.add(controlPanel, BorderLayout.SOUTH);
    }
    
    private JButton createStyledButton(String text, String tooltip) {
        JButton button = new JButton(text);
        button.setToolTipText(tooltip);
        button.setPreferredSize(new Dimension(50, 40));
        button.setBackground(LIGHT_BG);
        button.setForeground(TEXT_COLOR);
        button.setBorder(BorderFactory.createRaisedBorderBorder());
        button.setFocusPainted(false);
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));
        
        // Hover effect
        button.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                button.setBackground(ACCENT_COLOR);
            }
            
            @Override
            public void mouseExited(MouseEvent e) {
                button.setBackground(LIGHT_BG);
            }
        });
        
        return button;
    }
    
    private void createMainContent() {
        JPanel mainContent = new JPanel(new BorderLayout());
        mainContent.setBackground(DARK_BG);
        
        // Playlist panel
        JPanel playlistPanel = createPlaylistPanel();
        
        // Equalizer panel (initially hidden)
        equalizerPanel = createEqualizerPanel();
        equalizerPanel.setVisible(false);
        
        JSplitPane splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, 
                                            playlistPanel, equalizerPanel);
        splitPane.setResizeWeight(0.7);
        splitPane.setBackground(DARK_BG);
        
        mainContent.add(splitPane, BorderLayout.CENTER);
        mainFrame.add(mainContent, BorderLayout.CENTER);
    }
    
    private JPanel createPlaylistPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(DARK_BG);
        panel.setBorder(BorderFactory.createTitledBorder(
            BorderFactory.createLineBorder(ACCENT_COLOR), 
            "Playlist", 0, 0, null, TEXT_COLOR));
        
        playlistModel = new DefaultListModel<>();
        playlistView = new JList<>(playlistModel);
        playlistView.setBackground(LIGHT_BG);
        playlistView.setForeground(TEXT_COLOR);
        playlistView.setSelectionBackground(ACCENT_COLOR);
        playlistView.setSelectionForeground(Color.WHITE);
        
        JScrollPane scrollPane = new JScrollPane(playlistView);
        scrollPane.setBackground(DARK_BG);
        
        panel.add(scrollPane, BorderLayout.CENTER);
        
        // Playlist controls
        JPanel playlistControls = new JPanel(new FlowLayout());
        playlistControls.setBackground(DARK_BG);
        
        JButton clearButton = createStyledButton("Clear", "Clear Playlist");
        JButton removeButton = createStyledButton("Remove", "Remove Selected");
        
        clearButton.addActionListener(e -> controller.getPlaylist().clear());
        removeButton.addActionListener(e -> {
            MediaFile selected = playlistView.getSelectedValue();
            if (selected != null) {
                controller.getPlaylist().removeFile(selected);
            }
        });
        
        playlistControls.add(clearButton);
        playlistControls.add(removeButton);
        
        panel.add(playlistControls, BorderLayout.SOUTH);
        
        return panel;
    }
    
    private JPanel createEqualizerPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(DARK_BG);
        panel.setBorder(BorderFactory.createTitledBorder(
            BorderFactory.createLineBorder(ACCENT_COLOR), 
            "Equalizer", 0, 0, null, TEXT_COLOR));
        
        // Equalizer controls
        JPanel topControls = new JPanel(new FlowLayout());
        topControls.setBackground(DARK_BG);
        
        equalizerEnabledCheckBox = new JCheckBox("Enable");
        equalizerEnabledCheckBox.setBackground(DARK_BG);
        equalizerEnabledCheckBox.setForeground(TEXT_COLOR);
        
        presetComboBox = new JComboBox<>(controller.getMediaPlayer()
                                                .getAudioProcessor().getAvailablePresets());
        presetComboBox.setBackground(LIGHT_BG);
        presetComboBox.setForeground(TEXT_COLOR);
        
        topControls.add(equalizerEnabledCheckBox);
        topControls.add(new JLabel("Preset:"));
        topControls.add(presetComboBox);
        
        // Equalizer sliders
        JPanel slidersPanel = new JPanel(new GridLayout(1, 10, 5, 5));
        slidersPanel.setBackground(DARK_BG);
        slidersPanel.setBorder(new EmptyBorder(10, 10, 10, 10));
        
        AudioProcessor processor = controller.getMediaPlayer().getAudioProcessor();
        equalizerSliders = new JSlider[processor.getBandCount()];
        
        for (int i = 0; i < processor.getBandCount(); i++) {
            JPanel bandPanel = new JPanel(new BorderLayout());
            bandPanel.setBackground(DARK_BG);
            
            JLabel freqLabel = new JLabel(processor.getBandFrequency(i));
            freqLabel.setForeground(TEXT_COLOR);
            freqLabel.setHorizontalAlignment(SwingConstants.CENTER);
            freqLabel.setFont(new Font("Arial", Font.PLAIN, 10));
            
            JSlider slider = new JSlider(JSlider.VERTICAL, -12, 12, 0);
            slider.setBackground(DARK_BG);
            slider.setForeground(ACCENT_COLOR);
            slider.setMajorTickSpacing(6);
            slider.setMinorTickSpacing(3);
            slider.setPaintTicks(true);
            slider.setPaintLabels(true);
            slider.setFont(new Font("Arial", Font.PLAIN, 8));
            
            final int bandIndex = i;
            slider.addChangeListener(e -> {
                if (!slider.getValueIsAdjusting()) {
                    processor.setBand(bandIndex, slider.getValue());
                }
            });
            
            equalizerSliders[i] = slider;
            
            bandPanel.add(freqLabel, BorderLayout.NORTH);
            bandPanel.add(slider, BorderLayout.CENTER);
            
            slidersPanel.add(bandPanel);
        }
        
        panel.add(topControls, BorderLayout.NORTH);
        panel.add(slidersPanel, BorderLayout.CENTER);
        
        return panel;
    }
    
    private void createStatusPanel() {
        JPanel statusPanel = new JPanel(new BorderLayout());
        statusPanel.setBackground(DARK_BG);
        statusPanel.setBorder(new EmptyBorder(5, 15, 5, 15));
        
        statusLabel = new JLabel("Ready");
        statusLabel.setForeground(TEXT_COLOR);
        statusLabel.setFont(new Font("Arial", Font.PLAIN, 12));
        
        statusPanel.add(statusLabel, BorderLayout.WEST);
        
        mainFrame.add(statusPanel, BorderLayout.SOUTH);
    }
    
    private void setupEventListeners() {
        playButton.addActionListener(e -> controller.play());
        pauseButton.addActionListener(e -> controller.pause());
        stopButton.addActionListener(e -> controller.stop());
        nextButton.addActionListener(e -> controller.next());
        prevButton.addActionListener(e -> controller.previous());
        
        shuffleButton.addActionListener(e -> {
            boolean shuffle = !controller.getPlaylist().isShuffle();
            controller.getPlaylist().setShuffle(shuffle);
            shuffleButton.setBackground(shuffle ? ACCENT_COLOR : LIGHT_BG);
        });
        
        repeatButton.addActionListener(e -> {
            boolean repeat = !controller.getPlaylist().isRepeat();
            controller.getPlaylist().setRepeat(repeat);
            repeatButton.setBackground(repeat ? ACCENT_COLOR : LIGHT_BG);
        });
        
        volumeSlider.addChangeListener(e -> {
            if (!volumeSlider.getValueIsAdjusting()) {
                controller.setVolume(volumeSlider.getValue() / 100f);
            }
        });
        
        playlistView.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (e.getClickCount() == 2) {
                    int index = playlistView.getSelectedIndex();
                    if (index != -1) {
                        MediaFile selectedFile = playlistModel.getElementAt(index);
                        controller.getPlaylist().setCurrentIndex(index);
                        controller.loadAndPlay(selectedFile);
                    }
                }
            }
        });
        
        // Equalizer event listeners
        equalizerEnabledCheckBox.addActionListener(e -> {
            controller.getMediaPlayer().getAudioProcessor()
                    .setEnabled(equalizerEnabledCheckBox.isSelected());
        });
        
        presetComboBox.addActionListener(e -> {
            String preset = (String) presetComboBox.getSelectedItem();
            if (preset != null) {
                controller.getMediaPlayer().getAudioProcessor().setPreset(preset);
                updateEqualizerSliders();
            }
        });
    }
    
    private void setupProgressTimer() {
        progressTimer = new Timer(1000, e -> {
            if (controller.isPlaying()) {
                int currentPos = controller.getCurrentPosition();
                int duration = controller.getDuration();
                if (duration > 0) {
                    progressSlider.setValue(currentPos * 100 / duration);
                    currentTimeLabel.setText(formatTime(currentPos));
                    totalTimeLabel.setText(formatTime(duration));
                }
            }
        });
        progressTimer.start();
    }
    
    private void applyModernStyling() {
        // Set custom renderer for playlist
        playlistView.setCellRenderer(new DefaultListCellRenderer() {
            @Override
            public Component getListCellRendererComponent(JList<?> list, Object value,
                    int index, boolean isSelected, boolean cellHasFocus) {
                super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
                
                if (isSelected) {
                    setBackground(ACCENT_COLOR);
                    setForeground(Color.WHITE);
                } else {
                    setBackground(index % 2 == 0 ? LIGHT_BG : MEDIUM_BG);
                    setForeground(TEXT_COLOR);
                }
                
                setBorder(new EmptyBorder(5, 10, 5, 10));
                return this;
            }
        });
    }
    
    private void openFile() {
        JFileChooser fileChooser = new JFileChooser();
        FileNameExtensionFilter filter = new FileNameExtensionFilter(
                "Media files", "mp3", "wav", "mp4", "flac", "ogg", "aiff", "au");
        fileChooser.setFileFilter(filter);
        fileChooser.setMultiSelectionEnabled(true);
        
        int result = fileChooser.showOpenDialog(mainFrame);
        if (result == JFileChooser.APPROVE_OPTION) {
            File[] selectedFiles = fileChooser.getSelectedFiles();
            FileManager fileManager = new FileManager();
            
            for (File file : selectedFiles) {
                if (fileManager.isSupportedFormat(file.getName())) {
                    MediaFile mediaFile = fileManager.openFile(file.getPath());
                    controller.getPlaylist().addFile(mediaFile);
                }
            }
            
            if (selectedFiles.length > 0) {
                updatePlaylist();
                statusLabel.setText("Added " + selectedFiles.length + " files to playlist");
            }
        }
    }
    
    private void openDirectory() {
        JFileChooser dirChooser = new JFileChooser();
        dirChooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
        
        int result = dirChooser.showOpenDialog(mainFrame);
        if (result == JFileChooser.APPROVE_OPTION) {
            File selectedDir = dirChooser.getSelectedFile();
            FileManager fileManager = new FileManager();
            List<MediaFile> files = fileManager.scanDirectory(selectedDir.getPath());
            
            controller.getPlaylist().addFiles(files);
            updatePlaylist();
            statusLabel.setText("Added " + files.size() + " files from directory");
        }
    }
    
    private void savePlaylist() {
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setFileFilter(new FileNameExtensionFilter("Playlist files", "m3u", "pls"));
        
        int result = fileChooser.showSaveDialog(mainFrame);
        if (result == JFileChooser.APPROVE_OPTION) {
            File file = fileChooser.getSelectedFile();
            String filename = file.getAbsolutePath();
            if (!filename.endsWith(".m3u") && !filename.endsWith(".pls")) {
                filename += ".m3u";
            }
            controller.getPlaylist().save(filename);
        }
    }
    
    private void loadPlaylist() {
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setFileFilter(new FileNameExtensionFilter("Playlist files", "m3u", "pls"));
        
        int result = fileChooser.showOpenDialog(mainFrame);
        if (result == JFileChooser.APPROVE_OPTION) {
            File file = fileChooser.getSelectedFile();
            controller.getPlaylist().load(file.getAbsolutePath());
            updatePlaylist();
        }
    }
    
    private void toggleEqualizer() {
        equalizerPanel.setVisible(!equalizerPanel.isVisible());
        mainFrame.revalidate();
        mainFrame.repaint();
    }
    
    private void showAbout() {
        JOptionPane.showMessageDialog(mainFrame,
                "Java Media Player Pro v2.0\n" +
                "A modern media player with equalizer\n" +
                "Built with Java Swing\n\n" +
                "Features:\n" +
                "• MP3 playback support\n" +
                "• 10-band equalizer\n" +
                "• Playlist management\n" +
                "• Shuffle and repeat modes\n" +
                "• Modern dark theme",
                "About", JOptionPane.INFORMATION_MESSAGE);
    }
    
    private void updateEqualizerSliders() {
        AudioProcessor processor = controller.getMediaPlayer().getAudioProcessor();
        for (int i = 0; i < equalizerSliders.length; i++) {
            equalizerSliders[i].setValue((int) processor.getBand(i));
        }
    }
    
    public void updatePlaylist() {
        playlistModel.clear();
        Playlist playlist = controller.getPlaylist();
        for (int i = 0; i < playlist.size(); i++) {
            playlistModel.addElement(playlist.getFileAt(i));
        }
    }
    
    private String formatTime(int seconds) {
        int minutes = seconds / 60;
        seconds = seconds % 60;
        return String.format("%02d:%02d", minutes, seconds);
    }
    
    public void setVisible(boolean visible) {
        mainFrame.setVisible(visible);
    }

    // PlaybackListener implementation
    @Override
    public void onPlay() {
        statusLabel.setText("Playing: " + 
            (controller.getCurrentFile() != null ? controller.getCurrentFile().getTitle() : ""));
        if (controller.getCurrentFile() != null) {
            currentSongLabel.setText(controller.getCurrentFile().getArtist() + " - " + 
                                   controller.getCurrentFile().getTitle());
        }
    }

    @Override
    public void onPause() {
        statusLabel.setText("Paused");
    }

    @Override
    public void onStop() {
        statusLabel.setText("Stopped");
        progressSlider.setValue(0);
        currentTimeLabel.setText("00:00");
        currentSongLabel.setText("No song selected");
    }

    @Override
    public void onComplete() {
        statusLabel.setText("Playback complete");
        if (controller.getPlaylist().isRepeat()) {
            controller.play();
        } else {
            controller.next();
        }
    }

    @Override
    public void onProgress(int position, int duration) {
        if (duration > 0) {
            progressSlider.setValue(position * 100 / duration);
            currentTimeLabel.setText(formatTime(position));
            totalTimeLabel.setText(formatTime(duration));
        }
    }

    // MediaPlayerEventListener implementation
    @Override
    public void onFileLoaded(MediaFile file) {
        currentSongLabel.setText(file.getArtist() + " - " + file.getTitle());
        statusLabel.setText("Loaded: " + file.getTitle());
    }

    @Override
    public void onPlaybackStarted() {
        onPlay();
    }

    @Override
    public void onPlaybackPaused() {
        onPause();
    }

    @Override
    public void onPlaybackStopped() {
        onStop();
    }

    @Override
    public void onPlaybackCompleted() {
        onComplete();
    }

    @Override
    public void onVolumeChanged(float volume) {
        volumeSlider.setValue((int) (volume * 100));
    }

    @Override
    public void onPositionChanged(int position, int duration) {
        onProgress(position, duration);
    }

    @Override
    public void onError(String error) {
        statusLabel.setText("Error: " + error);
        JOptionPane.showMessageDialog(mainFrame, error, "Playback Error", JOptionPane.ERROR_MESSAGE);
    }

    // PlaylistEventListener implementation
    @Override
    public void onFileAdded(MediaFile file) {
        updatePlaylist();
    }

    @Override
    public void onFileRemoved(MediaFile file) {
        updatePlaylist();
    }

    @Override
    public void onPlaylistCleared() {
        updatePlaylist();
        statusLabel.setText("Playlist cleared");
    }

    @Override
    public void onCurrentFileChanged(MediaFile file) {
        currentSongLabel.setText(file.getArtist() + " - " + file.getTitle());
        // Highlight current file in playlist
        playlistView.setSelectedValue(file, true);
    }

    @Override
    public void onPlaylistSaved(String filename) {
        statusLabel.setText("Playlist saved: " + filename);
    }

    @Override
    public void onPlaylistLoaded(String filename) {
        statusLabel.setText("Playlist loaded: " + filename);
    }

    // EqualizerListener implementation
    @Override
    public void onBandChanged(int band, float value) {
        if (band >= 0 && band < equalizerSliders.length) {
            equalizerSliders[band].setValue((int) value);
        }
    }

    @Override
    public void onPresetChanged(String presetName) {
        presetComboBox.setSelectedItem(presetName);
        updateEqualizerSliders();
    }

    @Override
    public void onEqualizerEnabled(boolean enabled) {
        equalizerEnabledCheckBox.setSelected(enabled);
    }

    @Override
    public void onEqualizerReset() {
        updateEqualizerSliders();
        presetComboBox.setSelectedItem("Flat");
    }
}
