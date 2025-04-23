package com.margot.word_map.mapper;

import com.margot.word_map.dto.GridDto;
import com.margot.word_map.model.map.Grid;
import org.springframework.stereotype.Component;

@Component
public class GridMapper {

    public GridDto convertToGridDto(Grid grid) {
        return GridDto.builder()
                .id(grid.getId())
                .coordinateX(grid.getPoint().getX())
                .coordinateY(grid.getPoint().getY())
                .letter(grid.getLetter())
                .letterId(grid.getLetterObj().getId())
                .tileId(grid.getTile().getId())
                .userId(grid.getUser().getId())
                .build();
    }
}
