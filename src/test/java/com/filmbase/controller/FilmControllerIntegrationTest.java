package com.filmbase.controller;

import static org.assertj.core.api.Assertions.assertThat;

import com.filmbase.IntegrationTestBase;
import com.filmbase.entity.Cinema;
import com.filmbase.repository.CinemaRepository;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;

class FilmControllerIntegrationTest extends IntegrationTestBase {

    @Autowired private CinemaRepository cinemaRepository;

    @BeforeEach
    void cleanUp() {
        cinemaRepository.deleteAll();
    }

    @Test
    void addFilm_shouldReturnOk() {
        RestTemplate rest = restTemplate();
        Cinema cinema = new Cinema();
        cinema.setHeadName("Inception");
        cinema.setDirector("Nolan");
        cinema.setGenre("Sci-Fi");
        cinema.setYear(2010);
        cinema.setAbout("A thief who steals corporate secrets...");

        ResponseEntity<String> addResponse =
                rest.postForEntity("http://localhost:" + port + "/films/add", cinema, String.class);
        assertThat(addResponse.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(addResponse.getBody()).isEqualTo("Фильм успешно добавлен в каталог");
    }

    @Test
    void addFilm_shouldReturnBadRequest_whenDuplicate() {
        RestTemplate rest = restTemplate();
        Cinema cinema = new Cinema();
        cinema.setHeadName("Inception");
        cinema.setYear(2010);

        ResponseEntity<String> firstAdd =
                rest.postForEntity("http://localhost:" + port + "/films/add", cinema, String.class);
        assertThat(firstAdd.getStatusCode()).isEqualTo(HttpStatus.OK);

        Cinema duplicate = new Cinema();
        duplicate.setHeadName("Inception");
        duplicate.setYear(2010);

        ResponseEntity<String> response =
                rest.postForEntity(
                        "http://localhost:" + port + "/films/add", duplicate, String.class);
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        assertThat(response.getBody()).isEqualTo("Этот фильм уже есть в базе");
    }

    @Test
    void getAllFilms_shouldReturnList() {
        RestTemplate rest = restTemplate();
        Cinema cinema = new Cinema();
        cinema.setHeadName("Interstellar");
        cinema.setYear(2014);

        ResponseEntity<String> addResponse =
                rest.postForEntity("http://localhost:" + port + "/films/add", cinema, String.class);
        assertThat(addResponse.getStatusCode()).isEqualTo(HttpStatus.OK);

        ResponseEntity<List<Cinema>> response =
                rest.exchange(
                        "http://localhost:" + port + "/films",
                        HttpMethod.GET,
                        null,
                        new ParameterizedTypeReference<List<Cinema>>() {});
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isNotEmpty();
        assertThat(response.getBody()).anyMatch(c -> "Interstellar".equals(c.getHeadName()));
    }

    @Test
    void searchFilms_shouldReturnMatching() {
        RestTemplate rest = restTemplate();

        Cinema cinema = new Cinema();
        cinema.setHeadName("The Matrix");
        cinema.setDirector("Wachowski");
        cinema.setGenre("Action");
        cinema.setYear(1999);

        ResponseEntity<String> addResponse =
                rest.postForEntity("http://localhost:" + port + "/films/add", cinema, String.class);
        assertThat(addResponse.getStatusCode())
                .withFailMessage("Добавление фильма не удалось: " + addResponse.getBody())
                .isEqualTo(HttpStatus.OK);

        Cinema query = new Cinema();
        query.setHeadName("Matrix");

        ResponseEntity<List<Cinema>> response =
                rest.exchange(
                        "http://localhost:" + port + "/films/search",
                        HttpMethod.POST,
                        new HttpEntity<>(query),
                        new ParameterizedTypeReference<List<Cinema>>() {});

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isNotNull().hasSize(1);
        assertThat(response.getBody().get(0).getHeadName()).isEqualTo("The Matrix");
    }
}
