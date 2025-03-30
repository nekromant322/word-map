package com.margot.word_map.dto;

import lombok.Data;

@Data
public class ConfirmRequest {
    private String email;
    private String code;
}