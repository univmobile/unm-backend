package fr.univmobile.backend.hateoas.processor;

import fr.univmobile.backend.domain.Menu;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.hateoas.Link;
import org.springframework.hateoas.Resource;
import org.springframework.hateoas.ResourceProcessor;

public class MenuResourceProcessor implements ResourceProcessor<Resource<Menu>> {

    private static String UNIVERSITIES_PATH = "api/universities/";

    @Value("${baseURL}")
    private String baseUrl;

    @Override
    public Resource<Menu> process(Resource<Menu> resource) {
        Menu menu = resource.getContent();
        if (menu.getUniversity() != null) {
            resource.add(new Link(baseUrl + UNIVERSITIES_PATH + menu.getUniversity().getId(), "universityLink"));
        }
        return resource;
    }
}
