package com.margot.word_map.controller.rest;

import com.margot.word_map.controller.rest.api.PlatformApi;
import com.margot.word_map.dto.PlatformDto;
import com.margot.word_map.dto.request.CreatePlatformRequest;
import com.margot.word_map.service.platform.PlatformService;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/platform")
public class PlatformController implements PlatformApi {

    private final PlatformService platformService;

    public PlatformController(PlatformService platformService) {
        this.platformService = platformService;
    }

    @ResponseStatus(HttpStatus.CREATED)
    @PostMapping
    public PlatformDto createPlatform(CreatePlatformRequest request) {
        return platformService.createPlatform(request);
    }
}
