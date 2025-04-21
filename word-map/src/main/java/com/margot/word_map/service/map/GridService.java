package com.margot.word_map.service.map;

import com.margot.word_map.dto.request.WordAndLettersWithCoordinates;
import com.margot.word_map.exception.BadAttemptToMakeTheWord;
import com.margot.word_map.model.User;
import com.margot.word_map.model.map.Grid;
import com.margot.word_map.repository.map.GridRepository;
import lombok.RequiredArgsConstructor;
import org.locationtech.jts.geom.Coordinate;
import org.locationtech.jts.geom.GeometryFactory;
import org.locationtech.jts.geom.Point;
import org.locationtech.jts.geom.PrecisionModel;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class GridService {

    private final GridRepository gridRepository;

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

    private Point convertToPoint(double x, double y) {
        GeometryFactory geometryFactory = new GeometryFactory(new PrecisionModel(), 0);
        return geometryFactory.createPoint(new Coordinate(x, y));
    }
}
