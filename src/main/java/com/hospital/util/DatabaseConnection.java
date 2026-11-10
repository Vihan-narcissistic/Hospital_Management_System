
package com.hospital.util;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.ResultSet;
import java.sql.Statement;
import java.io.InputStream;
import java.io.IOException;
import java.util.Properties;

public class DatabaseConnection {

    private static final Properties properties = new Properties();

    static {
        // Load local configuration if available
        try (InputStream input =
                DatabaseConnection.class.getClassLoader()
                .getResourceAsStream("db.properties")) {

            if (input != null) {
                properties.load(input);
            }

            // Load MySQL JDBC driver
            String driver = getConfig(
                    "DB_DRIVER",
                    "db.driver",
                    "com.mysql.cj.jdbc.Driver"
            );

            Class.forName(driver);

        } catch (ClassNotFoundException e) {
            throw new RuntimeException(
                    "MySQL JDBC Driver not found", e
            );
        } catch (IOException e) {
            throw new RuntimeException(
                    "Unable to load database configuration", e
            );
        }
    }

    // Read environment variable first, then local properties
    private static String getConfig(
            String environmentVariable,
            String propertyName,
            String defaultValue) {

        String value = System.getenv(environmentVariable);

        if (value == null || value.isBlank()) {
            value = properties.getProperty(propertyName);
        }

        if (value == null || value.isBlank()) {
            return defaultValue;
        }

        return value.trim();
    }

    // Establish MySQL connection
    public static Connection getConnection()
            throws SQLException {

        String url = getConfig("DB_URL", "db.url", null);
        String username = getConfig(
                "DB_USERNAME", "db.username", null
        );
        String password = getConfig(
                "DB_PASSWORD", "db.password", null
        );

        if (url == null || username == null || password == null) {
            throw new SQLException(
                    "Database configuration is missing. " +
                    "Check DB_URL, DB_USERNAME and DB_PASSWORD."
            );
        }

        return DriverManager.getConnection(
                url, username, password
        );
    }

    // Test JDBC connection
    public static void main(String[] args) {

        try (
                Connection connection = getConnection();
                Statement statement = connection.createStatement();
                ResultSet result = statement.executeQuery(
                        "SELECT DATABASE(), VERSION()"
                )
        ) {

            if (result.next()) {
                System.out.println(
                        "MySQL Connection Successful!"
                );

                System.out.println(
                        "Database: " + result.getString(1)
                );

                System.out.println(
                        "MySQL Version: " + result.getString(2)
                );
            }

        } catch (SQLException e) {
            System.out.println(
                    "Database Connection Failed!"
            );

            e.printStackTrace();
        }
    }
}
