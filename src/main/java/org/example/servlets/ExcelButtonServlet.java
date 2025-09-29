package org.example.servlets;

import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.example.GlobalConstants;

import java.io.IOException;
import java.io.OutputStream;
import java.sql.*;

/// A servlet that generates an Excel file populated with data from a database query
/// and serves it as a downloadable response to the client.
public class ExcelButtonServlet extends HttpServlet {
    @Override
    protected void doGet(final HttpServletRequest httpServletRequest,
                         final HttpServletResponse httpServletResponse) throws IOException {

        try (Connection conn = DriverManager.getConnection(GlobalConstants.JDBC_CONNECTION)) {
            final Statement statement = conn.createStatement();
            final ResultSet resultSet = statement.executeQuery("SELECT * FROM SONGS");

            createExcelFile(httpServletResponse, resultSet);

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    /// Generates an Excel file populated with data from the provided ResultSet and writes the file
    /// to the HTTP response output stream for client download.
    ///
    /// @param httpServletResponse the HTTP response object to which the generated Excel file is written
    /// @param resultSet the ResultSet containing data to be written into the Excel file
    /// @return the HTTP response with the Excel file content
    /// @throws IOException if an I/O error occurs during file writing
    /// @throws SQLException if an error occurs while accessing the ResultSet
    protected HttpServletResponse createExcelFile(final HttpServletResponse httpServletResponse,
                                                  final ResultSet resultSet) throws IOException, SQLException {
        int currentRowIndex = 0;
        final Workbook workbook = new XSSFWorkbook();
        final Sheet sheet = workbook.createSheet("Music");

        createHeaderRow(sheet, currentRowIndex);

        while (resultSet.next()) {
            currentRowIndex++;

            final Row row = sheet.createRow(currentRowIndex);

            createCellWithValue(row, 0, resultSet.getString("years"));
            createCellWithValue(row, 1, resultSet.getString("artist"));
            createCellWithValue(row, 2, resultSet.getString("album"));
            createCellWithValue(row, 3, resultSet.getString("title"));
        }

        httpServletResponse.setContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
        httpServletResponse.setHeader("Content-Disposition", "attachment; filename=\"simpleExcel.xlsx\"");

        try (OutputStream out = httpServletResponse.getOutputStream()) {
            workbook.write(out);
        } finally {
            workbook.close();
        }

        return httpServletResponse;
    }

    /// Creates a header row in the given sheet at the specified row index with predefined column names.
    ///
    /// @param sheet the sheet where the header row will be created
    /// @param rowIndex the index of the row where the header will be created
    private void createHeaderRow(final Sheet sheet, int rowIndex) {
        final Row headerRow = sheet.createRow(rowIndex);

        createCellWithValue(headerRow, 0, "Year");
        createCellWithValue(headerRow, 1, "Artist");
        createCellWithValue(headerRow, 2, "Album");
        createCellWithValue(headerRow, 3, "Title");
        createCellWithValue(headerRow, 4, "Video");
        createCellWithValue(headerRow, 5, "Lyrics");
    }

    /// Creates a cell at the specified index in the given row and sets its value.
    ///
    /// @param row the row in which the cell will be created
    /// @param cellIndex the index at which the cell will be created
    /// @param value the value to set within the created cell
    private void createCellWithValue(final Row row, int cellIndex, final String value) {
        final Cell cell = row.createCell(cellIndex);
        cell.setCellValue(value);
    }
}
