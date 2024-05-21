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

        // Update HTML with Bootstrap, maybe.
        final String htmlString = """
                <!DOCTYPE html>
                <html lang="en">
                <head>
                    <meta charset="UTF-8">
                    <meta name="viewport" content="width=device-width, initial-scale=1.0">
                    <title>Bootstrap 5 Example</title>
                    <!-- Bootstrap CSS -->
                    <link href="https://cdnjs.cloudflare.com/ajax/libs/bootstrap/5.3.3/css/bootstrap.min.css" rel="stylesheet">
                </head>
                <body>
                  <div class="container mt-5">
                        <h2 class="mb-4">Your songs</h2>
                        <table  class="table table-striped table-hover table-bordered">
                        <thead class="table-dark">
                        <tr>
                           <th scope="col"> Year </th>
                           <th scope="col"> Artist </th>
                           <th scope="col"> Album </th>
                           <th scope="col"> Title </th>
                        </tr>
                """
                + builder +
                """
                        </table>
                        <!-- Bootstrap JS and dependencies -->
                        <script src="https://cdnjs.cloudflare.com/ajax/libs/bootstrap/5.3.3/js/bootstrap.bundle.min.js"></script>
                        </body>
                        </html>
                        """;
        resp.getWriter().write(htmlString);
    }
}
