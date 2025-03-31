package com.margot.word_map.model;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Getter
@Setter
@ToString
@NoArgsConstructor
@Table(name = "users")
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NonNull
    private String email;

    @Column(name = "date_creation")
    private LocalDateTime dateCreation;

    @Column(name = "date_active")
    private LocalDateTime dateActive;

    private Boolean access;

    @Enumerated(EnumType.STRING)
    @NonNull
    private Role role;
}