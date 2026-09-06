package org.jdbc_lab.statement.dao;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;
import org.jdbc_lab.connection.ConnectionManager;
import org.jdbc_lab.statement.model.ShopBook;

public class ShopBookDao implements StatementDao<ShopBook> {
    private static final Logger logger = Logger.getLogger(ShopBookDao.class.getName());

    @Override
    public void clearTable() {
        try (Statement stmt = ConnectionManager.getConnection().createStatement()) {
            stmt.executeUpdate("DELETE FROM shops_books");
        } catch (SQLException e) {
            logger.severe("Ошибка: " + e.getMessage());
            throw new RuntimeException(e);
        }
    }

    @Override
    public void insert(ShopBook shopBook) {
        try (Statement stmt = ConnectionManager.getConnection().createStatement()) {
            stmt.executeUpdate(
                    "INSERT INTO shops_books (shop_id, book_id) VALUES ("
                            + shopBook.getShopId()
                            + ", "
                            + shopBook.getBookId()
                            + ")");
        } catch (SQLException e) {
            logger.severe("Ошибка: " + e.getMessage());
            throw new RuntimeException(e);
        }
    }

    @Override
    public int getCount() {
        int count = 0;
        try (Statement stmt = ConnectionManager.getConnection().createStatement();
                ResultSet rs = stmt.executeQuery("SELECT COUNT(*) FROM shops_books")) {
            if (rs.next()) count = rs.getInt(1);
        } catch (SQLException e) {
            logger.severe("Ошибка: " + e.getMessage());
            throw new RuntimeException(e);
        }
        return count;
    }

    @Override
    public List<ShopBook> getAll() {
        List<ShopBook> shopBooks = new ArrayList<>();
        try (Statement stmt = ConnectionManager.getConnection().createStatement();
                ResultSet rs = stmt.executeQuery("SELECT * FROM shops_books")) {
            while (rs.next()) {
                ShopBook sb = new ShopBook();
                sb.setId(rs.getLong("id"));
                sb.setShopId(rs.getLong("shop_id"));
                sb.setBookId(rs.getLong("book_id"));
                shopBooks.add(sb);
            }
        } catch (SQLException e) {
            logger.severe("Ошибка: " + e.getMessage());
            throw new RuntimeException(e);
        }
        return shopBooks;
    }

    @Override
    public ShopBook getById(Long id) {
        ShopBook shopBook = null;
        try (Statement stmt = ConnectionManager.getConnection().createStatement();
                ResultSet rs = stmt.executeQuery("SELECT * FROM shops_books WHERE id = " + id)) {
            if (rs.next()) {
                shopBook = new ShopBook();
                shopBook.setId(rs.getLong("id"));
                shopBook.setShopId(rs.getLong("shop_id"));
                shopBook.setBookId(rs.getLong("book_id"));
            }
        } catch (SQLException e) {
            logger.severe("Ошибка: " + e.getMessage());
            throw new RuntimeException(e);
        }
        return shopBook;
    }

    // Дополнительный метод для получения случайной книги в магазине
    public Long getRandomBookIdByShop(Long shopId) {
        String query =
                "SELECT book_id FROM shops_books WHERE shop_id = "
                        + shopId
                        + " ORDER BY RANDOM() LIMIT 1";
        try (Statement stmt = ConnectionManager.getConnection().createStatement();
                ResultSet rs = stmt.executeQuery(query)) {
            if (rs.next()) {
                return rs.getLong("book_id");
            }
        } catch (SQLException e) {
            logger.severe("Ошибка: " + e.getMessage());
            throw new RuntimeException(e);
        }
        return null;
    }
}
