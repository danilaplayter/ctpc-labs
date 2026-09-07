package com.filmbase.controller;

import static org.assertj.core.api.Assertions.assertThat;

import com.filmbase.IntegrationTestBase;
import com.filmbase.entity.Cinema;
import com.filmbase.entity.User;
import com.filmbase.repository.CinemaRepository;
import com.filmbase.repository.UserRepository;
import java.util.List;
import java.util.Map;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;

class ReviewControllerIntegrationTest extends IntegrationTestBase {

    @Autowired private UserRepository userRepository;

    @Autowired private CinemaRepository cinemaRepository;

    private Long cinemaId;
    private String sessionCookie;

    @BeforeEach
    void setUp() {
        User user = new User();
        user.setLogin("reviewer");
        user.setPassword("pass");
        user.setEmail("reviewer@test.com");
        userRepository.save(user);

        sessionCookie = loginAndGetSessionId("reviewer", "pass");
        assertThat(sessionCookie).isNotNull();

        Cinema cinema = new Cinema();
        cinema.setHeadName("Review Film");
        cinema.setYear(2023);
        cinema = cinemaRepository.save(cinema);
        cinemaId = cinema.getId();
    }

    @Test
    void addReview_shouldReturnOk() {
        RestTemplate rest = restTemplateWithSession(sessionCookie);
        ReviewController.ReviewRequest request =
                new ReviewController.ReviewRequest(null, cinemaId, "Great movie!");

        ResponseEntity<String> response =
                rest.postForEntity(
                        "http://localhost:" + port + "/reviews/add", request, String.class);
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isEqualTo("Отзыв добавлен");
    }

    @Test
    void getReviewsByFilm_shouldReturnList() {
        RestTemplate rest = restTemplateWithSession(sessionCookie);
        ReviewController.ReviewRequest request =
                new ReviewController.ReviewRequest(null, cinemaId, "Nice!");
        rest.postForEntity("http://localhost:" + port + "/reviews/add", request, String.class);

        ResponseEntity<List<Object>> response =
                rest.exchange(
                        "http://localhost:" + port + "/reviews/film/" + cinemaId,
                        HttpMethod.GET,
                        null,
                        new ParameterizedTypeReference<List<Object>>() {});
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).hasSize(1);
    }

    @Test
    void editReview_shouldReturnOk() {
        RestTemplate rest = restTemplateWithSession(sessionCookie);
        ReviewController.ReviewRequest addRequest =
                new ReviewController.ReviewRequest(null, cinemaId, "Original text");
        rest.postForEntity("http://localhost:" + port + "/reviews/add", addRequest, String.class);

        ResponseEntity<List<Map>> listResponse =
                rest.exchange(
                        "http://localhost:" + port + "/reviews/film/" + cinemaId,
                        HttpMethod.GET,
                        null,
                        new ParameterizedTypeReference<List<Map>>() {});
        Long reviewId = Long.valueOf(listResponse.getBody().get(0).get("id").toString());

        ReviewController.ReviewRequest editRequest =
                new ReviewController.ReviewRequest(reviewId, null, "Updated text");

        ResponseEntity<String> editResponse =
                rest.postForEntity(
                        "http://localhost:" + port + "/reviews/edit", editRequest, String.class);
        assertThat(editResponse.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(editResponse.getBody()).isEqualTo("Отзыв обновлен");
    }

    @Test
    void deleteReview_shouldReturnOk() {
        RestTemplate rest = restTemplateWithSession(sessionCookie);
        ReviewController.ReviewRequest addRequest =
                new ReviewController.ReviewRequest(null, cinemaId, "To be deleted");
        rest.postForEntity("http://localhost:" + port + "/reviews/add", addRequest, String.class);

        ResponseEntity<List<Map>> listResponse =
                rest.exchange(
                        "http://localhost:" + port + "/reviews/film/" + cinemaId,
                        HttpMethod.GET,
                        null,
                        new ParameterizedTypeReference<List<Map>>() {});
        Long reviewId = Long.valueOf(listResponse.getBody().get(0).get("id").toString());

        ReviewController.ReviewRequest deleteRequest =
                new ReviewController.ReviewRequest(reviewId, null, null);
        ResponseEntity<String> deleteResponse =
                rest.postForEntity(
                        "http://localhost:" + port + "/reviews/delete",
                        deleteRequest,
                        String.class);
        assertThat(deleteResponse.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(deleteResponse.getBody()).isEqualTo("Отзыв удален");
    }
}
