package com.margot.word_map.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class PagedResponseDto<T> {

    List<T> content;

    PageableResponseDto pageable;

    int totalPages;

    long totalElements;

    public static <T> PagedResponseDto<T> fromPage(Page<T> page) {
        return PagedResponseDto.<T>builder()
                .content(page.getContent())
                .pageable(PageableResponseDto.fromPageable(page.getPageable()))
                .totalPages(page.getTotalPages())
                .totalElements(page.getTotalElements())
                .build();
    }

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    public static class PageableResponseDto {

        private int pageNumber;
        private int pageSize;

        public static PageableResponseDto fromPageable(Pageable pageable) {
            return new PageableResponseDto(pageable.getPageNumber(), pageable.getPageSize());
        }
    }
}
