package fr.univmobile.backend.api.configuration;

import fr.univmobile.backend.converter.StringToCategoryConverter;
import fr.univmobile.backend.converter.StringToPoiConverter;
import fr.univmobile.backend.converter.StringToUniversityConverter;
import fr.univmobile.backend.hateoas.assembler.*;
import fr.univmobile.backend.hateoas.processor.FeedResourceProcessor;
import fr.univmobile.backend.hateoas.processor.MenuResourceProcessor;
import fr.univmobile.backend.hateoas.processor.NotificationResourceProcessor;
import fr.univmobile.backend.hateoas.processor.PoiResourceProcessor;
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
    public PoiResourceProcessor getPoiResourceProcessor(){
        return new PoiResourceProcessor();
    }

    @Bean
    public StringToUniversityConverter getStringToUniversityConverter(){
        return new StringToUniversityConverter();
    }

    @Bean
    public StringToPoiConverter getStringToPoiConverter(){
        return new StringToPoiConverter();
    }

    @Bean
    public StringToCategoryConverter getStringToCategoryConverter(){
        return new StringToCategoryConverter();
    }
}