package org.example.servlets;

import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.example.GlobalConstants;

import java.io.IOException;
import java.sql.*;

public class ButtonServlet extends HttpServlet {
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse httpServletResponse) throws IOException {

        try (Connection conn = DriverManager.getConnection(GlobalConstants.JDBC_CONNECTION)) {
            final Statement statement = conn.createStatement();
            final ResultSet resultSet = statement.executeQuery("SELECT * FROM SONGS");

            while (resultSet.next()) {
                httpServletResponse.getWriter().write("<p>hello</p>");
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
}
