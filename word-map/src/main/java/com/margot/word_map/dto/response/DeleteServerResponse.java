package com.margot.word_map.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Schema(description = "Ответ, содержащий данные удаленного сервера")
public class DeleteServerResponse {

    private Long id;

    private String language;

    private String platform;
}
