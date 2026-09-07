package by.iba.repository;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.testcontainers.containers.PostgreSQLContainer;

public abstract class BaseIntegrationTest {

    protected static final PostgreSQLContainer<?> POSTGRES =
            new PostgreSQLContainer<>("postgres:15")
                    .withDatabaseName("testdb")
                    .withUsername("test")
                    .withPassword("test");

    protected static Connection connection;

    @BeforeAll
    static void startContainerAndInit() throws SQLException {
        POSTGRES.start();
        String jdbcUrl = POSTGRES.getJdbcUrl();
        String username = POSTGRES.getUsername();
        String password = POSTGRES.getPassword();
        connection = DriverManager.getConnection(jdbcUrl, username, password);
        createTables();
    }

    @AfterAll
    static void stopContainer() throws SQLException {
        if (connection != null) {
            connection.close();
        }
        POSTGRES.stop();
    }

    private static void createTables() throws SQLException {
        try (Statement stmt = connection.createStatement()) {
            stmt.execute(
                    "CREATE TABLE IF NOT EXISTS persons ("
                            + "id SERIAL PRIMARY KEY, "
                            + "pname VARCHAR(100), "
                            + "phone VARCHAR(20), "
                            + "email VARCHAR(100))");
            stmt.execute(
                    "CREATE TABLE IF NOT EXISTS teachers ("
                            + "id SERIAL PRIMARY KEY, "
                            + "tname VARCHAR(100), "
                            + "subject VARCHAR(100), "
                            + "phone VARCHAR(20))");
            stmt.execute(
                    "CREATE TABLE IF NOT EXISTS users ("
                            + "id SERIAL PRIMARY KEY, "
                            + "login VARCHAR(100) UNIQUE, "
                            + "passw BYTEA)");
        }
    }

    protected void clearTables() throws SQLException {
        try (Statement stmt = connection.createStatement()) {
            stmt.execute("DELETE FROM persons");
            stmt.execute("DELETE FROM teachers");
            stmt.execute("DELETE FROM users");
        }
    }
}
