package com.utube.utils;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DBConnect {
    public static final String URL = Config.getProperty("DB_URL");
    public static final String USER = Config.getProperty("DB_USER");
    public static final String PASSWORD = Config.getProperty("DB_PASSWORD");
    public static final String DRIVER = Config.getProperty("DB_DRIVER");

    public static Connection getConnection() {
        Connection connection = null;
        try {
            Class.forName(DRIVER);
            connection = DriverManager.getConnection(URL, USER, PASSWORD);
        } catch (ClassNotFoundException | SQLException e) {
            e.printStackTrace();
        }
        return connection;
    }

    public static void closeConnection(Connection conn) {
        try {
            conn.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
