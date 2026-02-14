package com.margot.word_map.model;

import com.margot.word_map.model.enums.PatternType;
import io.hypersistence.utils.hibernate.type.json.JsonBinaryType;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.Type;

import java.time.LocalDateTime;
import java.util.List;

@Entity
@Table(name = "patterns")
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class Pattern {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Type(JsonBinaryType.class)
    @Column(columnDefinition = "jsonb", nullable = false)
    private List<PatternCell> cells;

    @Column(name = "is_draft", nullable = false)
    private Boolean isDraft;

    @Column(name = "pattern_type", nullable = false)
    private PatternType patternType;

    @Column(name = "weight", nullable = false)
    private Short weight;

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;
}
