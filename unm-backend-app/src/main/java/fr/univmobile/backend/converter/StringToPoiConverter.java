package fr.univmobile.backend.converter;

import fr.univmobile.backend.domain.Poi;
import fr.univmobile.backend.domain.PoiRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.convert.converter.Converter;

public class StringToPoiConverter implements Converter<String, Poi> {

    @Autowired
    PoiRepository poiRepository;

    @Override
    public Poi convert(String id) {
        Poi poi = null;
        try {
            poi = poiRepository.findOne(new Long(id));
        } catch (NumberFormatException e) {
        }
        return poi;
    }
}
