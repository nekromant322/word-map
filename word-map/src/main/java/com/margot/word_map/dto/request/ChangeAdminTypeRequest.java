package com.margot.word_map.dto.request;

import com.margot.word_map.dto.AdminType;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ChangeAdminTypeRequest {

    @NotNull(message = "adminId не может быть null")
    private Long adminId;

    @NotNull(message = "type не может быть null")
    private AdminType type;
}
