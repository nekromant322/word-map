package com.margot.word_map.service.map;

import com.margot.word_map.repository.map.TileRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class TileService {

    private final TileRepository tileRepository;
}
