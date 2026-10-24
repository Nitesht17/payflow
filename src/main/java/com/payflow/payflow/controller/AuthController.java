package com.payflow.payflow.controller;

import com.payflow.payflow.dto.LoginRequestDTO;
import com.payflow.payflow.dto.LoginResponseDTO;
import com.payflow.payflow.entity.User;
import com.payflow.payflow.exception.ResourceNotFoundException;
import com.payflow.payflow.repository.UserRepository;
import com.payflow.payflow.util.JwtUtil;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Optional;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;

    public AuthController(UserRepository userRepository , PasswordEncoder passwordEncoder , JwtUtil jwtUtil)
    {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtUtil = jwtUtil;
    }

    /**
     * Authenticates a user by email and password, and issues a JWT token on success.
     * POST /api/auth/login
     *
     * @param requestDTO email and raw password from client
     * @return a JWT token wrapped in LoginResponseDTO
     * @throws ResourceNotFoundException if email doesn't exist or password is incorrect
     */
    @PostMapping("/login")
    public ResponseEntity<LoginResponseDTO> login(@Valid @RequestBody LoginRequestDTO requestDTO)
    {
        Optional<User> optionalUser = userRepository.findByEmail(requestDTO.getEmail());
        if(optionalUser.isEmpty())
        {
            throw new ResourceNotFoundException("Invalid email or password");
        }
        User user = optionalUser.get();

        // Verify the raw password against the stored hash
        boolean passwordMatches = passwordEncoder.matches(requestDTO.getPassword() , user.getPasswordHash());

        if(! passwordMatches)
        {
            throw new ResourceNotFoundException("Invalid email or password");
        }

        // Generate JWT token using the verified user's email
        String token = jwtUtil.generateToken(user.getEmail());

        return ResponseEntity.ok(new LoginResponseDTO(token));
    }

}
