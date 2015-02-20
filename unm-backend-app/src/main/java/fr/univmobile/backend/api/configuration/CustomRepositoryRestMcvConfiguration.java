package fr.univmobile.backend.api.configuration;

import fr.univmobile.backend.converter.StringToPoiConverter;
import fr.univmobile.backend.converter.StringToUniversityConverter;
import fr.univmobile.backend.domain.*;
import fr.univmobile.backend.validator.BeforeCreateCommentValidator;
import fr.univmobile.backend.validator.BeforeCreateLinkValidator;
import fr.univmobile.backend.validator.BeforeCreatePoiValidator;
import fr.univmobile.backend.validator.BeforeCreateUniversityLibraryValidator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.convert.support.ConfigurableConversionService;
import org.springframework.data.rest.core.config.RepositoryRestConfiguration;
import org.springframework.data.rest.core.event.ValidatingRepositoryEventListener;
import org.springframework.data.rest.webmvc.config.RepositoryRestMvcConfiguration;

@Configuration
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

    @Override
    protected void configureValidatingRepositoryEventListener(ValidatingRepositoryEventListener v) {
        v.addValidator("beforeCreate", new BeforeCreateCommentValidator());
        v.addValidator("beforeCreate", new BeforeCreatePoiValidator());
        v.addValidator("beforeCreate", new BeforeCreateLinkValidator());
        v.addValidator("beforeCreate", new BeforeCreateUniversityLibraryValidator());
    }
}
