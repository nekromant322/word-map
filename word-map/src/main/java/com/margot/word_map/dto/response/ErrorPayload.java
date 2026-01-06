package com.margot.word_map.dto.response;

import java.time.LocalDateTime;

public record ErrorPayload(
        String code,
        String message,
        LocalDateTime timestamp
) { }
