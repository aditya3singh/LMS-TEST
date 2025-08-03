package utils;

import java.sql.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class DatabaseUtils {
    private Connection connection;
    private ConfigReader config;

    public DatabaseUtils() {
        this.config = new ConfigReader();
        connectToDatabase();
    }

    private void connectToDatabase() {
        try {
            String url = config.getProperty("db.url");
            String username = config.getProperty("db.username");
            String password = config.getProperty("db.password");

            connection = DriverManager.getConnection(url, username, password);
            System.out.println("Database connection established");
        } catch (SQLException e) {
            throw new RuntimeException("Failed to connect to database", e);
        }
    }

    public List<Map<String, Object>> executeQuery(String query) {
        List<Map<String, Object>> results = new ArrayList<>();

        try (Statement stmt = connection.createStatement();
             ResultSet rs = stmt.executeQuery(query)) {

            ResultSetMetaData metaData = rs.getMetaData();
            int columnCount = metaData.getColumnCount();

            while (rs.next()) {
                Map<String, Object> row = new HashMap<>();
                for (int i = 1; i <= columnCount; i++) {
                    row.put(metaData.getColumnName(i), rs.getObject(i));
                }
                results.add(row);
            }
        } catch (SQLException e) {
            throw new RuntimeException("Failed to execute query: " + query, e);
        }

        return results;
    }

    public boolean validateUserExists(String username) {
        String query = "SELECT COUNT(*) as count FROM users WHERE username = '" + username + "'";
        List<Map<String, Object>> results = executeQuery(query);
        Long count = (Long) results.get(0).get("count");
        return count > 0;
    }

    public boolean validateCourseEnrollment(String username, String courseId) {
        String query = "SELECT COUNT(*) as count FROM enrollments e " +
                "JOIN users u ON e.user_id = u.id " +
                "WHERE u.username = '" + username + "' AND e.course_id = " + courseId;
        List<Map<String, Object>> results = executeQuery(query);
        Long count = (Long) results.get(0).get("count");
        return count > 0;
    }

    public void closeConnection() {
        try {
            if (connection != null && !connection.isClosed()) {
                connection.close();
                System.out.println("Database connection closed");
            }
        } catch (SQLException e) {
            System.err.println("Failed to close database connection: " + e.getMessage());
        }
    }
}