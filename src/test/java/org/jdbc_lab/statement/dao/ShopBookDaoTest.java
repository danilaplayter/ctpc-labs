package org.jdbc_lab.statement.dao;

import static org.assertj.core.api.Assertions.assertThat;

import java.sql.Statement;
import java.util.List;
import org.jdbc_lab.connection.ConnectionManager;
import org.jdbc_lab.statement.model.Author;
import org.jdbc_lab.statement.model.Book;
import org.jdbc_lab.statement.model.Shop;
import org.jdbc_lab.statement.model.ShopBook;
import org.jdbc_lab.test.BaseTest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class ShopBookDaoTest extends BaseTest {

    private ShopBookDao shopBookDao;
    private AuthorDao authorDao;
    private BookDao bookDao;
    private ShopDao shopDao;

    @BeforeEach
    void setUp() throws Exception {
        shopBookDao = new ShopBookDao();
        authorDao = new AuthorDao();
        bookDao = new BookDao();
        shopDao = new ShopDao();
        try (Statement stmt = ConnectionManager.getConnection().createStatement()) {
            stmt.execute("TRUNCATE TABLE shops_books RESTART IDENTITY CASCADE");
            stmt.execute("TRUNCATE TABLE books RESTART IDENTITY CASCADE");
            stmt.execute("TRUNCATE TABLE authors RESTART IDENTITY CASCADE");
            stmt.execute("TRUNCATE TABLE shops RESTART IDENTITY CASCADE");
        }
    }

    private Long createTestShop() {
        Shop shop = new Shop("Магазин", "Адрес");
        shopDao.insert(shop);
        return shopDao.getAll().get(0).getId();
    }

    private Long createTestBook() {
        Author author = new Author("Лев", "Толстой");
        authorDao.insert(author);
        Long authorId = authorDao.getAll().get(0).getId();
        Book book = new Book("Война и мир", authorId, 1869);
        bookDao.insert(book);
        return bookDao.getAll().get(0).getId();
    }

    @Test
    void insertAndGetById_shouldWork() {
        Long shopId = createTestShop();
        Long bookId = createTestBook();

        ShopBook shopBook = new ShopBook(shopId, bookId);
        shopBookDao.insert(shopBook);

        List<ShopBook> all = shopBookDao.getAll();
        assertThat(all).hasSize(1);

        ShopBook saved = all.get(0);
        assertThat(saved.getShopId()).isEqualTo(shopId);
        assertThat(saved.getBookId()).isEqualTo(bookId);

        ShopBook byId = shopBookDao.getById(saved.getId());
        assertThat(byId).usingRecursiveComparison().isEqualTo(saved);
    }

    @Test
    void clearTable_shouldDeleteAll() {
        Long shopId = createTestShop();
        Long bookId = createTestBook();
        shopBookDao.insert(new ShopBook(shopId, bookId));
        shopBookDao.clearTable();
        assertThat(shopBookDao.getCount()).isZero();
    }

    @Test
    void getCount_shouldReturnCorrectNumber() {
        Long shopId = createTestShop();
        Long bookId1 = createTestBook();
        Long bookId2 = createTestBook();
        shopBookDao.insert(new ShopBook(shopId, bookId1));
        shopBookDao.insert(new ShopBook(shopId, bookId2));
        assertThat(shopBookDao.getCount()).isEqualTo(2);
    }

    @Test
    void getAll_shouldReturnAllShopBooks() {
        Long shopId = createTestShop();
        Long bookId1 = createTestBook();
        Long bookId2 = createTestBook();
        shopBookDao.insert(new ShopBook(shopId, bookId1));
        shopBookDao.insert(new ShopBook(shopId, bookId2));
        List<ShopBook> all = shopBookDao.getAll();
        assertThat(all)
                .hasSize(2)
                .extracting(ShopBook::getBookId)
                .containsExactlyInAnyOrder(bookId1, bookId2);
    }

    @Test
    void getRandomBookIdByShop_shouldReturnRandomBookId() {
        Long shopId = createTestShop();
        Long bookId1 = createTestBook();
        Long bookId2 = createTestBook();
        shopBookDao.insert(new ShopBook(shopId, bookId1));
        shopBookDao.insert(new ShopBook(shopId, bookId2));

        Long randomBookId = shopBookDao.getRandomBookIdByShop(shopId);
        assertThat(randomBookId).isIn(bookId1, bookId2);
    }

    @Test
    void getRandomBookIdByShop_shouldReturnNullWhenNoBooks() {
        Long shopId = createTestShop();
        Long randomBookId = shopBookDao.getRandomBookIdByShop(shopId);
        assertThat(randomBookId).isNull();
    }
}
