package org.jdbc_lab;

import java.sql.SQLException;
import java.util.logging.Logger;
import org.jdbc_lab.connection.ConnectionManager;
import org.jdbc_lab.menus.MainMenu;

public class Main {
    private static final Logger logger = Logger.getLogger(Main.class.getName());

    public static void main(String[] args) {
        try {
            ConnectionManager.getConnection();
        } catch (SQLException e) {
            logger.severe("Не удалось подключиться к БД: " + e.getMessage());
            return;
        }

        try {
            new MainMenu().print();
        } catch (Exception e) {
            logger.severe("Ошибка при обработке главного меню: " + e.getMessage());
        } finally {
            ConnectionManager.closeConnection();
        }
    }
}
