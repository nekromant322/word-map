package com.margot.word_map.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class PageDto {
    @NotNull
    @Schema(description = "Номер возвращаемой страницы.", example = "3")
    private Integer numberPage;

    @NotNull
    @Schema(description = "Количество возвращаемых объектов на странице.", example = "20")
    private Integer pageSize;
}
