package com.margot.word_map.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
public class AdminInfoDto {

    private String email;
    private String role;
    private List<LanguageInfo> languages;
    private List<String> rules;

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    @Builder
    public static class LanguageInfo {

        private Long id;
        private String prefix;
    }
}
