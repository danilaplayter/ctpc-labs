package com.hibernate.xmlbased.config;

import com.hibernate.xmlbased.model.Department;
import com.hibernate.xmlbased.model.Developer;
import com.hibernate.xmlbased.model.User;
import java.io.InputStream;
import java.util.Map;
import java.util.Properties;
import org.hibernate.SessionFactory;
import org.hibernate.cfg.Configuration;
import org.yaml.snakeyaml.Yaml;

public class SessionConfig {

    private static SessionConfig instance;
    private final SessionFactory sessionFactory;

    private SessionConfig() {
        Properties props = loadPropertiesFromYaml();
        sessionFactory =
                new Configuration()
                        .addAnnotatedClass(User.class)
                        .addAnnotatedClass(Developer.class)
                        .addAnnotatedClass(Department.class)
                        .setProperties(props)
                        .buildSessionFactory();
    }

    @SuppressWarnings("unchecked")
    private Properties loadPropertiesFromYaml() {
        try (InputStream in = getClass().getClassLoader().getResourceAsStream("application.yml")) {
            Yaml yaml = new Yaml();
            Map<String, Map<String, Object>> root = yaml.load(in);
            Map<String, Object> db = root.get("database");
            Properties props = new Properties();
            props.setProperty("hibernate.dialect", (String) db.get("dialect"));
            props.setProperty("hibernate.connection.driver_class", (String) db.get("driver"));
            props.setProperty("hibernate.connection.url", (String) db.get("url"));
            props.setProperty("hibernate.connection.username", (String) db.get("username"));
            props.setProperty("hibernate.connection.password", (String) db.get("password"));
            props.setProperty("hibernate.show_sql", String.valueOf(db.get("show_sql")));
            props.setProperty("hibernate.hbm2ddl.auto", (String) db.get("hbm2ddl_auto"));
            return props;
        } catch (Exception e) {
            throw new RuntimeException("Failed to load YAML configuration", e);
        }
    }

    public static SessionConfig getInstance() {
        if (instance == null) {
            instance = new SessionConfig();
        }
        return instance;
    }

    public SessionFactory getSessionFactory() {
        return sessionFactory;
    }
}
