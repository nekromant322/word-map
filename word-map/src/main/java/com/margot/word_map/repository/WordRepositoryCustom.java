package com.margot.word_map.repository;

import com.margot.word_map.dto.request.DictionaryListRequest;

import java.util.List;

public interface WordRepositoryCustom {

    List<String> findAllByFilter(DictionaryListRequest request);
}
