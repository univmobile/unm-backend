package fr.univmobile.backend.api.configuration;

import fr.univmobile.backend.hateoas.assembler.*;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.data.rest.webmvc.config.RepositoryRestMvcConfiguration;

@Configuration
@Import(RepositoryRestMvcConfiguration.class)
public class SpringConfiguration {

    @Bean
    public CategoryResourceAssembler getCategoryResourceAssembler() {
        return new CategoryResourceAssembler();
    }

    @Bean
    public CommentResourceAssembler getCommentResourceAssembler() {
        return new CommentResourceAssembler();
    }

    @Bean
    public ImageMapDataResourceAssembler getImageMapDataResourceAssembler() {
        return new ImageMapDataResourceAssembler();
    }

    @Bean
    public ImageMapResourceAssembler getImageMapResourceAssembler() {
        return new ImageMapResourceAssembler();
    }

    @Bean
    public PoiResourceAssembler getPoiResourceAssembler() {
        return new PoiResourceAssembler();
    }

    @Bean
    public UserResourceAssembler getUserResourceAssembler() {
        return new UserResourceAssembler();
    }
}