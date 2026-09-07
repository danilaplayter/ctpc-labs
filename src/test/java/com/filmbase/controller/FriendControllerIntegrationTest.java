package com.filmbase.controller;

import static org.assertj.core.api.Assertions.assertThat;

import com.filmbase.IntegrationTestBase;
import com.filmbase.entity.User;
import com.filmbase.repository.UserRepository;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;

class FriendControllerIntegrationTest extends IntegrationTestBase {

    @Autowired private UserRepository userRepository;

    private Long userId1;
    private Long userId2;
    private String sessionCookie;

    @BeforeEach
    void setUp() {
        User user1 = new User();
        user1.setLogin("friend1");
        user1.setPassword("pass");
        user1.setEmail("f1@test.com");
        user1.setShowForAddFriend(true);
        userRepository.save(user1);
        userId1 = user1.getId();

        User user2 = new User();
        user2.setLogin("friend2");
        user2.setPassword("pass");
        user2.setEmail("f2@test.com");
        user2.setShowForAddFriend(true);
        userRepository.save(user2);
        userId2 = user2.getId();

        sessionCookie = loginAndGetSessionId("friend1", "pass");
        assertThat(sessionCookie).isNotNull();
    }

    @Test
    void addFriend_shouldReturnGood() {
        RestTemplate rest = restTemplateWithSession(sessionCookie);
        User target = new User();
        target.setId(userId2);

        ResponseEntity<String> response =
                rest.postForEntity(
                        "http://localhost:" + port + "/friends/add", target, String.class);
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isEqualTo("GOOD");
    }

    @Test
    void addFriend_shouldReturnBadRequest_whenAddingSelf() {
        RestTemplate rest = restTemplateWithSession(sessionCookie);
        User self = new User();
        self.setId(userId1);

        ResponseEntity<String> response =
                rest.postForEntity("http://localhost:" + port + "/friends/add", self, String.class);
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        assertThat(response.getBody()).isEqualTo("ERROR");
    }

    @Test
    void removeFriend_shouldReturnGood() {
        RestTemplate rest = restTemplateWithSession(sessionCookie);
        User target = new User();
        target.setId(userId2);
        rest.postForEntity("http://localhost:" + port + "/friends/add", target, String.class);

        ResponseEntity<String> response =
                rest.postForEntity(
                        "http://localhost:" + port + "/friends/remove", target, String.class);
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isEqualTo("GOOD");
    }

    @Test
    void myFriends_shouldReturnList() {
        RestTemplate rest = restTemplateWithSession(sessionCookie);
        User target = new User();
        target.setId(userId2);
        rest.postForEntity("http://localhost:" + port + "/friends/add", target, String.class);

        ResponseEntity<List<Object>> response =
                rest.exchange(
                        "http://localhost:" + port + "/friends",
                        HttpMethod.GET,
                        null,
                        new ParameterizedTypeReference<List<Object>>() {});
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).hasSize(1);
    }

    @Test
    void candidates_shouldReturnUsersWithShowForAddFriend() {
        RestTemplate rest = restTemplateWithSession(sessionCookie);
        ResponseEntity<List<User>> response =
                rest.exchange(
                        "http://localhost:" + port + "/friends/candidates",
                        HttpMethod.GET,
                        null,
                        new ParameterizedTypeReference<List<User>>() {});
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).hasSize(2);
    }
}
