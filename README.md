# 🎵 Radio Labi-Labi 🎵

A lightweight Java-based media player with an intuitive GUI interface.

## 📋 Overview

Radio Labi-Labi is a simple yet powerful media player built in Java that allows users to play audio files (primarily MP3 format) through an easy-to-use graphical interface. The application is designed using object-oriented principles with a modular architecture that separates playback logic, file management, and user interface components.

## ✨ Features

- 🎧 Audio playback of MP3 files
- 📂 File management for opening individual files or scanning directories
- 📑 Playlist management with add, remove, and reorder capabilities
- ⏯️ Playback controls (play, pause, stop, next, previous)
- 🔉 Simple user interface built with Java Swing

## 🛠️ Technical Stack

- *Language*: Java
- *Audio Playback*: JLayer library
- *User Interface*: Java Swing
- *File Handling*: Java IO

## 📊 Architecture

The application follows a modular design with the following key components:

1. *MediaPlayer*: Handles audio playback functionality
2. *FileManager*: Manages file operations and supported formats
3. *MediaFile*: Represents audio files with metadata
4. *Playlist*: Manages collections of media files
5. *PlaybackController*: Coordinates playback operations
6. *PlayerUI*: Provides the graphical user interface

## 🚀 Getting Started

### Prerequisites

- Java Runtime Environment (JRE) 8 or higher
- JLayer library (included in the lib directory)

### Installation

1. Clone the repository:

git clone https://github.com/yourusername/radio-labi-labi.git


2. Navigate to the project directory:

cd radio-labi-labi


3. Run the application:

./run.sh

Or on Windows:

javac -cp ".;lib/*" *.java
java -cp ".;lib/*" MediaPlayerApp


## 💡 Usage

1. Launch the application
2. Use the File menu to open an audio file or directory
3. Control playback using the buttons in the control panel
4. Double-click items in the playlist to play them

## 🤝 Contributing

Contributions are welcome! Please feel free to submit a Pull Request.

1. Fork the repository
2. Create your feature branch (git checkout -b feature/amazing-feature)
3. Commit your changes (git commit -m 'Add some amazing feature')
4. Push to the branch (git push origin feature/amazing-feature)
5. Open a Pull Request

## 📝 License

This project is licensed under the MIT License - see the LICENSE file for details.

## 👏 Acknowledgements

- [JLayer](http://www.javazoom.net/javalayer/javalayer.html) for MP3 decoding
- Java Swing for the GUI components
