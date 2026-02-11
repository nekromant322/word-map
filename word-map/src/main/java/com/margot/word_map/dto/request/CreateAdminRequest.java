package com.margot.word_map.dto.request;

import com.margot.word_map.validation.annotation.ValidEmail;
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
@Schema(description = "Запрос создания администратора")
public class CreateAdminRequest {

    @ValidEmail
    @Schema(description = "Email пользователя", example = "example@mail.com")
    private String email;

    @Schema(description = "Массив идентификаторов прав", example = "1, 2, 3")
    private List<Long> ruleID;
}