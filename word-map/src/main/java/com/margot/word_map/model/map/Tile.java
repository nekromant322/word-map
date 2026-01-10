package com.margot.word_map.model.map;

import com.margot.word_map.model.BonusType;
import com.margot.word_map.model.LetterType;
import jakarta.persistence.*;
import lombok.*;

import java.util.List;

@Entity
@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Table(name = "tiles")
public class Tile {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "bonus", nullable = false, length = 10)
    private BonusType bonus;

    @Column(name = "letter", nullable = false, length = 10)
    private LetterType letter;

    @Column(name = "multiplier", nullable = false)
    private Short multiplier;

    @OneToMany(mappedBy = "tile")
    private List<Grid> grids;
}
