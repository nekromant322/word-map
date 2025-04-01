package com.margot.word_map.dto.request;

import lombok.Data;

@Data
public class ConfirmRequest {
    private String email;
    private String code;
}