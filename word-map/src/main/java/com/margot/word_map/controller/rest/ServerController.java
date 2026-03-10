package com.margot.word_map.controller.rest;

import com.margot.word_map.controller.rest.api.ServerApi;
import com.margot.word_map.dto.request.CreateServerRequest;
import com.margot.word_map.dto.request.UpdateServerRequest;
import com.margot.word_map.dto.response.DeleteServerResponse;
import com.margot.word_map.service.server.ServerService;
import io.swagger.v3.oas.annotations.Parameter;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/server")
public class ServerController implements ServerApi {

    private final ServerService serverService;

    @PostMapping
    public void createServer(@RequestBody @Valid CreateServerRequest createServerRequest) {
        serverService.createServer(createServerRequest);
    }

    @PatchMapping("/{id}")
    public void updateServerName(@RequestBody @Validated UpdateServerRequest request,
                                 @Parameter(description = "id сервера", example = "10")
                                 @PathVariable Long id) {
        serverService.updateServerName(request, id);
    }

    @DeleteMapping("/{id}/close")
    public void closeServer(@Parameter(description = "id сервера", example = "10")
                            @PathVariable Long id) {
        serverService.closeServer(id);
    }

    @DeleteMapping("/{id}/wipe")
    public void wipeServer(@Parameter(description = "id сервера", example = "10")
                           @PathVariable Long id) {
        serverService.wipeServer(id);
    }

    @DeleteMapping("/{id}")
    public DeleteServerResponse deleteServer(@Parameter(description = "id сервера", example = "10")
                                             @PathVariable Long id) {
        return serverService.deleteServer(id);
    }
}
