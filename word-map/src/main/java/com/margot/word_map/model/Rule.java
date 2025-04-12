package com.margot.word_map.model;

import jakarta.persistence.*;
import lombok.*;
import org.springframework.security.core.GrantedAuthority;

@Entity
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Getter
@Setter
@ToString
@Table(name = "roles")
public class Rule implements GrantedAuthority {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "name")
    @Enumerated(EnumType.STRING)
    private RULE name;

    @Override
    public String getAuthority() {
        return name.name();
    }

    public enum RULE {
        MANAGE_DICTIONARY,
        WIPE_DICTIONARY,
        MANAGE_RATING,
        MANAGE_WORLD,
        MANAGE_ROLE,
        MANAGE_IVENT,
        MANAGE_SHOP
    }
}