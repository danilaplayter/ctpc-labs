package org.jdbc_lab.statement.dao;

import static org.assertj.core.api.Assertions.assertThat;

import java.sql.Statement;
import java.util.List;
import org.jdbc_lab.connection.ConnectionManager;
import org.jdbc_lab.statement.model.Author;
import org.jdbc_lab.test.BaseTest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class AuthorDaoTest extends BaseTest {

    private AuthorDao authorDao;

    @BeforeEach
    void setUp() throws Exception {
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

        List<Author> all = authorDao.getAll();
        assertThat(all).hasSize(1);

        Author saved = all.get(0);
        assertThat(saved.getFirstName()).isEqualTo("Лев");
        assertThat(saved.getLastName()).isEqualTo("Толстой");

        Author byId = authorDao.getById(saved.getId());
        assertThat(byId).usingRecursiveComparison().isEqualTo(saved);
    }

    @Test
    void clearTable_shouldDeleteAll() {
        authorDao.insert(new Author("Александр", "Пушкин"));
        authorDao.clearTable();
        assertThat(authorDao.getCount()).isZero();
    }

    @Test
    void getCount_shouldReturnCorrectNumber() {
        assertThat(authorDao.getCount()).isZero();
        authorDao.insert(new Author("Фёдор", "Достоевский"));
        assertThat(authorDao.getCount()).isEqualTo(1);
    }

    @Test
    void getAllSortedByFirstName_shouldOrderCorrectly() {
        authorDao.insert(new Author("Борис", "Пастернак"));
        authorDao.insert(new Author("Антон", "Чехов"));
        List<Author> sorted = authorDao.getAllSortedByFirstName();
        assertThat(sorted).extracting(Author::getFirstName).containsExactly("Антон", "Борис");
    }

    @Test
    void getAllSortedByLastName_shouldOrderCorrectly() {
        authorDao.insert(new Author("Антон", "Чехов"));
        authorDao.insert(new Author("Борис", "Пастернак"));
        List<Author> sorted = authorDao.getAllSortedByLastName();
        assertThat(sorted).extracting(Author::getLastName).containsExactly("Пастернак", "Чехов");
    }

    @Test
    void updateFirstNameByShop_shouldUpdateAuthors() throws Exception {
        try (Statement stmt = ConnectionManager.getConnection().createStatement()) {
            stmt.execute(
                    "INSERT INTO authors (id, first_name, last_name) VALUES (1, 'Иван', 'Иванов')");
            stmt.execute("INSERT INTO books (id, title, author_id) VALUES (1, 'Книга', 1)");
            stmt.execute("INSERT INTO shops (id, name) VALUES (1, 'Магазин')");
            stmt.execute("INSERT INTO shops_books (shop_id, book_id) VALUES (1, 1)");
        }

        authorDao.updateFirstNameByShop(1L, "Пётр");
        Author updated = authorDao.getById(1L);
        assertThat(updated.getFirstName()).isEqualTo("Пётр");
    }
}
