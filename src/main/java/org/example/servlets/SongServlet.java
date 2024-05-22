package org.example.servlets;

import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import org.example.GlobalConstants;

import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.sql.*;

public class SongServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse httpServletResponse) throws IOException {
        final StringBuilder tableRowData = new StringBuilder();
        String tableData = "";

        try (Connection conn = DriverManager.getConnection(GlobalConstants.JDBC_CONNECTION)) {
            final Statement statement = conn.createStatement();
            final ResultSet resultSet = statement.executeQuery("SELECT * FROM SONGS");

            while (resultSet.next()) {

                final String year = resultSet.getString("years");
                final String artist = resultSet.getString("artist");
                final String album = resultSet.getString("album");
                final String songTitle = resultSet.getString("title");

                final String yearUrl = "https://en.wikipedia.org/wiki/" + year + "_in_music";
                final String artistUrl = "https://www.last.fm/music/" + artist;
                final String albumUrl = artistUrl + "/" + album;
                final String songTitleUrl = albumUrl + "/" + songTitle;

                tableRowData.append("<tr class=\"table\">")
                        .append("<td>").append("<a href=\"" + yearUrl + "\"" + ">" + year + "</a>").append("</td>")
                        .append("<td>").append("<a href=\"" + artistUrl + "\"" + ">" + artist + "</a>").append("</td>")
                        .append("<td>").append("<a href=\"" + albumUrl + "\"" + ">" + album + "</a>").append("</td>")
                        .append("<td>").append("<a href=\"" + songTitleUrl + "\"" + ">" + songTitle + "</a>").append("</td>")
                        .append("</tr>");

                tableData = """
                         <table class="table table-striped table-hover table-bordered">
                                <thead class="table-dark">
                                <tr>
                                    <th scope="col"> Year</th>
                                    <th scope="col"> Artist</th>
                                    <th scope="col"> Album</th>
                                    <th scope="col"> Title</th>
                                    <th scope="col"> Video</th>
                                    <th scope="col"> Lyrics</th>
                                </tr>
                        """
                        + tableRowData +
                        """
                                    </thead>
                                    </table>
                        """;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        // Assuming your file is located in the src/main/resources folder.
        final String fileName = "templates/songs.html";

        // Get the URL of the file
        final URL resource = SongServlet.class.getClassLoader().getResource(fileName);

        if (resource == null) {
            System.out.println("File not found!");

            return;
        }

        String htmlString = "";
        try {
            // Convert URL to Path
            final Path filePath = Path.of(resource.toURI());

            // Read file content as String
            htmlString = Files.readString(filePath);

        } catch (IOException | URISyntaxException e) {
            e.printStackTrace();
        }

        final String finalHtmlString = htmlString.replace("<!--content goes here.-->", tableData);

        httpServletResponse.getWriter().write(finalHtmlString);
    }
}
