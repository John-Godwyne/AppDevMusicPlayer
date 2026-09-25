# 🎵 Migz Music Player

A Java Desktop Music Player application built with **Pure Java (Swing)** using the **MVC architecture**, **JPA Persistence**, and **Custom Event Handling**.

## 📋 Project Description
This application allows users to select songs from a playlist, view lyrics and album art, and control audio playback (Play, Pause, Stop). The song data is persisted using JPA with an embedded H2 database.

## 🛠️ Technologies Used
- **Language:** Java (JDK 21+)
- **GUI:** Java Swing (Layout Managers: BorderLayout, FlowLayout)
- **Architecture:** MVC (Model-View-Controller)
- **Persistence:** JPA (Hibernate 6.4.0) with H2 Database
- **Audio:** Java Sound API (`javax.sound.sampled`)
- **Events:** Custom `SongChangeEvent` and `SongChangeListener`

## 📂 Project Structure
```text
migz-Actual-Music-PLayer/
├── lib/               # External JAR libraries (Not committed to Git)
├── resources/         # Audio (.wav), Images (.jpg), Lyrics (.txt)
├── src/               # Java source code
│   ├── controller/    # AudioEngine, PlayerController
│   ├── events/        # SongChangeEvent, SongChangeListener
│   ├── model/         # Song, Playlist
│   ├── persistence/   # DatabaseManager (JPA)
│   ├── view/          # MainFrame, PlayerPanel (Swing)
│   ├── META-INF/      # persistence.xml
│   ├── Constants.java
│   └── Main.java      # Entry point
└── README.md
```

## 👥 Team Roles & Contributions

This project was completed by a team of 5 members. Work was divided based on the MVC architecture and the grading rubric:

| Member | Role | Primary Responsibilities | Rubric Focus |
| :--- | :--- | :--- | :--- |
| **RICO 1** | Project Lead & Backend | MVC structure, JPA/H2 Database, Audio Engine, Lib management | MVC (30), JPA (30), Complexity (20) |
| **MANZANARES** | UI/UX Designer | Swing GUI, Layout Managers, Styling, Responsive resizing | UI/UX (20), Layout Managers (30) |
| **FONTANOS** | Data & Content Manager | 5 .wav files, 5 .jpg images, lyrics, seedDatabase() | Minimum Reqs, Complexity (20) |
| **ABRIO** | Controller & Event Specialist | Custom Events, PlayerController, Next/Prev buttons, Progress Slider | Custom Events (30), Complexity (20) |
| **DE JESUS** | QA Tester & Presenter | Edge case testing, README, .gitignore, Video Demo | Demo (10), Uniqueness (20) |
