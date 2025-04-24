package com.margot.word_map.service.map;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.margot.word_map.dto.GridDto;
import com.margot.word_map.dto.request.WordAndLettersWithCoordinates;
import com.margot.word_map.exception.BadAttemptToMakeTheWord;
import com.margot.word_map.exception.BaseIsNotEmptyExceptions;
import com.margot.word_map.mapper.GridMapper;
import com.margot.word_map.model.User;
import com.margot.word_map.model.map.Grid;
import com.margot.word_map.model.map.Letter;
import com.margot.word_map.model.map.Tile;
import com.margot.word_map.repository.UserRepository;
import com.margot.word_map.repository.map.GridRepository;
import com.margot.word_map.repository.map.LetterRepository;
import com.margot.word_map.repository.map.TileRepository;
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
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class GridService {

    private final GridRepository gridRepository;
    private final ObjectMapper objectMapper;
    private final GridMapper gridMapper;
    private final GridBatchSaver gridBatchSaver;
    private final LetterRepository letterRepository;
    private final UserRepository userRepository;
    private final TileRepository tileRepository;

    @PersistenceContext
    private EntityManager entityManager;

    @Transactional
    public void update(WordAndLettersWithCoordinates word, User user) {
        List<Grid> titles = new ArrayList<>();
        for (int i = 0; i < word.getWord().length(); i++) {
            Optional<Grid> optimalMapTitle = gridRepository.findByPoint(
                    convertToPoint(word.getLettersWithCoordinates().get(i).getPosition().getX(),
                    word.getLettersWithCoordinates().get(i).getPosition().getY()));
            if (optimalMapTitle.isPresent()) {
                Grid mapTitle = optimalMapTitle.get();
                mapTitle.setLetter(word.getLettersWithCoordinates().get(i).getLetter());
                mapTitle.setUser(user);
                mapTitle.setLetterObj(letterRepository.findByLetter(
                        word.getLettersWithCoordinates().get(i).getLetter().toString()));
                gridRepository.saveAll(titles);
            } else {
                throw new NoSuchElementException("Нет такой клетки");
            }
        }
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

    public void createMap(int radius, int batchSize) {
        LocalDateTime start = LocalDateTime.now();
        if (!gridRepository.existsBy()) {
            List<Grid> grids = createList(radius);
            gridBatchSaver.saveInBatches(grids, batchSize);
        } else {
            throw new BaseIsNotEmptyExceptions("Ошибка создания, таблица не пустая");
        }
        Duration durationBetween = Duration.between(start, LocalDateTime.now());
        log.info("Table created in {} seconds", durationBetween.getSeconds());
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

    //Тестовый метод проверить скорость работы
    public List<Grid> createRandomList(int radius) {
        List<Grid> grids = new ArrayList<>();
        User user = entityManager.find(User.class, 1L);
        Letter letter = entityManager.find(Letter.class, (short) 13);
        Tile tile = entityManager.find(Tile.class, (short) 2);
        for (int x = -radius; x < radius; x++) {
            for (int y = -radius; y < radius; y++) {
                Grid grid = Grid.builder()
                        .point(convertToPoint(x, y))
                        .user(user)
                        .tile(tile)
                        .letter(letter.getLetter().charAt(0))
                        .letterObj(letter)
                        .build();
                grids.add(grid);
            }
        }
        log.info("Создано {} Grid объектов", grids.size());
        return grids;
    }

    public void createRandomMap(int radius, int batchSize) {
        LocalDateTime start = LocalDateTime.now();
        if (!gridRepository.existsBy()) {
            List<Grid> grids = createRandomList(radius);
            gridBatchSaver.saveInBatches(grids, batchSize);
        } else {
            throw new BaseIsNotEmptyExceptions("Ошибка создания, таблица не пустая");
        }
        Duration durationBetween = Duration.between(start, LocalDateTime.now());
        log.info("Table created in {} seconds", durationBetween.getSeconds());
    }

    @Transactional
    public void truncateTable() {
        entityManager.createNativeQuery("TRUNCATE TABLE grid RESTART IDENTITY").executeUpdate();
    }

    public File getTableJson() throws IOException {
        File outputFile = new File(LocalDate.now() + "_grid_export.json");
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(outputFile))) {
            writer.write("[\n");

            int page = 0;
            boolean first = true;
            while (true) {
                Page<Grid> gridPage = gridRepository.findAll(PageRequest.of(page, 500));
                List<GridDto> gridDtoPage = gridPage.stream().map(gridMapper::convertToGridDto).toList();
                if (gridPage.isEmpty()) break;

                for (GridDto gridDto : gridDtoPage) {
                    if (!first) writer.write(",\n");
                    else first = false;

                    try {
                        String json = objectMapper.writeValueAsString(gridDto);
                        writer.write(json);
                    } catch (JsonProcessingException e) {
                        log.warn("Ошибка сериализации объекта с id {}: {} \n {}",
                                gridDto.getId(),
                                e.getMessage(),
                                Arrays.stream(e.getStackTrace())
                                        .limit(5).map(StackTraceElement::toString).collect(Collectors.joining("\n")));
                    }
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
