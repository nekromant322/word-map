package com.margot.word_map.model;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Table(name = "words_offer")
public class WordOffer {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String word;

    private String description;

    private Long userId;

    @Builder.Default
    private Boolean approved = false;

    @Builder.Default
    private Boolean checked = false;
}
