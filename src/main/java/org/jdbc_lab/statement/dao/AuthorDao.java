package org.jdbc_lab.statement.dao;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;
import org.jdbc_lab.connection.ConnectionManager;
import org.jdbc_lab.statement.model.Author;

public class AuthorDao implements StatementDao<Author> {
    private static final Logger logger = Logger.getLogger(AuthorDao.class.getName());

    @Override
    public void clearTable() {
        try (Statement stmt = ConnectionManager.getConnection().createStatement()) {
            stmt.executeUpdate("DELETE FROM authors");
        } catch (SQLException e) {
            logger.severe("Ошибка: " + e.getMessage());
            throw new RuntimeException(e);
        }
    }

    @Override
    public void insert(Author author) {
        try (Statement stmt = ConnectionManager.getConnection().createStatement()) {
            stmt.executeUpdate(
                    "INSERT INTO authors (first_name, last_name) VALUES ('"
                            + author.getFirstName()
                            + "', '"
                            + author.getLastName()
                            + "')");
        } catch (SQLException e) {
            logger.severe("Ошибка: " + e.getMessage());
            throw new RuntimeException(e);
        }
    }

    @Override
    public int getCount() {
        int count = 0;
        try (Statement stmt = ConnectionManager.getConnection().createStatement()) {
            ResultSet rs = stmt.executeQuery("SELECT COUNT(*) FROM authors");
            if (rs.next()) count = rs.getInt(1);
        } catch (SQLException e) {
            logger.severe("Ошибка: " + e.getMessage());
            throw new RuntimeException(e);
        }
        return count;
    }

    @Override
    public List<Author> getAll() {
        List<Author> authors = new ArrayList<>();
        try (Statement stmt = ConnectionManager.getConnection().createStatement();
                ResultSet rs = stmt.executeQuery("SELECT * FROM authors")) {
            while (rs.next()) {
                Author a = new Author();
                a.setId(rs.getLong("id"));
                a.setFirstName(rs.getString("first_name"));
                a.setLastName(rs.getString("last_name"));
                authors.add(a);
            }
        } catch (SQLException e) {
            logger.severe("Ошибка: " + e.getMessage());
            throw new RuntimeException(e);
        }
        return authors;
    }

    @Override
    public Author getById(Long id) {
        Author author = null;
        try (Statement stmt = ConnectionManager.getConnection().createStatement();
                ResultSet rs = stmt.executeQuery("SELECT * FROM authors WHERE id = " + id)) {
            if (rs.next()) {
                author = new Author();
                author.setId(rs.getLong("id"));
                author.setFirstName(rs.getString("first_name"));
                author.setLastName(rs.getString("last_name"));
            }
        } catch (SQLException e) {
            logger.severe("Ошибка: " + e.getMessage());
            throw new RuntimeException(e);
        }
        return author;
    }

    // Дополнительные методы для задания

    public List<Author> getAllSortedByFirstName() {
        List<Author> authors = new ArrayList<>();
        try (Statement stmt = ConnectionManager.getConnection().createStatement();
                ResultSet rs = stmt.executeQuery("SELECT * FROM authors ORDER BY first_name")) {
            while (rs.next()) {
                Author a = new Author();
                a.setId(rs.getLong("id"));
                a.setFirstName(rs.getString("first_name"));
                a.setLastName(rs.getString("last_name"));
                authors.add(a);
            }
        } catch (SQLException e) {
            logger.severe("Ошибка: " + e.getMessage());
            throw new RuntimeException(e);
        }
        return authors;
    }

    public List<Author> getAllSortedByLastName() {
        List<Author> authors = new ArrayList<>();
        try (Statement stmt = ConnectionManager.getConnection().createStatement();
                ResultSet rs = stmt.executeQuery("SELECT * FROM authors ORDER BY last_name")) {
            while (rs.next()) {
                Author a = new Author();
                a.setId(rs.getLong("id"));
                a.setFirstName(rs.getString("first_name"));
                a.setLastName(rs.getString("last_name"));
                authors.add(a);
            }
        } catch (SQLException e) {
            logger.severe("Ошибка: " + e.getMessage());
            throw new RuntimeException(e);
        }
        return authors;
    }

    public void updateFirstNameByShop(Long shopId, String newFirstName) {
        String query =
                "UPDATE authors SET first_name = '"
                        + newFirstName
                        + "' "
                        + "WHERE id IN (SELECT author_id FROM books "
                        + "WHERE id IN (SELECT book_id FROM shops_books WHERE shop_id = "
                        + shopId
                        + "))";
        try (Statement stmt = ConnectionManager.getConnection().createStatement()) {
            stmt.executeUpdate(query);
        } catch (SQLException e) {
            logger.severe("Ошибка: " + e.getMessage());
            throw new RuntimeException(e);
        }
    }
}
