
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
        try (InputStream input =
                DatabaseConnection.class.getClassLoader()
                .getResourceAsStream("db.properties")) {

            if (input == null) {
                throw new RuntimeException(
                    "db.properties file not found!"
                );
            }

            properties.load(input);

            String driver = properties.getProperty("db.driver", "com.mysql.cj.jdbc.Driver");
            Class.forName(driver);

        } catch (ClassNotFoundException e) {
            throw new RuntimeException(
                "MySQL JDBC Driver class not found in classpath: " + e.getMessage(), e
            );
        } catch (IOException e) {
            throw new RuntimeException(
                "Unable to load database configuration", e
            );
        }
    }

    public static Connection getConnection()
            throws SQLException {

        String url = properties.getProperty("db.url");
        String username = properties.getProperty("db.username");
        String password = properties.getProperty("db.password");

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
