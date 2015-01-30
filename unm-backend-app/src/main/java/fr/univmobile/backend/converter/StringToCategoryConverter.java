package fr.univmobile.backend.converter;

import fr.univmobile.backend.domain.Category;
import fr.univmobile.backend.domain.CategoryRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.convert.converter.Converter;

public class StringToCategoryConverter implements Converter<String, Category> {

    @Autowired
    CategoryRepository categoryRepository;

    @Override
    public Category convert(String id) {
        Category category = null;
        try {
            category = categoryRepository.findOne(new Long(id));
        } catch (NumberFormatException e) {
        }
        return category;
    }
}