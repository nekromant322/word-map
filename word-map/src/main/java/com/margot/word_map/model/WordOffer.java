package com.margot.word_map.model;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
@RequiredArgsConstructor
@Builder
@Table(name = "words_offer")
public class WordOffer {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NonNull
    private String word;

    @NonNull
    private String description;

    @NonNull
    private Long userId;

    private Boolean approved = false;

    private Boolean checked = false;
}
