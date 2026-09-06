package org.jdbc_lab.statement.dao;

import static org.assertj.core.api.Assertions.assertThat;

import java.sql.Statement;
import java.util.List;
import org.jdbc_lab.connection.ConnectionManager;
import org.jdbc_lab.statement.model.Shop;
import org.jdbc_lab.test.BaseTest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class ShopDaoTest extends BaseTest {

    private ShopDao shopDao;

    @BeforeEach
    void setUp() throws Exception {
        shopDao = new ShopDao();
        try (Statement stmt = ConnectionManager.getConnection().createStatement()) {
            stmt.execute("TRUNCATE TABLE shops_books RESTART IDENTITY CASCADE");
            stmt.execute("TRUNCATE TABLE books RESTART IDENTITY CASCADE");
            stmt.execute("TRUNCATE TABLE authors RESTART IDENTITY CASCADE");
            stmt.execute("TRUNCATE TABLE shops RESTART IDENTITY CASCADE");
        }
    }

    @Test
    void insertAndGetById_shouldWork() {
        Shop shop = new Shop("Белкнига", "проспект Независимости, 14");
        shopDao.insert(shop);

        List<Shop> all = shopDao.getAll();
        assertThat(all).hasSize(1);

        Shop saved = all.get(0);
        assertThat(saved.getName()).isEqualTo("Белкнига");
        assertThat(saved.getAddress()).isEqualTo("проспект Независимости, 14");

        Shop byId = shopDao.getById(saved.getId());
        assertThat(byId).usingRecursiveComparison().isEqualTo(saved);
    }

    @Test
    void clearTable_shouldDeleteAll() {
        shopDao.insert(new Shop("Магазин1", "Адрес1"));
        shopDao.clearTable();
        assertThat(shopDao.getCount()).isZero();
    }

    @Test
    void getCount_shouldReturnCorrectNumber() {
        assertThat(shopDao.getCount()).isZero();
        shopDao.insert(new Shop("Магазин1", "Адрес1"));
        shopDao.insert(new Shop("Магазин2", "Адрес2"));
        assertThat(shopDao.getCount()).isEqualTo(2);
    }

    @Test
    void getAll_shouldReturnAllShops() {
        shopDao.insert(new Shop("Магазин1", "Адрес1"));
        shopDao.insert(new Shop("Магазин2", "Адрес2"));
        List<Shop> all = shopDao.getAll();
        assertThat(all)
                .hasSize(2)
                .extracting(Shop::getName)
                .containsExactlyInAnyOrder("Магазин1", "Магазин2");
    }

    @Test
    void getById_shouldReturnNullForNonExistentId() {
        Shop shop = shopDao.getById(999L);
        assertThat(shop).isNull();
    }
}
