package com.payflow.payflow.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.payflow.payflow.dto.UserRequestDTO;
import com.payflow.payflow.dto.UserResponseDTO;
import com.payflow.payflow.service.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import java.time.LocalDateTime;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * Integration tests for UserController.
 * Uses MockMvc to simulate HTTP requests without starting a real server.
 */
@SpringBootTest
class UserControllerTest {

    private MockMvc mockMvc;

    @Autowired
    private WebApplicationContext context;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private UserService userService;

    @BeforeEach
    void setup() {
        mockMvc = MockMvcBuilders.webAppContextSetup(context).build();
    }

    @Test
    void createUser_shouldReturn200_whenValidRequest() throws Exception {

        UserRequestDTO requestDTO = new UserRequestDTO();
        requestDTO.setEmail("test@example.com");
        requestDTO.setPassword("test123");
        requestDTO.setRole("USER");

        UserResponseDTO responseDTO = new UserResponseDTO(
                1L, "test@example.com", "USER", LocalDateTime.now());

        when(userService.createUser(any(UserRequestDTO.class))).thenReturn(responseDTO);

        mockMvc.perform(post("/api/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestDTO)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.email").value("test@example.com"))
                .andExpect(jsonPath("$.role").value("USER"))
                .andExpect(jsonPath("$.id").value(1));
    }

    @Test
    void createUser_shouldReturn400_whenEmailIsBlank() throws Exception {

        UserRequestDTO requestDTO = new UserRequestDTO();
        requestDTO.setEmail("");
        requestDTO.setPassword("test123");
        requestDTO.setRole("USER");

        mockMvc.perform(post("/api/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestDTO)))
                .andExpect(status().isBadRequest());
    }
}