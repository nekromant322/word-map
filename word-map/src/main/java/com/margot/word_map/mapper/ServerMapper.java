package com.margot.word_map.mapper;

import com.margot.word_map.dto.ServerDto;
import com.margot.word_map.dto.response.ListServerResponse;
import com.margot.word_map.model.Server;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
public class ServerMapper {

    private final PageMapper pageMapper;

    public ServerDto toDto(Server server) {
        return ServerDto.builder()
                .id(server.getId())
                .platform(server.getPlatform().getName())
                .language(server.getLanguage().getPrefix())
                .name(server.getName())
                .wipeCount(server.getWipeCount())
                .created(server.getCreatedAt())
                .wiped(server.getWipedAt())
                .closed(server.getClosedAt())
                .open(server.getIsOpen())
                .build();
    }

    public List<ServerDto> toDtoList(List<Server> servers) {
        return servers.stream()
                .map(this::toDto)
                .toList();
    }

    public ListServerResponse toResponse(List<Server> servers, Integer numberPage,
                                         Integer pageSize, Integer totalPage, Long totalElement) {
        return ListServerResponse.builder()
                .content(this.toDtoList(servers))
                .pageble(pageMapper.toDto(numberPage, pageSize))
                .totalPages(totalPage)
                .totalElements(totalElement)
                .build();
    }
}
