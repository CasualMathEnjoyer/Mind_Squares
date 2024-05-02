package mySquareApp;

import java.awt.*;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class SQL {
    private static Connection connect() throws SQLException {
        return DriverManager.getConnection(
                        "jdbc:postgresql://localhost:5432/db",
                        "username",
                        "password");
    }
    public static boolean saveSquares(List<Square> squares){
        try (Connection conn = connect()) {

            try (Statement statement = conn.createStatement()) {
                // Execute the DELETE operation
                int rowsDeleted = statement.executeUpdate("DELETE FROM squares");

                // Output the number of rows deleted (optional)
                System.out.println("Rows deleted: " + rowsDeleted);
            }
            System.out.println("here");
            for (Square square : squares) {
                // Use PreparedStatement to execute INSERT statement
                String insertQuery = "INSERT INTO squares (x, y, size, text, color_hex) VALUES (?, ?, ?, ?, ?)";
                try (PreparedStatement insertStatement = conn.prepareStatement(insertQuery)) {
//                    insertStatement.setInt(1, square.squareID);
                    insertStatement.setInt(1, square.getX());
                    insertStatement.setInt(2, square.getY());
                    insertStatement.setInt(3, square.getSize());
                    insertStatement.setString(4, square.getText());
                    insertStatement.setString(5, square.getColor());

                    // Execute the INSERT statement
                    insertStatement.executeUpdate();
                }
            }
            return true;

        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }
    public static boolean saveConnections(List<List<Point>> connections) {
        try (Connection conn = connect()) {

            try (Statement statement = conn.createStatement()) {
                // Execute the DELETE operation
                int rowsDeleted = statement.executeUpdate("DELETE FROM connections");

                // Output the number of rows deleted (optional)
                System.out.println("Rows deleted: " + rowsDeleted);
            }

            for (List<Point> connection : connections) {
                // Use PreparedStatement to execute INSERT statement
                String insertQuery = "INSERT INTO connections (point1_x, point1_y, point2_x, point2_y) VALUES (?, ?, ?, ?)";
                try (PreparedStatement insertStatement = conn.prepareStatement(insertQuery)) {
                    Point point1 = connection.get(0);
                    Point point2 = connection.get(1);

                    insertStatement.setInt(1, point1.x);
                    insertStatement.setInt(2, point1.y);
                    insertStatement.setInt(3, point2.x);
                    insertStatement.setInt(4, point2.y);

                    // Execute the INSERT statement
                    insertStatement.executeUpdate();
                }
            }
            return true;

        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }
    public static List<Square> getSquares(){
        List<Square> squares = new ArrayList<>();
        try (Connection conn = connect()) {
            PreparedStatement selectStatement = conn.prepareStatement("select * from squares");
            ResultSet rs = selectStatement.executeQuery();

            while (rs.next()) { // will traverse through all rows
//                int SquareID = rs.getInt("id");
                int x = rs.getInt("x");
                int y = rs.getInt("y");
                int size = rs.getInt("size");
                String text = rs.getString("text");
                String color = rs.getString("color_hex");

                Square square = new Square(x, y, size, text, color);
                squares.add(square);
            }

        } catch (final SQLException e) {
            e.printStackTrace();
        }
        return squares;
    }
    public static List<List<Point>> getConnections() {
        List<List<Point>> loadedConnections = new ArrayList<>();

        try (Connection conn = connect()) {

            PreparedStatement selectStatement = conn.prepareStatement("SELECT * FROM connections");
            ResultSet rs = selectStatement.executeQuery();

            while (rs.next()) {
                int point1_x = rs.getInt("point1_x");
                int point1_y = rs.getInt("point1_y");
                int point2_x = rs.getInt("point2_x");
                int point2_y = rs.getInt("point2_y");

                Point point1 = new Point(point1_x, point1_y);
                Point point2 = new Point(point2_x, point2_y);

                List<Point> connection = new ArrayList<>();
                connection.add(point1);
                connection.add(point2);

                loadedConnections.add(connection);
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return loadedConnections;
    }
}
