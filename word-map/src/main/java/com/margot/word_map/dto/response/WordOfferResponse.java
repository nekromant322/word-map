package com.margot.word_map.dto.response;

import com.margot.word_map.model.WordOffer;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class WordOfferResponse {

    private Long id;

    private String word;
}
