package com.margot.word_map.model;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Table(name = "offers")
public class WordOffer {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "word", nullable = false, length = 100)
    private String word;

    @Column(name = "language_id", nullable = false)
    private Long languageId;

    @Column(name = "player_id", nullable = false)
    private Long playerId;

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    @Column(name = "status")
    @Builder.Default
    private WordOfferStatus status = WordOfferStatus.CHECK;
}
