package com.margot.word_map.service.map;

import com.margot.word_map.model.map.Grid;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Service
@Slf4j
public class GridBatchSaver {
    @PersistenceContext
    private EntityManager entityManager;

    @Transactional
    public void saveInBatches(List<Grid> grids, int batchSize, String tableName) {

        for (int i = 0; i < grids.size(); i += batchSize) {
            StringBuilder insertSql = new StringBuilder(
                    "INSERT INTO " + tableName + " (point, letter, platform_id, world_id) VALUES ");
            List<String> values = new ArrayList<>();
            for (int j = i; j < Math.min(i + batchSize, grids.size()); j++) {
                Grid grid = grids.get(j);
                String point = grid.getPoint() != null ? "ST_GeomFromText('POINT(%f %f)', 0)".formatted(
                        grid.getPoint().getX(), grid.getPoint().getY()) : "NULL";
                String letter = grid.getLetter() != null ? "'" + grid.getLetter() + "'" : "NULL";
                Long platformId = grid.getPlatform() != null ? grid.getPlatform().getId() : null;
                Long worldId = grid.getWorld().getId();
                values.add("(%s, %s, %s, %s)".formatted(point, letter, platformId, worldId));
            }
            insertSql.append(String.join(",", values));
            entityManager.createNativeQuery(insertSql.toString()).executeUpdate();
        }

        log.info("Saved {} grids to {}", grids.size(), tableName);
    }
}

