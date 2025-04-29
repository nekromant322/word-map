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
                .platform(grid.getPlatform().getName())
                .letter(grid.getLetter() != null ? grid.getLetter() : null)
                .letterId(grid.getLetterObj() != null ? grid.getLetterObj().getId() : null)
                .tileId(grid.getTile() != null ? grid.getTile().getId() : null)
                .userId(grid.getUser() != null ? grid.getUser().getId() : null)
                .build();
    }
}
