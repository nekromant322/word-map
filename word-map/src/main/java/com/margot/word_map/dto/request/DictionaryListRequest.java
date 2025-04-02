package com.margot.word_map.dto.request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class DictionaryListRequest {

    private String language;

    private Boolean reuse;

    private String lettersUsed;

    private String lettersExclude;

    private Integer wordLength;

    private List<SymbolPosition> positions;
}
