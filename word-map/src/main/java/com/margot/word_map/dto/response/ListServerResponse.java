package com.margot.word_map.dto.response;

import com.margot.word_map.dto.PageDto;
import com.margot.word_map.dto.ServerDto;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Schema(description = "Список серверов с информацией о пагинации")
public class ListServerResponse {

    @Schema(description = "Список серверов")
    private List<ServerDto> content;

    @Schema(description = "Информация о текущей странице")
    private PageDto pageble;

    @Schema(description = "Общее количество страниц", example = "18")
    private Integer totalPages;

    @Schema(description = "Общее количество найденных объектов", example = "55")
    private Long totalElements;
}
