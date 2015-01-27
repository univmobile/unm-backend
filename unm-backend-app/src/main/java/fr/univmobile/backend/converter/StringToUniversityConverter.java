package fr.univmobile.backend.converter;

import fr.univmobile.backend.domain.University;
import fr.univmobile.backend.domain.UniversityRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.convert.converter.Converter;

public class StringToUniversityConverter implements Converter<String, University> {

    @Autowired
    UniversityRepository universityRepository;

    @Override
    public University convert(String id) {
        University university = null;
        try {
            university = universityRepository.findOne(new Long(id));
        } catch (NumberFormatException e) {
        }
        return university;
    }
}
