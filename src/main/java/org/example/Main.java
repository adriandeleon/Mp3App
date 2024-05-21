package org.example;

import com.mpatric.mp3agic.ID3v2;
import com.mpatric.mp3agic.InvalidDataException;
import com.mpatric.mp3agic.Mp3File;
import com.mpatric.mp3agic.UnsupportedTagException;
import org.eclipse.jetty.ee10.servlet.ServletContextHandler;
import org.eclipse.jetty.server.Server;


import java.awt.*;
import java.io.IOException;
import java.net.URI;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.util.ArrayList;
import java.util.List;

public class Main {
    public static void main(String[] args) throws Exception {
        if (args.length != 1) {
            throw new IllegalArgumentException("You need to specify a valid mp3 directory.");
        }

        // Specify a path with the MP3 files.
        final Path mp3Directory = Path.of(args[0]);
        if (Files.notExists(mp3Directory)) {
            throw new IllegalArgumentException("The specified mp3 directory does not exist.");
        }

        // Get all the MP3 files from the directory.
        final List<Path> mp3Paths = new ArrayList<>();
        try (DirectoryStream<Path> paths = Files.newDirectoryStream(mp3Directory, "*.mp3")) {
            paths.forEach(path -> {
                System.out.println("Found : " + path.getFileName().toString());
                mp3Paths.add(path);
            });
        }

        //Create a list of songs with all the ID3 tag metadata.
        final List<Song> songs = mp3Paths.stream().map(path -> {
            try {
                final Mp3File mp3file = new Mp3File(String.valueOf(path));
                final ID3v2 id3 = mp3file.getId3v2Tag();

                return new Song(id3.getArtist(), id3.getYear(), id3.getAlbum(), id3.getTitle());

            } catch (IOException | UnsupportedTagException | InvalidDataException e) {
                throw new IllegalStateException(e);
            }
        }).toList();

        //Create a connection to our H2 database and insert all the songs.
        try (Connection conn = DriverManager.getConnection("jdbc:h2:~/mydatabase;AUTO_SERVER=TRUE;INIT=runscript from './create.sql'")) {

            final PreparedStatement preparedStatement = conn.prepareStatement("INSERT INTO SONGS (artist, years, album, title) VALUES (?, ?, ?, ?);");

            for (Song song : songs) {
                preparedStatement.setString(1, song.getArtist());
                preparedStatement.setString(2, song.getYear());
                preparedStatement.setString(3, song.getAlbum());
                preparedStatement.setString(4, song.getTitle());
                preparedStatement.addBatch();
            }

            int[] updates = preparedStatement.executeBatch();
            System.out.println("Inserted " + updates.length + " records into the database");
        }

        //Create a new Jetty HTTP Server and serve our page.
        final Server server = new Server(8080);
        final ServletContextHandler context = new ServletContextHandler(ServletContextHandler.SESSIONS);

        context.setContextPath("/");
        context.setBaseResourceAsString(System.getProperty("java.io.tmpdir"));

        server.setHandler(context);

        context.addServlet(SongServlet.class, "/songs");
        server.start();

        //Load a browser with the web page.
        if (Desktop.isDesktopSupported()) {
            Desktop.getDesktop().browse(new URI("http://localhost:8080/songs"));
        }
    }
}

