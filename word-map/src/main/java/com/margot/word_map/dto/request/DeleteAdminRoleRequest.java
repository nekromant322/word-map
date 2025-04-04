package com.margot.word_map.dto.request;

import com.margot.word_map.model.Role;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class DeleteAdminRoleRequest {

    @NotNull(message = "Id админа не может быть null")
    private Long adminId;

    @NotNull(message = "role не может быть null")
    private Role.ROLE role;
}
