package org.jdbc_lab.statement.action;

import static org.assertj.core.api.Assertions.assertThat;

import java.sql.Statement;
import org.jdbc_lab.connection.ConnectionManager;
import org.jdbc_lab.statement.dao.*;
import org.jdbc_lab.statement.model.Author;
import org.jdbc_lab.statement.model.Shop;
import org.jdbc_lab.test.BaseTest;
import org.jdbc_lab.test.TestUtils;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class StatementActionTest extends BaseTest {

    private StatementAction action;
    private AuthorDao authorDao;
    private BookDao bookDao;
    private ShopDao shopDao;
    private ShopBookDao shopBookDao;

    @BeforeEach
    void setUp() throws Exception {
        action = new StatementAction();
        authorDao = new AuthorDao();
        bookDao = new BookDao();
        shopDao = new ShopDao();
        shopBookDao = new ShopBookDao();
        try (Statement stmt = ConnectionManager.getConnection().createStatement()) {
            stmt.execute("TRUNCATE TABLE shops_books RESTART IDENTITY CASCADE");
            stmt.execute("TRUNCATE TABLE books RESTART IDENTITY CASCADE");
            stmt.execute("TRUNCATE TABLE authors RESTART IDENTITY CASCADE");
            stmt.execute("TRUNCATE TABLE shops RESTART IDENTITY CASCADE");
        }
    }

    @Test
    void addDefaultInfo_shouldInsertData() {
        action.addDefaultInfo();
        assertThat(authorDao.getCount()).isEqualTo(4);
        assertThat(bookDao.getCount()).isEqualTo(8);
        assertThat(shopDao.getCount()).isEqualTo(3);
        assertThat(shopBookDao.getCount()).isEqualTo(8);
    }

    @Test
    void deleteAllInfo_shouldClearTables_WhenConfirmed() {
        action.addDefaultInfo();
        TestUtils.provideInput("y\n");
        action.deleteAllInfo();
        assertThat(authorDao.getCount()).isZero();
        assertThat(bookDao.getCount()).isZero();
        assertThat(shopDao.getCount()).isZero();
        assertThat(shopBookDao.getCount()).isZero();
    }

    @Test
    void deleteAllInfo_shouldAbort_WhenNotConfirmed() {
        action.addDefaultInfo();
        TestUtils.provideInput("n\n");
        action.deleteAllInfo();
        assertThat(authorDao.getCount()).isEqualTo(4); // ничего не удалилось
    }

    @Test
    void getAllInfo_shouldNotThrow() {
        // Просто проверяем, что метод не падает
        action.getAllInfo();
    }

    @Test
    void updateRandomBookInShop_shouldUpdateBookTitle() {
        // Сначала добавим дефолтные данные
        action.addDefaultInfo();
        // Найдём ID магазина "Белкнига" и книгу, связанную с ним
        Shop shop =
                shopDao.getAll().stream()
                        .filter(s -> s.getName().equals("Белкнига"))
                        .findFirst()
                        .orElseThrow();
        // Убедимся, что в магазине есть книги
        assertThat(shopBookDao.getAll()).isNotEmpty();

        // Эмулируем ввод: ID магазина и новое название
        TestUtils.provideInput(shop.getId() + "\nНовое название\n");
        action.updateRandomBookInShop();

        // Проверим, что хотя бы одна книга изменила название
        boolean anyUpdated =
                bookDao.getAll().stream().anyMatch(b -> b.getTitle().equals("Новое название"));
        assertThat(anyUpdated).isTrue();
    }

    @Test
    void updateAuthorsNamesByShop_shouldUpdateAuthorFirstName() throws Exception {
        // Подготовим данные: автор, книга, магазин
        try (Statement stmt = ConnectionManager.getConnection().createStatement()) {
            stmt.execute(
                    "INSERT INTO authors (id, first_name, last_name) VALUES (1, 'Иван', 'Иванов')");
            stmt.execute("INSERT INTO books (id, title, author_id) VALUES (1, 'Книга', 1)");
            stmt.execute("INSERT INTO shops (id, name) VALUES (1, 'Магазин')");
            stmt.execute("INSERT INTO shops_books (shop_id, book_id) VALUES (1, 1)");
        }

        TestUtils.provideInput("1\nПётр\n");
        action.updateAuthorsNamesByShop();

        Author updated = authorDao.getById(1L);
        assertThat(updated.getFirstName()).isEqualTo("Пётр");
    }
}
