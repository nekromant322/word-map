package com.margot.word_map.dto.response;

import com.margot.word_map.dto.AdminDto;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.domain.Page;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class GetAdminsResponse {

    private Long count;

    private Integer page;

    private Integer itemsOnPage;

    private Page<AdminDto> admins;
}
