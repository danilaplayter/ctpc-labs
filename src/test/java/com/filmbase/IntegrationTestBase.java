package com.filmbase;

import com.filmbase.entity.User;
import org.junit.jupiter.api.BeforeEach;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.web.client.RestTemplate;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@Testcontainers
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_CLASS)
public abstract class IntegrationTestBase {

    @Container
    static PostgreSQLContainer<?> postgres =
            new PostgreSQLContainer<>("postgres:15")
                    .withDatabaseName("testdb")
                    .withUsername("test")
                    .withPassword("test");

    @LocalServerPort protected int port;

    @Autowired private JdbcTemplate jdbcTemplate;

    @BeforeEach
    void cleanDatabase() {
        jdbcTemplate.execute(
                "TRUNCATE TABLE friend_relationship, cinema_user, review, cinema, users RESTART"
                        + " IDENTITY CASCADE");
    }

    @DynamicPropertySource
    static void properties(DynamicPropertyRegistry registry) {
        registry.add("spring.datasource.url", postgres::getJdbcUrl);
        registry.add("spring.datasource.username", postgres::getUsername);
        registry.add("spring.datasource.password", postgres::getPassword);
    }

    protected RestTemplate restTemplate() {
        return new TestRestTemplate().getRestTemplate();
    }

    protected String loginAndGetSessionId(String login, String password) {
        RestTemplate rest = restTemplate();
        User credentials = new User();
        credentials.setLogin(login);
        credentials.setPassword(password);
        ResponseEntity<String> response =
                rest.postForEntity(
                        "http://localhost:" + port + "/auth/login", credentials, String.class);
        if (response.getStatusCode().is2xxSuccessful()) {
            HttpHeaders headers = response.getHeaders();
            String setCookie = headers.getFirst(HttpHeaders.SET_COOKIE);
            if (setCookie != null) {
                String[] parts = setCookie.split(";");
                for (String part : parts) {
                    if (part.trim().startsWith("JSESSIONID=")) {
                        return part.trim();
                    }
                }
            }
        }
        return null;
    }

    protected RestTemplate restTemplateWithSession(String sessionCookie) {
        RestTemplate rest = restTemplate();
        rest.getInterceptors()
                .add(
                        (request, body, execution) -> {
                            request.getHeaders().add(HttpHeaders.COOKIE, sessionCookie);
                            return execution.execute(request, body);
                        });
        return rest;
    }
}
