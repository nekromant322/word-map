package com.margot.word_map.service.map;

import com.margot.word_map.repository.map.WorldRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class WorldService {

    private final WorldRepository worldRepository;
}
