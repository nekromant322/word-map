package com.margot.word_map.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class GridDto {

    private Long id;

    private double coordinateX;

    private double coordinateY;

    private Character letter;

    private Long userId;

    private Short tileId;

    private Short letterId;
}
