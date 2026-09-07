package com.filmbase.controller;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

import com.filmbase.entity.Cinema;
import com.filmbase.entity.CinemaUser;
import com.filmbase.repository.CinemaRepository;
import com.filmbase.repository.CinemaUserRepository;
import com.filmbase.repository.UserRepository;
import java.util.Optional;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

@ExtendWith(MockitoExtension.class)
class UserFilmControllerUnitTest {

    @Mock private CinemaUserRepository cinemaUserRepository;

    @Mock private CinemaRepository cinemaRepository;

    @Mock private UserRepository userRepository;

    @InjectMocks private UserFilmController controller;

    @Test
    void rate_shouldUpdateAverageCorrectly_whenFirstRating() {
        Long cinemaUserId = 10L;
        Long cinemaId = 100L;

        Cinema cinema = new Cinema();
        cinema.setId(cinemaId);
        cinema.setMarks(0);
        cinema.setRating(0);

        CinemaUser cu = new CinemaUser();
        cu.setId(cinemaUserId);
        cu.setCinema(cinema);
        cu.setRatingUser(0);

        CinemaUser payload = new CinemaUser();
        payload.setId(cinemaUserId);
        payload.setRatingUser(7);
        payload.setStatusCinema("Просмотрен");

        when(cinemaUserRepository.findById(cinemaUserId)).thenReturn(Optional.of(cu));
        when(cinemaRepository.save(any(Cinema.class))).thenReturn(cinema);
        when(cinemaUserRepository.save(any(CinemaUser.class))).thenReturn(cu);

        ResponseEntity<String> response = controller.rate(payload);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isEqualTo("Изменения успешно сохранены");
        assertThat(cinema.getRating()).isEqualTo(7.0f);
        assertThat(cinema.getMarks()).isEqualTo(1);
        assertThat(cu.getRatingUser()).isEqualTo(7);
        verify(cinemaRepository).save(cinema);
        verify(cinemaUserRepository).save(cu);
    }

    @Test
    void rate_shouldUpdateAverageCorrectly_whenUserAlreadyRated() {
        Long cinemaUserId = 10L;
        Long cinemaId = 100L;

        Cinema cinema = new Cinema();
        cinema.setId(cinemaId);
        cinema.setMarks(5);
        cinema.setRating(4.0f);

        CinemaUser cu = new CinemaUser();
        cu.setId(cinemaUserId);
        cu.setCinema(cinema);
        cu.setRatingUser(3);

        CinemaUser payload = new CinemaUser();
        payload.setId(cinemaUserId);
        payload.setRatingUser(5);
        payload.setStatusCinema("Просмотрен");

        when(cinemaUserRepository.findById(cinemaUserId)).thenReturn(Optional.of(cu));
        when(cinemaRepository.save(any(Cinema.class))).thenReturn(cinema);
        when(cinemaUserRepository.save(any(CinemaUser.class))).thenReturn(cu);

        ResponseEntity<String> response = controller.rate(payload);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(cinema.getRating()).isEqualTo(4.4f);
        assertThat(cinema.getMarks()).isEqualTo(5);
        assertThat(cu.getRatingUser()).isEqualTo(5);
    }

    @Test
    void rate_shouldUpdateAverageCorrectly_whenUserFirstTimeRatingButMarksExist() {
        Long cinemaUserId = 10L;
        Long cinemaId = 100L;

        Cinema cinema = new Cinema();
        cinema.setId(cinemaId);
        cinema.setMarks(3);
        cinema.setRating(3.5f);

        CinemaUser cu = new CinemaUser();
        cu.setId(cinemaUserId);
        cu.setCinema(cinema);
        cu.setRatingUser(0);

        CinemaUser payload = new CinemaUser();
        payload.setId(cinemaUserId);
        payload.setRatingUser(4);
        payload.setStatusCinema("Просмотрен");

        when(cinemaUserRepository.findById(cinemaUserId)).thenReturn(Optional.of(cu));
        when(cinemaRepository.save(any(Cinema.class))).thenReturn(cinema);
        when(cinemaUserRepository.save(any(CinemaUser.class))).thenReturn(cu);

        ResponseEntity<String> response = controller.rate(payload);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(cinema.getRating()).isEqualTo(3.625f);
        assertThat(cinema.getMarks()).isEqualTo(4);
        assertThat(cu.getRatingUser()).isEqualTo(4);
    }
}
