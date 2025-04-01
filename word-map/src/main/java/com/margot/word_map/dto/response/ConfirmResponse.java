package com.margot.word_map.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
public class ConfirmResponse {
    private Long confirmID;
    private Integer lifetime;
}
