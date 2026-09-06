package org.jdbc_lab.statement.dao;

import static org.assertj.core.api.Assertions.assertThat;

import java.sql.Statement;
import java.util.List;
import java.util.Map;
import org.jdbc_lab.connection.ConnectionManager;
import org.jdbc_lab.statement.model.Author;
import org.jdbc_lab.statement.model.Book;
import org.jdbc_lab.test.BaseTest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class BookDaoTest extends BaseTest {

    private BookDao bookDao;
    private AuthorDao authorDao;

    @BeforeEach
    void setUp() throws Exception {
        bookDao = new BookDao();
        authorDao = new AuthorDao();
        try (Statement stmt = ConnectionManager.getConnection().createStatement()) {
            stmt.execute("TRUNCATE TABLE shops_books RESTART IDENTITY CASCADE");
            stmt.execute("TRUNCATE TABLE books RESTART IDENTITY CASCADE");
            stmt.execute("TRUNCATE TABLE authors RESTART IDENTITY CASCADE");
            stmt.execute("TRUNCATE TABLE shops RESTART IDENTITY CASCADE");
        }
    }

    @Test
    void insertAndGetById_shouldWork() {
        Author author = new Author("Лев", "Толстой");
        authorDao.insert(author);
        author = authorDao.getAll().get(0);

        Book book = new Book("Война и мир", author.getId(), 1869);
        bookDao.insert(book);

        List<Book> all = bookDao.getAll();
        assertThat(all).hasSize(1);
        Book saved = all.get(0);
        assertThat(saved.getTitle()).isEqualTo("Война и мир");
        assertThat(saved.getAuthorId()).isEqualTo(author.getId());

        Book byId = bookDao.getById(saved.getId());
        assertThat(byId).usingRecursiveComparison().isEqualTo(saved);
    }

    @Test
    void getBooksPublishedAfterYear_shouldFilter() {
        Author author = new Author("Лев", "Толстой");
        authorDao.insert(author);
        author = authorDao.getAll().get(0);
        bookDao.insert(new Book("Война и мир", author.getId(), 1869));
        bookDao.insert(new Book("Анна Каренина", author.getId(), 1877));

        List<Book> after1870 = bookDao.getBooksPublishedAfterYear(1870);
        assertThat(after1870)
                .hasSize(1)
                .extracting(Book::getTitle)
                .containsExactly("Анна Каренина");
    }

    @Test
    void getBooksByAuthorLastNameStartingWith_shouldFilter() {
        Author author1 = new Author("Лев", "Толстой");
        Author author2 = new Author("Антон", "Чехов");
        authorDao.insert(author1);
        authorDao.insert(author2);
        List<Author> authors = authorDao.getAll();
        Long tolstoyId =
                authors.stream()
                        .filter(a -> a.getLastName().equals("Толстой"))
                        .findFirst()
                        .get()
                        .getId();
        Long chekhovId =
                authors.stream()
                        .filter(a -> a.getLastName().equals("Чехов"))
                        .findFirst()
                        .get()
                        .getId();
        bookDao.insert(new Book("Война и мир", tolstoyId, 1869));
        bookDao.insert(new Book("Чайка", chekhovId, 1896));

        List<Book> books = bookDao.getBooksByAuthorLastNameStartingWith("Т");
        assertThat(books).hasSize(1).extracting(Book::getTitle).containsExactly("Война и мир");
    }

    @Test
    void getBooksGroupedByAuthorWithCount_shouldReturnCounts() {
        Author author = new Author("Лев", "Толстой");
        authorDao.insert(author);
        author = authorDao.getAll().get(0);
        bookDao.insert(new Book("Война и мир", author.getId(), 1869));
        bookDao.insert(new Book("Анна Каренина", author.getId(), 1877));

        List<Map<String, Object>> grouped = bookDao.getBooksGroupedByAuthorWithCount();
        assertThat(grouped).hasSize(1);
        assertThat(grouped.get(0).get("book_count")).isEqualTo(2L);
    }

    @Test
    void updateTitle_shouldUpdate() {
        Author author = new Author("Лев", "Толстой");
        authorDao.insert(author);
        author = authorDao.getAll().get(0);
        bookDao.insert(new Book("Война и мир", author.getId(), 1869));
        Book saved = bookDao.getAll().get(0);

        bookDao.updateTitle(saved.getId(), "Война и мир (новая)");
        Book updated = bookDao.getById(saved.getId());
        assertThat(updated.getTitle()).isEqualTo("Война и мир (новая)");
    }
}
