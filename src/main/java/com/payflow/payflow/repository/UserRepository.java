package com.payflow.payflow.repository;

import com.payflow.payflow.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserRepository extends JpaRepository<User , Long> {

    /**
     * Finds a user by email — used during login to look up credentials.
     * Spring Data JPA auto-generates the query from this method name
     * (Query Derivation) — no implementation needed.
     */
    Optional<User> findByEmail(String email);
}
