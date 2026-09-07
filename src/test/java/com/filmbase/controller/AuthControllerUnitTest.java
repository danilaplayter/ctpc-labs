package com.filmbase.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.filmbase.entity.User;
import com.filmbase.repository.UserRepository;
import java.util.Optional;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockHttpSession;
import org.springframework.test.web.servlet.MockMvc;

@WebMvcTest(AuthController.class)
class AuthControllerUnitTest {

    @Autowired private MockMvc mockMvc;

    @Autowired private ObjectMapper objectMapper;

    @MockBean private UserRepository userRepository;

    @Test
    void register_shouldReturnErrorLogin_whenLoginExists() throws Exception {
        User user = new User();
        user.setLogin("existing");
        user.setEmail("test@test.com");
        user.setPassword("pass");

        when(userRepository.findByLogin("existing")).thenReturn(Optional.of(new User()));

        mockMvc.perform(
                        post("/auth/register")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(user)))
                .andExpect(status().isBadRequest())
                .andExpect(content().string("ERRORLOGIN"));
    }

    @Test
    void register_shouldReturnErrorEmail_whenEmailExists() throws Exception {
        User user = new User();
        user.setLogin("newuser");
        user.setEmail("existing@test.com");
        user.setPassword("pass");

        when(userRepository.findByLogin("newuser")).thenReturn(Optional.empty());
        when(userRepository.findByEmail("existing@test.com")).thenReturn(Optional.of(new User()));

        mockMvc.perform(
                        post("/auth/register")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(user)))
                .andExpect(status().isBadRequest())
                .andExpect(content().string("ERROREMAIL"));
    }

    @Test
    void register_shouldReturnGood_whenSuccess() throws Exception {
        User user = new User();
        user.setLogin("newuser");
        user.setEmail("new@test.com");
        user.setPassword("pass");

        when(userRepository.findByLogin("newuser")).thenReturn(Optional.empty());
        when(userRepository.findByEmail("new@test.com")).thenReturn(Optional.empty());
        when(userRepository.save(any(User.class))).thenReturn(user);

        mockMvc.perform(
                        post("/auth/register")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(user)))
                .andExpect(status().isOk())
                .andExpect(content().string("GOOD"));
    }

    @Test
    void login_shouldReturnAuth_whenCredentialsValid() throws Exception {
        User credentials = new User();
        credentials.setLogin("testuser");
        credentials.setPassword("pass");

        User found = new User();
        found.setId(1L);
        found.setLogin("testuser");
        found.setPassword("pass");

        when(userRepository.findByLogin("testuser")).thenReturn(Optional.of(found));

        mockMvc.perform(
                        post("/auth/login")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(credentials)))
                .andExpect(status().isOk())
                .andExpect(content().string("AUTH"));
    }

    @Test
    void login_shouldReturnNoAuth_whenCredentialsInvalid() throws Exception {
        User credentials = new User();
        credentials.setLogin("testuser");
        credentials.setPassword("wrong");

        when(userRepository.findByLogin("testuser")).thenReturn(Optional.empty());

        mockMvc.perform(
                        post("/auth/login")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(credentials)))
                .andExpect(status().isUnauthorized())
                .andExpect(content().string("NOAUTH"));
    }

    @Test
    void status_shouldReturnUser_whenLoggedIn() throws Exception {
        MockHttpSession session = new MockHttpSession();
        session.setAttribute("userId", 1L);

        User user = new User();
        user.setId(1L);
        user.setLogin("testuser");

        when(userRepository.findById(1L)).thenReturn(Optional.of(user));

        mockMvc.perform(get("/auth/status").session(session))
                .andExpect(status().isOk())
                .andExpect(content().string("USER"));
    }

    @Test
    void status_shouldReturnAdmin_whenLoggedInAsAdmin() throws Exception {
        MockHttpSession session = new MockHttpSession();
        session.setAttribute("userId", 1L);

        User user = new User();
        user.setId(1L);
        user.setLogin("admin");

        when(userRepository.findById(1L)).thenReturn(Optional.of(user));

        mockMvc.perform(get("/auth/status").session(session))
                .andExpect(status().isOk())
                .andExpect(content().string("ADMIN"));
    }

    @Test
    void status_shouldReturnNull_whenNotLoggedIn() throws Exception {
        mockMvc.perform(get("/auth/status"))
                .andExpect(status().isOk())
                .andExpect(content().string("NULL"));
    }

    @Test
    void me_shouldReturnUser_whenLoggedIn() throws Exception {
        MockHttpSession session = new MockHttpSession();
        session.setAttribute("userId", 1L);

        User user = new User();
        user.setId(1L);
        user.setLogin("testuser");
        user.setPassword("secret");
        user.setEmail("test@test.com");

        when(userRepository.findById(1L)).thenReturn(Optional.of(user));

        mockMvc.perform(get("/auth/me").session(session))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.login").value("testuser"))
                .andExpect(jsonPath("$.password").doesNotExist())
                .andExpect(jsonPath("$.email").value("test@test.com"));
    }

    @Test
    void me_shouldReturnUnauthorized_whenNotLoggedIn() throws Exception {
        mockMvc.perform(get("/auth/me")).andExpect(status().isUnauthorized());
    }

    @Test
    void logout_shouldInvalidateSession() throws Exception {
        MockHttpSession session = new MockHttpSession();
        session.setAttribute("userId", 1L);

        mockMvc.perform(get("/auth/logout").session(session))
                .andExpect(status().isOk())
                .andExpect(content().string("DEAUTH"));
    }
}
