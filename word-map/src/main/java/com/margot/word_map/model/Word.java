package com.margot.word_map.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Table(name = "words")
public class Word {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "word")
    private String word;

    @Column(name = "description")
    private String description;

    @Column(name = "word_length")
    private Integer wordLength;

    @ManyToOne
    @JoinColumn(name = "id_language")
    private Language language;

    @Column(name = "date_creation")
    private LocalDateTime dateCreation;

    @Column(name = "date_edited")
    private LocalDateTime dateEdited;

    @ManyToOne
    @JoinColumn(name = "id_creation")
    private User createdBy;

    @ManyToOne
    @JoinColumn(name = "id_edited")
    private User editedBy;
}
