package com.margot.word_map.model;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

import java.time.LocalDateTime;

@Entity
@Table(name = "servers")
@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@ToString(exclude = {"platform", "language"})
public class Server {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "platform_id", nullable = false)
    @OnDelete(action = OnDeleteAction.CASCADE)
    private Platform platform;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "language_id", nullable = false)
    @OnDelete(action = OnDeleteAction.CASCADE)
    private Language language;

    @Column(name = "name", length = 100)
    private String name;

    @Column(name = "wipe_count", nullable = false)
    private Integer wipeCount = 0;

    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "wiped_at")
    private LocalDateTime wipedAt;

    @Column(name = "closed_at")
    private LocalDateTime closedAt;

    @Column(name = "is_open", nullable = false)
    private Boolean isOpen = true;
}