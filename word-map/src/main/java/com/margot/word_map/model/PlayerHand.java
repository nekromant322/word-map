package com.margot.word_map.model;

import com.margot.word_map.model.map.Letter;
import jakarta.persistence.*;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.time.LocalDateTime;

@Entity
@Table(name = "player_hand")
@ToString(exclude = {"player", "letter"})
@Getter
@Setter
public class PlayerHand {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "player_id", nullable = false)
    private Player player;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "letter_id")
    private Letter letter;

    @Min(1)
    @Max(10)
    @Column(name = "priority", nullable = false)
    private Integer priority;

    @Column(name = "is_premium", nullable = false)
    private Boolean isPremium;

    @Column(name = "expiry_at")
    private LocalDateTime expiryAt;
}
