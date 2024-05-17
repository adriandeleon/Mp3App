package org.example;

import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.sql.*;

public class SongServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        final StringBuilder builder = new StringBuilder();

        try (Connection conn = DriverManager.getConnection("jdbc:h2:~/mydatabase")) {
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

        final String string = "<html><h1>Your Songs</h1><table><tr><th> Year </th><th> Artist </th><th> Album </th><th> Title </th></tr>" + builder + "</table></html>";

        resp.getWriter().write(string);
    }
}
