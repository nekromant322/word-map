package com.margot.word_map.service.map;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.margot.word_map.dto.GridDto;
import com.margot.word_map.dto.request.WordAndLettersWithCoordinates;
import com.margot.word_map.dto.request.WorldRequest;
import com.margot.word_map.exception.BadAttemptToMakeTheWord;
import com.margot.word_map.exception.BaseIsNotEmptyExceptions;
import com.margot.word_map.exception.PlatformNotFoundException;
import com.margot.word_map.mapper.GridMapper;
import com.margot.word_map.model.Admin;
import com.margot.word_map.model.Language;
import com.margot.word_map.model.User;
import com.margot.word_map.model.map.Grid;
import com.margot.word_map.model.map.Platform;
import com.margot.word_map.model.map.World;
import com.margot.word_map.repository.UserRepository;
import com.margot.word_map.repository.map.*;
import com.margot.word_map.service.language.LanguageService;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.Query;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.locationtech.jts.geom.Coordinate;
import org.locationtech.jts.geom.GeometryFactory;
import org.locationtech.jts.geom.Point;
import org.locationtech.jts.geom.PrecisionModel;
import org.springframework.security.core.userdetails.UserDetails;
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
    private final WorldRepository worldRepository;
    private final PlatformRepository platformRepository;
    private final LanguageService languageService;

    private static final GeometryFactory GEOMETRY_FACTORY = new GeometryFactory(new PrecisionModel(), 0);

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

    @Transactional
    public World createWorld(int radius, int batchSize, WorldRequest request) {
        LocalDateTime start = LocalDateTime.now();
        World world;
        if (!isActive(request)) {
            world = createTableGrid(request);
            List<Grid> grids = createList(radius, request.getPlatform(), world.getId());
            gridBatchSaver.saveInBatches(grids, batchSize, "grid_" + world.getId());
        } else {
            throw new BaseIsNotEmptyExceptions("Ошибка создания, таблица уже активна");
        }
        Duration durationBetween = Duration.between(start, LocalDateTime.now());
        log.info("Table created in {} seconds", durationBetween.getSeconds());

        return world;
    }

    @Transactional
    public List<Grid> createList(int radius, Long platformId, Long worldId) {
        List<Grid> grids = new ArrayList<>();
        for (int x = -radius; x < radius; x++) {
            for (int y = -radius; y < radius; y++) {
                Grid grid = Grid.builder()
                        .point(convertToPoint(x, y))
                        .platform(platformRepository.findById(platformId).get())
                        .world(worldRepository.findById(worldId).get())
                        .build();
                grids.add(grid);
            }
        }
        return grids;
    }

    @Transactional
    public World createTableGrid(WorldRequest request) {
        Platform platform = platformRepository.findById(request.getPlatform()).orElseThrow(
                () -> new PlatformNotFoundException("Платформы с таким id нет"));
        String language = languageService.getLanguageById(request.getLanguage()).getName();

        World world = World.builder()
                .platform(platform.getName())
                .language(language)
                .active(true)
                .build();

        World savedWorld = worldRepository.saveAndFlush(world);

        String tableName = "grid_" + savedWorld.getId();
        String createTableSql = """
                CREATE TABLE IF NOT EXISTS %s
                PARTITION OF grid
                FOR VALUES IN (%d);
                """.formatted(
                    tableName, world.getId()
            );
        entityManager.createNativeQuery(createTableSql).executeUpdate();
        log.info("Table {} created successfully", tableName);

        return world;
    }

    @Transactional
    public void dropTable(Long worldId) {
        if (worldId == null || worldId <= 0) {
            throw new IllegalArgumentException("Invalid world ID: " + worldId);
        }
        World world = worldRepository.findById(worldId).orElseThrow(NoSuchElementException::new);
        world.setActive(false);
        worldRepository.save(world);

        String tableName = "grid_" + worldId;
        String dropTableSql = "DROP TABLE " + tableName ;
        entityManager.createNativeQuery(dropTableSql).executeUpdate();
        log.info("Table {} dropped successfully", tableName);
    }

    public boolean isActive(WorldRequest worldRequest) {
        Language language;
        if (languageService.findById(worldRequest.getLanguage()).isPresent()) {
            language = languageService.findById(worldRequest.getLanguage()).get();
        } else {
            throw new NoSuchElementException("Нет языка с таким id");
        }
        Platform platform;
        if (platformRepository.findById(worldRequest.getPlatform()).isPresent()) {
            platform = platformRepository.findById(worldRequest.getPlatform()).get();
        } else {
            throw new NoSuchElementException("Нет платформы с таким id");
        }
        Optional<World> worldOptional =
                worldRepository.findByActiveIsTrueAndLanguageAndPlatform(language.getName(), platform.getName());
        return worldOptional.isPresent();
    }

    public File getTableJson(Long id) throws IOException {
        String tableName = "grid_" + id;

        boolean tableExists = (boolean) entityManager.createNativeQuery(
                "SELECT EXISTS (SELECT FROM information_schema.tables WHERE table_name = :tableName)"
        ).setParameter("tableName", tableName).getSingleResult();
        if (!tableExists) {
            throw new IllegalStateException("Table " + tableName + " does not exist");
        }

        String fileName = String.format("%s_grid_%d_export.json", LocalDate.now().toString(), id);
        File outputFile = new File("/app", fileName);

        try (BufferedWriter writer = new BufferedWriter(new FileWriter(outputFile))) {
            writer.write("[\n");

            Query query = entityManager.createNativeQuery("SELECT * FROM " + tableName, Grid.class);
            List<Grid> grids = query.getResultList();
            boolean first = true;

            for (Grid grid : grids) {
                GridDto gridDto = gridMapper.convertToGridDto(grid);
                if (!first) {
                    writer.write(",\n");
                } else {
                    first = false;
                }

                try {
                    String json = objectMapper.writeValueAsString(gridDto);
                    writer.write(json);
                } catch (JsonProcessingException e) {
                    log.warn("Ошибка сериализации объекта с id {}: {}", gridDto.getId(), e.getMessage());
                }
            }

            writer.write("\n]");
            writer.flush();
            log.info("File {} written successfully with {} records", outputFile.getAbsolutePath(), grids.size());
        } catch (IOException e) {
            log.error("Failed to write to file {}: {}", outputFile.getAbsolutePath(), e.getMessage());
            throw e;
        }

        if (!outputFile.exists()) {
            throw new IllegalStateException("File was not created: " + outputFile.getAbsolutePath());
        }

        return outputFile;
    }

    @Transactional
    public void wipe(UserDetails userDetails, Long id) {
        Admin admin = (Admin) userDetails;
        String tableName = "grid_" + id;
        boolean tableExists = (boolean) entityManager.createNativeQuery(
                "SELECT EXISTS (SELECT FROM information_schema.tables WHERE table_name = :tableName)"
        ).setParameter("tableName", tableName).getSingleResult();
        if (!tableExists) {
            throw new IllegalStateException("Table " + tableName + " does not exist");
        }
        entityManager.createNativeQuery("TRUNCATE TABLE " + tableName + " RESTART IDENTITY").executeUpdate();
        //Todo обавить потом заполнение по патерну
        log.info("{}: WIPE GRID Пользователь {} очистил мир.", LocalDateTime.now(), admin.getEmail());
    }

    private Point convertToPoint(double x, double y) {
        return GEOMETRY_FACTORY.createPoint(new Coordinate(x, y));
    }
}
