package com.margot.word_map.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ConfirmCodeDto {

    private String code;

    private Long codeId;

    private Integer expirationTime;
}
