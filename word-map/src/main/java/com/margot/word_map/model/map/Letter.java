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
@Table(name = "letters")
public class Letter {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Short id;

    private String language;

    private String letter;

    private String type;

    private Short multiplier;

    private Short weight;

    @OneToMany(mappedBy = "letterObj")
    private List<Grid> grids;
}
