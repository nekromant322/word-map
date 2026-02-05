package com.margot.word_map.model;

import com.margot.word_map.model.enums.Role;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

@Entity
@Getter
@Setter
@ToString(exclude = {"rules", "languages"})
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Table(name = "admins")
public class Admin {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    @NonNull
    private String email;

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    @Column(name = "date_active")
    private LocalDateTime dateActive;

    @Column(name = "role")
    @Enumerated(EnumType.STRING)
    private Role role;

    @Column(name = "access", nullable = false)
    @Builder.Default
    private boolean accessGranted = true;

    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(
            name = "admin_rules",
            joinColumns = @JoinColumn(name = "admin_id", referencedColumnName = "id"),
            inverseJoinColumns = @JoinColumn(name = "rule_id", referencedColumnName = "id" )

    )
    @Builder.Default
    private Set<Rule> rules = new HashSet<>();

    @OneToMany(mappedBy = "admin", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private Set<AdminLanguage> languages = new HashSet<>();
}
