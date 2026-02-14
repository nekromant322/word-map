package com.margot.word_map.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serial;
import java.io.Serializable;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class PatternCell implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    private int x;
    private int y;
    private int tileId;
}
