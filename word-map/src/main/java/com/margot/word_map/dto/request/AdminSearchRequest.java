package com.margot.word_map.dto.request;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class AdminSearchRequest {

    private String search;

    private String sortingType;

    private String filterRole;

    private Long filterLanguage;

    private Boolean filterAccess;
}
