package com.margot.word_map.model.map;

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
    private Short id;

    private String bonus;

    private String letter;

    private Short multiplier;

    @OneToMany(mappedBy = "tile")
    private List<Grid> grids;
}
