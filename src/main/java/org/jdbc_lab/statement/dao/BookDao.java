package org.jdbc_lab.statement.dao;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;
import org.jdbc_lab.connection.ConnectionManager;
import org.jdbc_lab.statement.model.Book;

public class BookDao implements StatementDao<Book> {
    private static final Logger logger = Logger.getLogger(BookDao.class.getName());

    @Override
    public void clearTable() {
        try (Statement stmt = ConnectionManager.getConnection().createStatement()) {
            stmt.executeUpdate("DELETE FROM books");
        } catch (SQLException e) {
            logger.severe("Ошибка: " + e.getMessage());
            throw new RuntimeException(e);
        }
    }

    @Override
    public void insert(Book book) {
        try (Statement stmt = ConnectionManager.getConnection().createStatement()) {
            stmt.executeUpdate(
                    "INSERT INTO books (title, author_id, year_of_publication) VALUES ('"
                            + book.getTitle()
                            + "', "
                            + book.getAuthorId()
                            + ", "
                            + book.getYearOfPublication()
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
                ResultSet rs = stmt.executeQuery("SELECT COUNT(*) FROM books")) {
            if (rs.next()) count = rs.getInt(1);
        } catch (SQLException e) {
            logger.severe("Ошибка: " + e.getMessage());
            throw new RuntimeException(e);
        }
        return count;
    }

    @Override
    public List<Book> getAll() {
        List<Book> books = new ArrayList<>();
        try (Statement stmt = ConnectionManager.getConnection().createStatement();
                ResultSet rs = stmt.executeQuery("SELECT * FROM books")) {
            while (rs.next()) {
                Book b = new Book();
                b.setId(rs.getLong("id"));
                b.setTitle(rs.getString("title"));
                b.setAuthorId(rs.getLong("author_id"));
                b.setYearOfPublication(rs.getInt("year_of_publication"));
                books.add(b);
            }
        } catch (SQLException e) {
            logger.severe("Ошибка: " + e.getMessage());
            throw new RuntimeException(e);
        }
        return books;
    }

    @Override
    public Book getById(Long id) {
        Book book = null;
        try (Statement stmt = ConnectionManager.getConnection().createStatement();
                ResultSet rs = stmt.executeQuery("SELECT * FROM books WHERE id = " + id)) {
            if (rs.next()) {
                book = new Book();
                book.setId(rs.getLong("id"));
                book.setTitle(rs.getString("title"));
                book.setAuthorId(rs.getLong("author_id"));
                book.setYearOfPublication(rs.getInt("year_of_publication"));
            }
        } catch (SQLException e) {
            logger.severe("Ошибка: " + e.getMessage());
            throw new RuntimeException(e);
        }
        return book;
    }

    // Дополнительные методы для задания

    public List<Book> getBooksPublishedAfterYear(int year) {
        List<Book> books = new ArrayList<>();
        try (Statement stmt = ConnectionManager.getConnection().createStatement();
                ResultSet rs =
                        stmt.executeQuery(
                                "SELECT * FROM books WHERE year_of_publication > "
                                        + year
                                        + " ORDER BY year_of_publication")) {
            while (rs.next()) {
                Book b = new Book();
                b.setId(rs.getLong("id"));
                b.setTitle(rs.getString("title"));
                b.setAuthorId(rs.getLong("author_id"));
                b.setYearOfPublication(rs.getInt("year_of_publication"));
                books.add(b);
            }
        } catch (SQLException e) {
            logger.severe("Ошибка: " + e.getMessage());
            throw new RuntimeException(e);
        }
        return books;
    }

    public List<Book> getBooksByAuthorLastNameStartingWith(String prefix) {
        List<Book> books = new ArrayList<>();
        String query =
                "SELECT b.* FROM books b JOIN authors a ON b.author_id = a.id "
                        + "WHERE a.last_name LIKE '"
                        + prefix
                        + "%'";
        try (Statement stmt = ConnectionManager.getConnection().createStatement();
                ResultSet rs = stmt.executeQuery(query)) {
            while (rs.next()) {
                Book b = new Book();
                b.setId(rs.getLong("id"));
                b.setTitle(rs.getString("title"));
                b.setAuthorId(rs.getLong("author_id"));
                b.setYearOfPublication(rs.getInt("year_of_publication"));
                books.add(b);
            }
        } catch (SQLException e) {
            logger.severe("Ошибка: " + e.getMessage());
            throw new RuntimeException(e);
        }
        return books;
    }

    public List<Map<String, Object>> getBooksGroupedByAuthorWithCount() {
        List<Map<String, Object>> result = new ArrayList<>();
        String query =
                "SELECT a.id, a.first_name, a.last_name, COUNT(b.id) AS book_count "
                        + "FROM authors a LEFT JOIN books b ON a.id = b.author_id GROUP BY a.id";
        try (Statement stmt = ConnectionManager.getConnection().createStatement();
                ResultSet rs = stmt.executeQuery(query)) {
            while (rs.next()) {
                Map<String, Object> row = new HashMap<>();
                row.put("id", rs.getLong("id"));
                row.put("first_name", rs.getString("first_name"));
                row.put("last_name", rs.getString("last_name"));
                row.put("book_count", rs.getLong("book_count"));
                result.add(row);
            }
        } catch (SQLException e) {
            logger.severe("Ошибка: " + e.getMessage());
            throw new RuntimeException(e);
        }
        return result;
    }

    public void updateTitle(Long bookId, String newTitle) {
        try (Statement stmt = ConnectionManager.getConnection().createStatement()) {
            stmt.executeUpdate("UPDATE books SET title = '" + newTitle + "' WHERE id = " + bookId);
        } catch (SQLException e) {
            logger.severe("Ошибка: " + e.getMessage());
            throw new RuntimeException(e);
        }
    }
}
