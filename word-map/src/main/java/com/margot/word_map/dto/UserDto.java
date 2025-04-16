package com.margot.word_map.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class UserDto {

    private Long id;

    private String email;

    private String username;

    private LocalDateTime dateCreation;

    private LocalDateTime dateActive;

    private Boolean access;
}
