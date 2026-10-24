package com.payflow.payflow.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@ToString(exclude = "passwordHash")
@Table(name = "users")
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false , unique = true)
    private String email;

    @Column(name="password_hash" , nullable = false)
    private String passwordHash ;

    @Column(nullable = false)
    private String role;

    @Column(name="created_at" , nullable = false)
    private LocalDateTime createdAt;



}
