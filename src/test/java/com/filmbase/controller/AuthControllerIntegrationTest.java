package com.filmbase.controller;

import static org.assertj.core.api.Assertions.assertThat;

import com.filmbase.IntegrationTestBase;
import com.filmbase.entity.User;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;

class AuthControllerIntegrationTest extends IntegrationTestBase {

    @Test
    void registerAndLoginFlow_shouldSucceed() {
        RestTemplate rest = restTemplate();
        User newUser = new User();
        newUser.setLogin("integration");
        newUser.setPassword("pass");
        newUser.setEmail("integration@test.com");

        ResponseEntity<String> registerResponse =
                rest.postForEntity(
                        "http://localhost:" + port + "/auth/register", newUser, String.class);
        assertThat(registerResponse.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(registerResponse.getBody()).isEqualTo("GOOD");

        String sessionCookie = loginAndGetSessionId("integration", "pass");
        assertThat(sessionCookie).isNotNull();

        RestTemplate sessionRest = restTemplateWithSession(sessionCookie);
        ResponseEntity<String> statusResponse =
                sessionRest.getForEntity("http://localhost:" + port + "/auth/status", String.class);
        assertThat(statusResponse.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(statusResponse.getBody()).isEqualTo("USER");
    }

    @Test
    void register_shouldFail_whenLoginExists() {
        RestTemplate rest = restTemplate();
        User user = new User();
        user.setLogin("existing");
        user.setPassword("pass");
        user.setEmail("existing@test.com");
        rest.postForEntity("http://localhost:" + port + "/auth/register", user, String.class);

        User duplicate = new User();
        duplicate.setLogin("existing");
        duplicate.setPassword("other");
        duplicate.setEmail("other@test.com");

        ResponseEntity<String> response =
                rest.postForEntity(
                        "http://localhost:" + port + "/auth/register", duplicate, String.class);
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        assertThat(response.getBody()).isEqualTo("ERRORLOGIN");
    }
}
