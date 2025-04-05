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
public class Role implements GrantedAuthority {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "role")
    @Enumerated(EnumType.STRING)
    private ROLE role;

    @Column(name = "level")
    @Enumerated(EnumType.STRING)
    private LEVEL level;

    @Column(name = "description")
    private String description;

    @Override
    public String getAuthority() {
        return role.name();
    }

    public enum ROLE {
        ADMIN,
        WORD_FIND,
        WORD_DELETE,
        DICT_CHANGE,
        DICT_DOWNLOAD,
        RATING_CHECK,
        RATING_MANAGEMENT,
        RATING_CLEAN,
        WORLD_CHECK,
        WORLD_MANAGEMENT,
        ROLES_CHECK,
        ROLES_MANAGEMENT,
        EVENTS_CHECK,
        EVENTS_MANAGEMENT,
        LOGS_CHECK
    }

    public enum LEVEL {
        AVAILABLE,
        SETTING,
        FORBIDDEN
    }
}