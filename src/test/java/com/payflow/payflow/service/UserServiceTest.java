package com.payflow.payflow.service;


import com.payflow.payflow.dto.UserRequestDTO;
import com.payflow.payflow.dto.UserResponseDTO;
import com.payflow.payflow.entity.User;
import com.payflow.payflow.exception.ResourceNotFoundException;
import com.payflow.payflow.repository.UserRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.time.LocalDateTime;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * Unit tests for UserService.
 * Repository and PasswordEncoder are mocked — only the Service logic is tested.
 */

@ExtendWith(MockitoExtension.class)
public class UserServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private UserService userService;

    @Test
    void createUser_shouldReturnResponseDTO_whenValidRequest()
    {
        UserRequestDTO requestDTO = new UserRequestDTO();
        requestDTO.setEmail("test@example.com");
        requestDTO.setPassword("test123");
        requestDTO.setRole("USER");

        User savedUser = new User();
        savedUser.setId(1L);
        savedUser.setEmail("test@example.com");
        savedUser.setPasswordHash("hashed_password");
        savedUser.setRole("USER");
        savedUser.setCreatedAt(LocalDateTime.now());

        when(passwordEncoder.encode("test123")).thenReturn("hashed_password");
        when(userRepository.save(any(User.class))).thenReturn(savedUser);

        UserResponseDTO response = userService.createUser(requestDTO);

        assertNotNull(response);
        assertEquals("test@example.com", response.getEmail());
        assertEquals("USER", response.getRole());
        assertNotNull(response.getId());

        verify(passwordEncoder, times(1)).encode("test123");
        verify(userRepository, times(1)).save(any(User.class));


    }

    @Test
    void getUserById_shouldThrowException_whenUserNotFound() {

        // Arrange — mock findById to return empty Optional (user doesn't exist)
        when(userRepository.findById(999L)).thenReturn(Optional.empty());

        // Act + Assert — expect exception to be thrown
        assertThrows(ResourceNotFoundException.class, () -> {
            userService.getUserById(999L);
        });

        // Verify — findById was called once
        verify(userRepository, times(1)).findById(999L);
    }
}
