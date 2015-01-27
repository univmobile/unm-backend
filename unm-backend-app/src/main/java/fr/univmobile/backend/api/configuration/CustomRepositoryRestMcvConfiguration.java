package fr.univmobile.backend.api.configuration;

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

    @Override
    protected void configureConversionService(ConfigurableConversionService conversionService) {
        conversionService.addConverter(stringToUniversityConverter);
    }

    @Override
    protected void configureRepositoryRestConfiguration(RepositoryRestConfiguration config) {
        config.exposeIdsFor(
                Feed.class,
                Menu.class,
                Notification.class
        );
    }
}
