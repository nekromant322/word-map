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

    private String word;

    private Long userId;

    private LocalDateTime createdAt;

    private Long languageId;

    @Column(name = "status")
    @Enumerated(EnumType.STRING)
    private WordOfferStatus status = WordOfferStatus.UNCHECKED;
}
