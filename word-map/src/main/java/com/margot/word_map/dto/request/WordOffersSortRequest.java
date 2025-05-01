package com.margot.word_map.dto.request;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class WordOffersSortRequest {

    private int page = 0;
    private int size = 10;
    private String sortBy = "createdAt";
    private String sortDir = "";
    private String status = "UNCHECKED";
}
