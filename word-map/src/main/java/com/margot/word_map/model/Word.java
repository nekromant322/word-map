package com.margot.word_map.model;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Table(name = "words")
public class Word {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "word", length = 100, nullable = false, unique = true)
    private String word;

    @Column(name = "description", length = 200, nullable = false)
    private String description;

    @Column(name = "word_length", nullable = false)
    private Integer wordLength;

    @ManyToOne
    @JoinColumn(name = "language_id")
    private Language language;

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    @Column(name = "edited_at", nullable = false)
    private LocalDateTime editedAt;

    @ManyToOne
    @JoinColumn(name = "created_id", nullable = false)
    private Admin createdBy;

    @ManyToOne
    @JoinColumn(name = "edited_id", nullable = false)
    private Admin editedBy;
}
