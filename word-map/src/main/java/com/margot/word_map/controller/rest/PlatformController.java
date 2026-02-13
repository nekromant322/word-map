package com.margot.word_map.controller.rest;

import com.margot.word_map.controller.rest.api.PlatformApi;
import com.margot.word_map.dto.PlatformDto;
import com.margot.word_map.dto.request.CreateUpdatePlatformRequest;
import com.margot.word_map.service.platform.PlatformService;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/platform")
public class PlatformController implements PlatformApi {

    private final PlatformService platformService;

    public PlatformController(PlatformService platformService) {
        this.platformService = platformService;
    }

    @ResponseStatus(HttpStatus.CREATED)
    @PostMapping
    public PlatformDto createPlatform(CreateUpdatePlatformRequest request) {
        return platformService.createPlatform(request);
    }

    @PutMapping("/{id}")
    public PlatformDto updatePlatform(@PathVariable("id") Long id, CreateUpdatePlatformRequest request) {
        return platformService.updatePlatform(id, request);
    }
}
