package com.margot.word_map.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.margot.word_map.model.Admin;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class AdminListQueryDto {

    private Long id;

    private String email;

    private Admin.ROLE role;

    private Boolean access;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "dd.MM.yyyy HH:mm")
    private LocalDateTime dateActive;
}
