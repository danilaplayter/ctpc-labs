package org.jdbc_lab.connection;

import java.sql.*;
import java.util.logging.Logger;

public class ConnectionManager {
    private static final Logger logger = Logger.getLogger(ConnectionManager.class.getName());
    private static Connection connection = null;

    public static Connection getConnection() throws SQLException {
        try {
            if (connection == null || !connection.isValid(2)) {
                if (connection != null) {
                    try {
                        connection.close();
                    } catch (SQLException ignored) {
                    }
                }
                Class.forName("org.postgresql.Driver");
                String url =
                        System.getProperty("jdbc.url", "jdbc:postgresql://localhost:5432/jdbc_lab");
                String user = System.getProperty("jdbc.user", "postgres");
                String password = System.getProperty("jdbc.password", "postgres");
                connection = DriverManager.getConnection(url, user, password);
                logger.info("Соединение с БД установлено успешно!");
            }
        } catch (ClassNotFoundException e) {
            logger.severe("Драйвер для БД не найден: " + e.getMessage());
            throw new SQLException("Драйвер не найден", e);
        }
        return connection;
    }

    public static void closeConnection() {
        if (connection != null) {
            try {
                connection.close();
                logger.info("Соединение с БД закрыто успешно!");
            } catch (SQLException e) {
                logger.severe("Соединение с БД закрыть не получилось: " + e.getMessage());
            } finally {
                connection = null;
            }
        } else {
            logger.info("Обнаружен запрос закрытия уже закрытого соединения с БД!");
        }
    }
}
