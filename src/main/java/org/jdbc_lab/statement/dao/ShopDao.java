package org.jdbc_lab.statement.dao;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;
import org.jdbc_lab.connection.ConnectionManager;
import org.jdbc_lab.statement.model.Shop;

public class ShopDao implements StatementDao<Shop> {
    private static final Logger logger = Logger.getLogger(ShopDao.class.getName());

    @Override
    public void clearTable() {
        try (Statement stmt = ConnectionManager.getConnection().createStatement()) {
            stmt.executeUpdate("DELETE FROM shops");
        } catch (SQLException e) {
            logger.severe("Ошибка: " + e.getMessage());
            throw new RuntimeException(e);
        }
    }

    @Override
    public void insert(Shop shop) {
        try (Statement stmt = ConnectionManager.getConnection().createStatement()) {
            stmt.executeUpdate(
                    "INSERT INTO shops (name, address) VALUES ('"
                            + shop.getName()
                            + "', '"
                            + shop.getAddress()
                            + "')");
        } catch (SQLException e) {
            logger.severe("Ошибка: " + e.getMessage());
            throw new RuntimeException(e);
        }
    }

    @Override
    public int getCount() {
        int count = 0;
        try (Statement stmt = ConnectionManager.getConnection().createStatement();
                ResultSet rs = stmt.executeQuery("SELECT COUNT(*) FROM shops")) {
            if (rs.next()) count = rs.getInt(1);
        } catch (SQLException e) {
            logger.severe("Ошибка: " + e.getMessage());
            throw new RuntimeException(e);
        }
        return count;
    }

    @Override
    public List<Shop> getAll() {
        List<Shop> shops = new ArrayList<>();
        try (Statement stmt = ConnectionManager.getConnection().createStatement();
                ResultSet rs = stmt.executeQuery("SELECT * FROM shops")) {
            while (rs.next()) {
                Shop s = new Shop();
                s.setId(rs.getLong("id"));
                s.setName(rs.getString("name"));
                s.setAddress(rs.getString("address"));
                shops.add(s);
            }
        } catch (SQLException e) {
            logger.severe("Ошибка: " + e.getMessage());
            throw new RuntimeException(e);
        }
        return shops;
    }

    @Override
    public Shop getById(Long id) {
        Shop shop = null;
        try (Statement stmt = ConnectionManager.getConnection().createStatement();
                ResultSet rs = stmt.executeQuery("SELECT * FROM shops WHERE id = " + id)) {
            if (rs.next()) {
                shop = new Shop();
                shop.setId(rs.getLong("id"));
                shop.setName(rs.getString("name"));
                shop.setAddress(rs.getString("address"));
            }
        } catch (SQLException e) {
            logger.severe("Ошибка: " + e.getMessage());
            throw new RuntimeException(e);
        }
        return shop;
    }
}
