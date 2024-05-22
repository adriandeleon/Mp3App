package org.example;

import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.sql.*;

public class SongServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        final StringBuilder builder = new StringBuilder();

        try (Connection conn = DriverManager.getConnection(GlobalConstants.JDBC_CONNECTION)) {
            final Statement statement = conn.createStatement();
            final ResultSet resultSet = statement.executeQuery("SELECT * FROM SONGS");

            while (resultSet.next()) {
                builder.append("<tr class=\"table\">")
                        .append("<td>").append(resultSet.getString("years")).append("</td>")
                        .append("<td>").append(resultSet.getString("artist")).append("</td>")
                        .append("<td>").append(resultSet.getString("album")).append("</td>")
                        .append("<td>").append(resultSet.getString("title")).append("</td>")
                        .append("</tr>");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        // Assuming your file is located in the src/main/resources folder.
        String fileName = "templates/songs.html";

        // Get the URL of the file
        URL resource = SongServlet.class.getClassLoader().getResource(fileName);

        if (resource == null) {
            System.out.println("File not found!");

            return;
        }

        String htmlString = "";
        try {
            // Convert URL to Path
            Path filePath = Path.of(resource.toURI());
            // Read file content as String
            htmlString = Files.readString(filePath);
        } catch (IOException | URISyntaxException e) {
            e.printStackTrace();
        }

        final String finalHtmlString = htmlString.replace("<!--content goes here.-->", builder);

        resp.getWriter().write(finalHtmlString);
    }
}
