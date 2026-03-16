package com.margot.word_map.mapper;

import com.margot.word_map.dto.PageDto;
import org.springframework.stereotype.Component;

@Component
public class PageMapper {

    public PageDto toDto(int numberPage, int pageSize) {
        return PageDto.builder()
                .numberPage(numberPage)
                .pageSize(pageSize)
                .build();
    }
}
