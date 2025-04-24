package com.margot.word_map.service.map;

import com.margot.word_map.model.map.Grid;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Slf4j
public class GridBatchSaver {
    @PersistenceContext
    private EntityManager entityManager;

    @Transactional
    public void saveInBatches(List<Grid> grids, int batchSize) {
        for (int i = 0; i < grids.size(); i++) {
            entityManager.persist(grids.get(i));
            if (i % batchSize == 0 && i > 0) {
                entityManager.flush();
                entityManager.clear();
            }
        }
        entityManager.flush();
        entityManager.clear();
        log.info("saved {}", grids.size());
    }
}

