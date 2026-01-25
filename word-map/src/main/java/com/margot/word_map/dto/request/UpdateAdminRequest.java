package com.margot.word_map.dto.request;

import com.margot.word_map.model.Admin;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
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

    @Schema(description = "id админа", example = "12")
    @NotNull(message = "id не может быть null")
    private Long id;

    @Schema(description = "роль, админ/модератор", example = "ADMIN|MODERATOR")
    @NotBlank(message = "role не может быть пустой")
    private Admin.ROLE role;

    @Schema(description = "массив идентификаторов прав, указываются для роли MODERATOR",
            example = "MANAGE_DICTIONARY, " +
                    "WIPE_DICTIONARY, MANAGE_RATING, MANAGE_WORLD," +
                    " MANAGE_ROLE, MANAGE_EVENT, MANAGE_SHOP")
    private List<Long> ruleIds;
}
