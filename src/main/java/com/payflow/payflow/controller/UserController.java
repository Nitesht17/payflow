package com.payflow.payflow.controller;

import com.payflow.payflow.dto.UserRequestDTO;
import com.payflow.payflow.dto.UserResponseDTO;
import com.payflow.payflow.service.UserService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/users")
public class UserController {

    private final UserService userService;

    public UserController(UserService userService)
    {
        this.userService = userService;
    }
    @PostMapping
    public ResponseEntity<UserResponseDTO> createUSer(@Valid @RequestBody UserRequestDTO requestDTO)
    {
        UserResponseDTO response = userService.createUser(requestDTO);
        return ResponseEntity.ok(response);
    }

    /**
     * Fetches a single user by ID.
     * GET /api/users/{id}
     */
    @GetMapping("/{id}")
    public ResponseEntity<UserResponseDTO> getUserById(@PathVariable Long id)
    {
        UserResponseDTO  response = userService.getUserById(id);
        return ResponseEntity.ok(response);
    }

    /**
     * Fetches all users.
     * GET /api/users
     */
    @GetMapping
    public ResponseEntity<List<UserResponseDTO>> getAllUsers()
    {
       List<UserResponseDTO> users =  userService.getAllUsers();
       return ResponseEntity.ok(users);
    }

    /**
     * Updates an existing user's details.
     * PUT /api/users/{id}
     */
    @PutMapping("/{id}")
    public ResponseEntity<UserResponseDTO> updateUser(@PathVariable Long id , @Valid @RequestBody UserRequestDTO requestDTO )
    {
        UserResponseDTO response = userService.updateUser(id , requestDTO);
        return ResponseEntity.ok(response);
    }

    /**
     * Deletes a user by ID.
     * DELETE /api/users/{id}
     */
    @DeleteMapping("/{id}")
    public  ResponseEntity<Void> deleteUser(@PathVariable Long id)
    {
        userService.deleteUser(id);
        return ResponseEntity.noContent().build();
    }
}
