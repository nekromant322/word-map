package com.margot.word_map.model;

import com.margot.word_map.model.map.Letter;
import jakarta.persistence.*;
import lombok.*;

import java.util.ArrayList;
import java.util.List;

@Entity
@Setter
@Getter
@ToString(exclude = { "letters" })
@EqualsAndHashCode(of = { "prefix" })
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Table(name = "languages")
public class Language {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "prefix", unique = true)
    private String prefix;

    @Column(name = "name", unique = true)
    private String name;

    @OneToMany(mappedBy = "language", cascade = CascadeType.ALL)
    @Builder.Default
    @OrderBy("id")
    private List<Letter> letters = new ArrayList<>();
}
