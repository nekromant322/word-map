package com.margot.word_map.model.map;

import com.margot.word_map.model.Player;
import jakarta.persistence.*;
import lombok.*;
import org.locationtech.jts.geom.Point;

@Entity
@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class Grid {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(columnDefinition = "geometry(Point,0)", nullable = false)
    private Point point;

    private Character letter;

    @ManyToOne
    @JoinColumn(name = "player_id", referencedColumnName = "id")
    private Player player;

    @ManyToOne
    @JoinColumn(name = "tile_id", referencedColumnName = "id", nullable = false)
    private Tile tile;

    @ManyToOne
    @JoinColumn(name = "letter_id", referencedColumnName = "id")
    private Letter letterObj;
}
