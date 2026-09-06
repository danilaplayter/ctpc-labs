package org.jdbc_lab.test;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.testcontainers.containers.PostgreSQLContainer;

public abstract class BaseTest {
    protected static final PostgreSQLContainer<?> POSTGRES =
            new PostgreSQLContainer<>("postgres:17")
                    .withDatabaseName("testdb")
                    .withUsername("test")
                    .withPassword("test")
                    .withInitScript("init.sql");

    @BeforeAll
    static void startContainer() {
        POSTGRES.start();
        System.setProperty("jdbc.url", POSTGRES.getJdbcUrl());
        System.setProperty("jdbc.user", POSTGRES.getUsername());
        System.setProperty("jdbc.password", POSTGRES.getPassword());
    }

    @AfterAll
    static void stopContainer() {
        POSTGRES.stop();
    }
}
