package com.hibernate.xmlbased;

import com.hibernate.xmlbased.model.Department;
import com.hibernate.xmlbased.model.Developer;
import com.hibernate.xmlbased.model.User;
import java.util.Properties;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.cfg.Configuration;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

@Testcontainers
public abstract class BaseDaoTest {

    @Container
    static PostgreSQLContainer<?> postgres =
            new PostgreSQLContainer<>("postgres:15")
                    .withDatabaseName("testdb")
                    .withUsername("test")
                    .withPassword("test");

    protected static SessionFactory sessionFactory;

    @BeforeAll
    static void initSessionFactory() {
        Properties props = new Properties();
        props.setProperty("hibernate.dialect", "org.hibernate.dialect.PostgreSQLDialect");
        props.setProperty("hibernate.connection.driver_class", "org.postgresql.Driver");
        props.setProperty("hibernate.connection.url", postgres.getJdbcUrl());
        props.setProperty("hibernate.connection.username", postgres.getUsername());
        props.setProperty("hibernate.connection.password", postgres.getPassword());
        props.setProperty("hibernate.hbm2ddl.auto", "create-drop");
        props.setProperty("hibernate.show_sql", "true");

        sessionFactory =
                new Configuration()
                        .addAnnotatedClass(User.class)
                        .addAnnotatedClass(Developer.class)
                        .addAnnotatedClass(Department.class)
                        .setProperties(props)
                        .buildSessionFactory();
    }

    @AfterAll
    static void closeSessionFactory() {
        if (sessionFactory != null) {
            sessionFactory.close();
        }
    }

    @BeforeEach
    void cleanDatabase() {
        try (Session session = sessionFactory.openSession()) {
            session.beginTransaction();
            session.createMutationQuery("DELETE FROM User").executeUpdate();
            session.createMutationQuery("DELETE FROM Developer").executeUpdate();
            session.createMutationQuery("DELETE FROM Department").executeUpdate();
            session.getTransaction().commit();
        }
    }
}
