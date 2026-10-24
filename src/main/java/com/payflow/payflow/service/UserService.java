package com.payflow.payflow.service;

import com.payflow.payflow.dto.UserRequestDTO;
import com.payflow.payflow.dto.UserResponseDTO;
import com.payflow.payflow.entity.User;
import com.payflow.payflow.exception.ResourceNotFoundException;
import com.payflow.payflow.repository.UserRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public UserService(UserRepository userRepository , PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Transactional
    public UserResponseDTO createUser(UserRequestDTO requestDTO)
    {
        User user = new User();
        user.setEmail(requestDTO.getEmail());

        user.setPasswordHash(passwordEncoder.encode(requestDTO.getPassword()));
        user.setRole(requestDTO.getRole());
        user.setCreatedAt(LocalDateTime.now());

        User saved= userRepository.save(user);

        return new UserResponseDTO(saved.getId(),
                saved.getEmail() ,
                saved.getRole() ,
                saved.getCreatedAt());
    }

    /**
     * Fetches a single user by their ID.
     *
     * @param id the user's database ID
     * @return response DTO with user details
     * @throws ResourceNotFoundException if no user exists with the given ID
     */
    public UserResponseDTO getUserById(Long id)
    {
        User user = userRepository.findById(id).orElseThrow(()-> new ResourceNotFoundException("User Not Found By id"+id));
        return new UserResponseDTO(user.getId() , user.getEmail() , user.getRole() , user.getCreatedAt());
    }

    /**
     * Fetches all users in the system.
     *
     * @return list of response DTOs for every user
     */
    public List<UserResponseDTO>getAllUsers()
    {
        List<User> users = userRepository.findAll();
        List<UserResponseDTO> responseList = new ArrayList<>();

        for(User user : users)
        {
            responseList.add(new UserResponseDTO(user.getId() , user.getEmail() , user.getRole() , user.getCreatedAt()));
        }
        return responseList;
    }

    /**
     * Updates an existing user's email and role.
     * Password is not updated here — handled separately for security reasons.
     *
     * @param id the user's database ID
     * @param requestDTO new values for email and role
     * @return response DTO with updated user details
     * @throws ResourceNotFoundException if no user exists with the given ID
     */
    public UserResponseDTO updateUser(Long id , UserRequestDTO requestDTO)
    {

        Optional<User> optionalUser = userRepository.findById(id);
        if(optionalUser.isEmpty())
        {
            throw new ResourceNotFoundException("User not found by id"+id);
        }
        User user = optionalUser.get();
        user.setEmail(requestDTO.getEmail());
        user.setRole(requestDTO.getRole());

        User update = userRepository.save(user);

        return new UserResponseDTO(update.getId() , update.getEmail() , update.getRole() , update.getCreatedAt());
    }

    /**
     * Deletes a user by their ID.
     *
     * @param id the user's database ID
     * @throws ResourceNotFoundException if no user exists with the given ID
     */

    public void deleteUser(Long id)
    {
        if(!userRepository.existsById(id))
        {
            throw new ResourceNotFoundException("User not found by id"+id);
        }
        userRepository.deleteById(id);
    }

}
