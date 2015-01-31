package fr.univmobile.backend.hateoas.processor;

import fr.univmobile.backend.domain.University;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.hateoas.Link;
import org.springframework.hateoas.Resource;
import org.springframework.hateoas.ResourceProcessor;

public class UniversityResourceProcessor implements ResourceProcessor<Resource<University>> {

    @Value("${baseURL}")
    private String baseUrl;
    @Value("${universitiesLogoBaseUrl}")
    private String universitiesLogoBaseUrl;

    @Override
    public Resource<University> process(Resource<University> resource) {
    	University university = resource.getContent();
    	if (university.getLogoUrl() != null && !university.getLogoUrl().isEmpty()) {
    		resource.add(new Link(String.format("%s/%s", universitiesLogoBaseUrl, university.getLogoUrl()), "logo"));
    	}
        return resource;
    }
}