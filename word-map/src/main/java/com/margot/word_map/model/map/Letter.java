package com.margot.word_map.model.map;

import com.margot.word_map.model.Language;
import com.margot.word_map.model.LetterType;
import jakarta.persistence.*;
import lombok.*;

import java.util.List;

@Entity
@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
@ToString(exclude = { "grids", "language" })
@Builder
@Table(name = "letters")
public class Letter {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "language_id")
    private Language language;

    @Column(name = "letter", nullable = false)
    private Character letter;

    @Column(name = "type", length = 10)
    private LetterType type;

    @Column(name = "multiplier", nullable = false)
    private Short multiplier;

    @Column(name = "weight", nullable = false)
    private Short weight;

    @OneToMany(mappedBy = "letterObj")
    private List<Grid> grids;

    @PrePersist
    public void normalizeLetter() {
        if (this.letter != null) {
            this.letter = Character.toUpperCase(this.letter);
        }
    }
}
