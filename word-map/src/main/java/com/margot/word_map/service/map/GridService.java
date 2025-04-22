package com.margot.word_map.service.map;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.margot.word_map.dto.request.WordAndLettersWithCoordinates;
import com.margot.word_map.exception.BadAttemptToMakeTheWord;
import com.margot.word_map.exception.BaseIsNotEmptyExceptions;
import com.margot.word_map.model.User;
import com.margot.word_map.model.map.Grid;
import com.margot.word_map.repository.map.GridRepository;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.locationtech.jts.geom.Coordinate;
import org.locationtech.jts.geom.GeometryFactory;
import org.locationtech.jts.geom.Point;
import org.locationtech.jts.geom.PrecisionModel;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class GridService {

    private final GridRepository gridRepository;
    private final ObjectMapper objectMapper;

    @PersistenceContext
    private EntityManager entityManager;

    @Transactional
    public void save(WordAndLettersWithCoordinates word, User user) {
        List<Grid> titles = new ArrayList<>();
        for (int i = 0; i < word.getWord().length(); i++) {
            Grid mapTitle = Grid.builder()
                    .point(convertToPoint(word.getLettersWithCoordinates().get(i).getPosition().getX(),
                            word.getLettersWithCoordinates().get(i).getPosition().getY()))
                    .letter(word.getLettersWithCoordinates().get(i).getLetter())
                    .user(user)
                    .build();
            titles.add(mapTitle);
        }
        gridRepository.saveAll(titles);
    }

    public void check(WordAndLettersWithCoordinates wordAndLettersWithCoordinates) {
        int counter = 0;
        for (int i = 0; i < wordAndLettersWithCoordinates.getWord().length(); i++) {
            Point point = convertToPoint(
                    wordAndLettersWithCoordinates.getLettersWithCoordinates().get(i).getPosition().getX(),
                    wordAndLettersWithCoordinates.getLettersWithCoordinates().get(i).getPosition().getY());
            if (gridRepository.findByPoint(point).isPresent()) {
                counter++;
            }
        }
        if ((counter >= 2) || (counter == 0)) {
            throw new BadAttemptToMakeTheWord("Ошибка расположения");
        }
    }

    @Transactional
    public void createMap(int radius, int batchSize) {
        LocalDateTime start = LocalDateTime.now();
        if (!gridRepository.existsBy()) {
            List<Grid> grids = createList(radius);
            saveInBatches(grids, batchSize);
        } else {
            throw new BaseIsNotEmptyExceptions("Ошибка создания, таблица не пустая");
        }
        Duration durationBetween = Duration.between(start, LocalDateTime.now());
        log.info("Table created in {} seconds", durationBetween.getSeconds());
    }

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
    }

    public List<Grid> createList(int radius) {
        List<Grid> grids = new ArrayList<>();
        for (int x = -radius; x < radius; x++) {
            for (int y = -radius; y < radius; y++) {
                Grid grid = Grid.builder()
                        .point(convertToPoint(x, y))
                        .build();
                grids.add(grid);
            }
        }
        return grids;
    }

    @Transactional
    public void truncateTable() {
        entityManager.createNativeQuery("TRUNCATE TABLE grid RESTART IDENTITY CASCADE").executeUpdate();
    }

    public File getTableJson() throws IOException {
        File outputFile = new File(LocalDate.now() + "_grid_export.json");
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(outputFile))) {
            writer.write("[\n");

            int page = 0;
            boolean first = true;
            while (true) {
                Page<Grid> gridPage = gridRepository.findAll(PageRequest.of(page, 500));
                if (gridPage.isEmpty()) break;

                for (Grid grid : gridPage.getContent()) {
                    if (!first) writer.write(",\n");
                    else first = false;

                    String json = objectMapper.writeValueAsString(grid);
                    writer.write(json);
                }

                page++;
            }
            writer.write("\n]");
        }
        return outputFile;
    }

    private Point convertToPoint(double x, double y) {
        GeometryFactory geometryFactory = new GeometryFactory(new PrecisionModel(), 0);
        return geometryFactory.createPoint(new Coordinate(x, y));
    }
}
