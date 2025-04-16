package com.margot.word_map.service.map;

import com.margot.word_map.dto.request.LettersWithCoordinatesRequest;
import com.margot.word_map.map.MapTitle;
import com.margot.word_map.repository.MapTitlesRepository;
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
public class MapTitlesService {

    private final MapTitlesRepository mapTitlesRepository;

    @Transactional
    public void save(LettersWithCoordinatesRequest word) {
        List<MapTitle> titles = new ArrayList<>();
        for (int i = 0; i < word.getWord().length(); i++) {
            MapTitle mapTitle = MapTitle.builder()
                    .point(convertToPoint(word.getCoordinates().get(i).getX(), word.getCoordinates().get(i).getY()))
                    .letter(word.getLetters().get(i))
                    .build();
            titles.add(mapTitle);
        }
        mapTitlesRepository.saveAll(titles);
    }

    private Point convertToPoint(double x, double y) {
        GeometryFactory geometryFactory = new GeometryFactory(new PrecisionModel(), 0);
        return geometryFactory.createPoint(new Coordinate(x, y));
    }
}
