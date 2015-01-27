package fr.univmobile.backend.api.configuration;

import fr.univmobile.backend.converter.StringToPoiConverter;
import fr.univmobile.backend.converter.StringToUniversityConverter;
import fr.univmobile.backend.domain.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.convert.support.ConfigurableConversionService;
import org.springframework.data.rest.core.config.RepositoryRestConfiguration;
import org.springframework.data.rest.webmvc.config.RepositoryRestMvcConfiguration;
import org.springframework.stereotype.Component;

@Component
public class CustomRepositoryRestMcvConfiguration extends RepositoryRestMvcConfiguration {

    @Autowired
    StringToUniversityConverter stringToUniversityConverter;
    @Autowired
    StringToPoiConverter stringToPoiConverter;

    @Override
    protected void configureConversionService(ConfigurableConversionService conversionService) {
        conversionService.addConverter(stringToUniversityConverter);
        conversionService.addConverter(stringToPoiConverter);
    }

    @Override
    protected void configureRepositoryRestConfiguration(RepositoryRestConfiguration config) {
        config.exposeIdsFor(
                Bookmark.class,
                Category.class,
                Comment.class,
                Feed.class,
                ImageMap.class,
                Menu.class,
                News.class,
                Notification.class,
                Poi.class,
                Region.class,
                RestoMenu.class,
                University.class,
                UsageStat.class,
                User.class
        );
    }
}
