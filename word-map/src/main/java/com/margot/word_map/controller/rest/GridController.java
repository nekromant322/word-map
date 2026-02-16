package com.margot.word_map.controller.rest;

import com.margot.word_map.controller.rest.api.GridApi;
import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/map")
@RequiredArgsConstructor
@Validated
public class GridController implements GridApi {

}
