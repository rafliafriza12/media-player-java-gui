import javax.swing.*;
import javax.swing.filechooser.FileNameExtensionFilter;
import java.awt.*;
import java.awt.event.*;
import java.io.File;
import java.util.List;

public class PlayerUI implements PlaybackListener {
    private JFrame mainFrame;
    private JButton playButton, pauseButton, stopButton, nextButton, prevButton;
    // private JSlider volumeSlider, progressSlider;
    private JList<MediaFile> playlistView;
    private DefaultListModel<MediaFile> playlistModel;
    private JLabel currentTimeLabel, totalTimeLabel, statusLabel;
    private JMenuBar menuBar;
    private JMenu fileMenu;
    private JMenuItem openFileItem, openDirItem, exitItem;
    private PlaybackController controller;
    private Timer progressTimer;
    
    public PlayerUI(PlaybackController controller) {
        this.controller = controller;
        controller.addPlaybackListener(this);
    }
    
    public void initComponents() {
        // Initialize main frame
        mainFrame = new JFrame("Java Media Player");
        mainFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        mainFrame.setSize(800, 600);
        mainFrame.setLayout(new BorderLayout());
        
        // Create menu bar
        menuBar = new JMenuBar();
        fileMenu = new JMenu("File");
        openFileItem = new JMenuItem("Open File");
        openDirItem = new JMenuItem("Open Directory");
        exitItem = new JMenuItem("Exit");
        
        fileMenu.add(openFileItem);
        fileMenu.add(openDirItem);
        fileMenu.addSeparator();
        fileMenu.add(exitItem);
        menuBar.add(fileMenu);
        mainFrame.setJMenuBar(menuBar);
        
        // Create control panel
        JPanel controlPanel = new JPanel(new FlowLayout());
        playButton = new JButton("Play");
        pauseButton = new JButton("Pause");
        stopButton = new JButton("Stop");
        prevButton = new JButton("Previous");
        nextButton = new JButton("Next");
        
        controlPanel.add(prevButton);
        controlPanel.add(playButton);
        controlPanel.add(pauseButton);
        controlPanel.add(stopButton);
        controlPanel.add(nextButton);
        
        // Create status panel
        // JPanel statusPanel = new JPanel(new BorderLayout());
        // progressSlider = new JSlider(0, 100, 0);
        // currentTimeLabel = new JLabel("00:00");
        // totalTimeLabel = new JLabel("00:00");
        // statusLabel = new JLabel("Ready");
        
        // JPanel timePanel = new JPanel(new BorderLayout());
        // timePanel.add(currentTimeLabel, BorderLayout.WEST);
        // timePanel.add(totalTimeLabel, BorderLayout.EAST);
        
        // statusPanel.add(progressSlider, BorderLayout.CENTER);
        // statusPanel.add(timePanel, BorderLayout.SOUTH);
        
        // // Create volume panel
        // JPanel volumePanel = new JPanel(new BorderLayout());
        // volumeSlider = new JSlider(JSlider.VERTICAL, 0, 100, 80);
        // volumePanel.add(new JLabel("Volume"), BorderLayout.NORTH);
        // volumePanel.add(volumeSlider, BorderLayout.CENTER);
        
        // Create playlist panel
        JPanel playlistPanel = new JPanel(new BorderLayout());
        playlistModel = new DefaultListModel<>();
        playlistView = new JList<>(playlistModel);
        JScrollPane scrollPane = new JScrollPane(playlistView);
        playlistPanel.add(new JLabel("Playlist"), BorderLayout.NORTH);
        playlistPanel.add(scrollPane, BorderLayout.CENTER);
        
        // Add components to main frame
        mainFrame.add(controlPanel, BorderLayout.NORTH);
        // mainFrame.add(statusPanel, BorderLayout.SOUTH);
        // mainFrame.add(volumePanel, BorderLayout.EAST);
        mainFrame.add(playlistPanel, BorderLayout.CENTER);
        
        // Set up action listeners
        openFileItem.addActionListener(e -> openFile());
        openDirItem.addActionListener(e -> openDirectory());
        exitItem.addActionListener(e -> System.exit(0));
        
        playButton.addActionListener(e -> controller.play());
        pauseButton.addActionListener(e -> controller.pause());
        stopButton.addActionListener(e -> controller.stop());
        nextButton.addActionListener(e -> controller.next());
        prevButton.addActionListener(e -> controller.previous());
        
        // volumeSlider.addChangeListener(e -> controller.setVolume(volumeSlider.getValue() / 100f));
        
        playlistView.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (e.getClickCount() == 2) {
                    int index = playlistView.getSelectedIndex();
                    if (index != -1) {
                        MediaFile selectedFile = playlistModel.getElementAt(index);
                        controller.loadAndPlay(selectedFile);
                    }
                }
            }
        });
        
        // Setup progress timer
        progressTimer = new Timer(1000, e -> {
            if (controller.isPlaying()) {
                int currentPos = controller.getCurrentPosition();
                int duration = controller.getDuration();
                if (duration > 0) {
                    // progressSlider.setValue(currentPos * 100 / duration);
                    currentTimeLabel.setText(formatTime(currentPos));
                    totalTimeLabel.setText(formatTime(duration));
                }
            }
        });
        progressTimer.start();
    }

    private void openFile() {
        JFileChooser fileChooser = new JFileChooser();
        FileNameExtensionFilter filter = new FileNameExtensionFilter(
                "Media files", "mp3", "wav", "mp4", "flac");
        fileChooser.setFileFilter(filter);
        
        int result = fileChooser.showOpenDialog(mainFrame);
        if (result == JFileChooser.APPROVE_OPTION) {
            File selectedFile = fileChooser.getSelectedFile();
            FileManager fileManager = new FileManager();
            if (fileManager.isSupportedFormat(selectedFile.getName())) {
                MediaFile mediaFile = fileManager.openFile(selectedFile.getPath());
                controller.getPlaylist().addFile(mediaFile);
                updatePlaylist();
                controller.loadAndPlay(mediaFile);
            } else {
                JOptionPane.showMessageDialog(mainFrame, 
                        "Unsupported file format", "Error", JOptionPane.ERROR_MESSAGE);
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
            
            for (MediaFile file : files) {
                controller.getPlaylist().addFile(file);
            }
            updatePlaylist();
            statusLabel.setText("Added " + files.size() + " files to playlist");
        }
    }
    
    public void updatePlaylist() {
        playlistModel.clear();
        Playlist playlist = controller.getPlaylist();
        for (int i = 0; i < playlist.size(); i++) {
            playlistModel.addElement(playlist.getFileAt(i));
        }
    }
    
    public void updatePlaybackStatus() {
        if (controller.isPlaying()) {
            statusLabel.setText("Playing: " + controller.getCurrentFile().getTitle());
        } else {
            statusLabel.setText("Stopped");
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

    @Override
    public void onPlay() {
        statusLabel.setText("Playing: " + controller.getCurrentFile().getTitle());
    }

    @Override
    public void onPause() {
        statusLabel.setText("Paused");
    }

    @Override
    public void onStop() {
        statusLabel.setText("Stopped");
        // progressSlider.setValue(0);
        currentTimeLabel.setText("00:00");
    }

    @Override
    public void onComplete() {
        statusLabel.setText("Playback complete");
        controller.next();
    }

    @Override
    public void onProgress(int position, int duration) {
        // This is handled by the timer
    }
}