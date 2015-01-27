package fr.univmobile.backend.api.configuration;

import fr.univmobile.backend.converter.StringToUniversityConverter;
import fr.univmobile.backend.hateoas.assembler.*;
import fr.univmobile.backend.hateoas.processor.FeedResourceProcessor;
import fr.univmobile.backend.hateoas.processor.MenuResourceProcessor;
import fr.univmobile.backend.hateoas.processor.NotificationResourceProcessor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
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

    @Bean
    public NotificationStatusAssembler getNotificationStatusAssembler() {
        return new NotificationStatusAssembler();
    }

    @Bean
    public MenuResourceProcessor getUserResourceProcessor(){
        return new MenuResourceProcessor();
    }

    @Bean
    public FeedResourceProcessor getFeedResourceProcessor(){
        return new FeedResourceProcessor();
    }

    @Bean
    public NotificationResourceProcessor getNotificationResourceProcessor(){
        return new NotificationResourceProcessor();
    }

    @Bean
    public StringToUniversityConverter getStringToUniversityConverter(){
        return new StringToUniversityConverter();
    }
}