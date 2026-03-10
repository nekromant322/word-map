package com.margot.word_map.controller.rest;

import com.margot.word_map.controller.rest.api.ServerApi;
import com.margot.word_map.dto.request.CreateServerRequest;
import com.margot.word_map.service.server.ServerService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/server")
public class ServerController implements ServerApi {

    private final ServerService serverService;

    @PostMapping
    public void createServer(@RequestBody @Valid CreateServerRequest createServerRequest) {
        serverService.createServer(createServerRequest);
    }
}
