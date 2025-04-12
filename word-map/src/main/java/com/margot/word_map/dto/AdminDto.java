package com.margot.word_map.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class AdminDto {

    private Long id;

    private String email;

    private LocalDateTime dateCreation;

    private LocalDateTime dateActive;

    private String role;

    private Boolean access;

    private List<RuleDto> adminRules;
}
