package com.margot.word_map.dto.request;

import lombok.Data;

import java.util.List;

@Data
public class LettersWithCoordinatesRequest {

    private String word;

    private List<Character> letters;

    private List<CoordinatesXY> coordinates;
}


