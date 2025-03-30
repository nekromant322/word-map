package com.margot.word_map.dto;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class ConfirmResponse {
    private Long confirmID;
    private LocalDateTime lifetime;
}
