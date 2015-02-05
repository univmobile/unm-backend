package fr.univmobile.backend.api.serializers;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.fasterxml.jackson.databind.ObjectMapper;

import fr.univmobile.backend.api.serializers.PoiMixIn;
import fr.univmobile.backend.domain.Comment;
import fr.univmobile.backend.domain.ImageMap;
import fr.univmobile.backend.domain.Poi;
import fr.univmobile.backend.domain.University;

@Configuration
public class JacksonConfig {

	@Bean
	public ObjectMapper mapper() {
		final ObjectMapper mapper = new ObjectMapper();
		mapper.addMixInAnnotations(Poi.class, PoiMixIn.class);
		mapper.addMixInAnnotations(ImageMap.class, ImageMapMixIn.class);
		mapper.addMixInAnnotations(Comment.class, CommentMixIn.class);
		mapper.addMixInAnnotations(University.class, UniversityMixIn.class);
		
		return mapper; 
	}  

}