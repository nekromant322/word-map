package com.margot.word_map.model.map;

import com.margot.word_map.model.Player;
import com.margot.word_map.model.Server;
import com.margot.word_map.model.enums.GridPatternType;
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

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "player_id", referencedColumnName = "id")
    private Player player;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "tile_id", referencedColumnName = "id", nullable = false)
    private Tile tile;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "letter_id", referencedColumnName = "id")
    private Letter letterObj;

    @Enumerated(EnumType.STRING)
    @Column(name = "pattern_type", nullable = false)
    private GridPatternType patternType;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "server_id", nullable = false)
    private Server server;
}
