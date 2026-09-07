package com.filmbase.controller;

import static org.assertj.core.api.Assertions.assertThat;

import com.filmbase.IntegrationTestBase;
import com.filmbase.entity.Cinema;
import com.filmbase.entity.CinemaUser;
import com.filmbase.entity.User;
import com.filmbase.repository.CinemaRepository;
import com.filmbase.repository.CinemaUserRepository;
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

class UserFilmControllerIntegrationTest extends IntegrationTestBase {

    @Autowired private UserRepository userRepository;

    @Autowired private CinemaRepository cinemaRepository;

    @Autowired private CinemaUserRepository cinemaUserRepository;

    private Long cinemaId;
    private Long userId;
    private String sessionCookie;

    @BeforeEach
    void setUp() {
        User user = new User();
        user.setLogin("filmuser");
        user.setPassword("pass");
        user.setEmail("filmuser@test.com");
        userRepository.save(user);
        userId = user.getId();

        sessionCookie = loginAndGetSessionId("filmuser", "pass");
        assertThat(sessionCookie).isNotNull();

        Cinema cinema = new Cinema();
        cinema.setHeadName("User Film");
        cinema.setYear(2023);
        cinema = cinemaRepository.save(cinema);
        cinemaId = cinema.getId();
    }

    @Test
    void addToCollection_shouldReturnGood() {
        RestTemplate rest = restTemplateWithSession(sessionCookie);
        Cinema cinemaRef = new Cinema();
        cinemaRef.setId(cinemaId);

        ResponseEntity<String> response =
                rest.postForEntity(
                        "http://localhost:" + port + "/myfilms/add", cinemaRef, String.class);
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isEqualTo("GOOD");
    }

    @Test
    void addToCollection_shouldReturnBadRequest_whenAlreadyAdded() {
        RestTemplate rest = restTemplateWithSession(sessionCookie);
        Cinema cinemaRef = new Cinema();
        cinemaRef.setId(cinemaId);
        rest.postForEntity("http://localhost:" + port + "/myfilms/add", cinemaRef, String.class);

        ResponseEntity<String> response =
                rest.postForEntity(
                        "http://localhost:" + port + "/myfilms/add", cinemaRef, String.class);
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        assertThat(response.getBody()).isEqualTo("ERROR");
    }

    @Test
    void myFilms_shouldReturnList() {
        RestTemplate rest = restTemplateWithSession(sessionCookie);
        Cinema cinemaRef = new Cinema();
        cinemaRef.setId(cinemaId);
        rest.postForEntity("http://localhost:" + port + "/myfilms/add", cinemaRef, String.class);

        ResponseEntity<List<CinemaUser>> response =
                rest.exchange(
                        "http://localhost:" + port + "/myfilms",
                        HttpMethod.GET,
                        null,
                        new ParameterizedTypeReference<List<CinemaUser>>() {});
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).hasSize(1);
        assertThat(response.getBody().get(0).getCinema().getId()).isEqualTo(cinemaId);
    }

    @Test
    void removeFromCollection_shouldReturnOk() {
        RestTemplate rest = restTemplateWithSession(sessionCookie);
        Cinema cinemaRef = new Cinema();
        cinemaRef.setId(cinemaId);
        rest.postForEntity("http://localhost:" + port + "/myfilms/add", cinemaRef, String.class);

        ResponseEntity<String> response =
                rest.postForEntity(
                        "http://localhost:" + port + "/myfilms/remove", cinemaRef, String.class);
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isEqualTo("Фильм успешно удален из вашего каталога");
    }

    @Test
    void rateFilm_shouldUpdateRating() {
        RestTemplate rest = restTemplateWithSession(sessionCookie);
        Cinema cinemaRef = new Cinema();
        cinemaRef.setId(cinemaId);
        rest.postForEntity("http://localhost:" + port + "/myfilms/add", cinemaRef, String.class);

        List<CinemaUser> list =
                cinemaUserRepository.findAllByUser(userRepository.findById(userId).get());
        CinemaUser cu = list.get(0);
        cu.setRatingUser(8);
        cu.setStatusCinema("Просмотрен");

        ResponseEntity<String> response =
                rest.postForEntity("http://localhost:" + port + "/myfilms/rate", cu, String.class);
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isEqualTo("Изменения успешно сохранены");

        Cinema updatedCinema = cinemaRepository.findById(cinemaId).get();
        assertThat(updatedCinema.getRating()).isEqualTo(8.0f);
        assertThat(updatedCinema.getMarks()).isEqualTo(1);
    }
}
