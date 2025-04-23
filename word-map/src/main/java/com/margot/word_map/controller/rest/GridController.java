package com.margot.word_map.controller.rest;

import com.margot.word_map.dto.request.WordAndLettersWithCoordinates;
import com.margot.word_map.model.User;
import com.margot.word_map.service.map.GridService;
import com.margot.word_map.service.word.WordService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.core.io.InputStreamResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;

@RestController
@RequestMapping("/map")
@RequiredArgsConstructor
@Validated
public class GridController {

    private final GridService gridService;
    private final WordService wordService;

    @PostMapping("/word")
    public void sendWord (@Valid @RequestBody WordAndLettersWithCoordinates word, UserDetails userDetails) {
        checkAndSave(word, userDetails);
    }

    private void checkAndSave(WordAndLettersWithCoordinates word, UserDetails userDetails) {
        User user = (User) userDetails;
        wordService.getWordInfo(word.getWord());
        gridService.check(word);
        gridService.update(word, user);
    }

    @GetMapping("/new")
    public void createMap() {
        gridService.createRandomMap(10, 500);
    }

    @DeleteMapping("/end")
    public File delete() throws IOException {
        File file = gridService.getTableJson();
        gridService.truncateTable();
        return file;
    }

    @GetMapping("/export")
    public ResponseEntity<Resource> exportGridData() throws IOException {
        File file = gridService.getTableJson();

        InputStreamResource resource = new InputStreamResource(new FileInputStream(file));

        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=" + file.getName())
                .contentLength(file.length())
                .contentType(MediaType.APPLICATION_OCTET_STREAM)
                .body(resource);
    }
}
