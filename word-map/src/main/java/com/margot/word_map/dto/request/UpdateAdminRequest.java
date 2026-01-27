package com.margot.word_map.dto.request;

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
@Schema(description = "Запрос для обновления админа (роль и права)")
public class UpdateAdminRequest {

    @Schema(description = "массив идентификаторов прав, указываются для роли MODERATOR",
            example = "1, 2, 3")
    private List<Long> ruleId;
}
