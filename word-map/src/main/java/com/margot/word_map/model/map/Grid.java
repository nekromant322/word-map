package com.margot.word_map.model.map;

import com.margot.word_map.model.User;
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

    @Column(columnDefinition = "geometry(Point,0)")
    private Point point;

    private Character letter;

    @Column(name = "user_id")
    @ManyToOne
    @JoinColumn(name = "user_id", referencedColumnName = "id")
    private User user;

    @Column(name = "tile_id")
    @ManyToOne
    @JoinColumn(name = "tile_id", referencedColumnName = "id")
    private Tile tile;

    @Column(name = "letter_id")
    @ManyToOne
    @JoinColumn(name = "letter_id", referencedColumnName = "id")
    private Letter letterObj;
}
