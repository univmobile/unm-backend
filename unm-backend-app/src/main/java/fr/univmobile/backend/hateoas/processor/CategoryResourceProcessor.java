package fr.univmobile.backend.hateoas.processor;

import fr.univmobile.backend.domain.Category;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.hateoas.Link;
import org.springframework.hateoas.Resource;
import org.springframework.hateoas.ResourceProcessor;

public class CategoryResourceProcessor implements ResourceProcessor<Resource<Category>> {

    @Value("${baseURL}")
    private String baseUrl;
    @Value("${categoriesIconsBaseUrl}")
    private String categoriesIconsBaseUrl;

    @Override
    public Resource<Category> process(Resource<Category> resource) {
    	Category category = resource.getContent();
    	if (category.getActiveIconUrl() != null && !category.getActiveIconUrl().isEmpty()) {
    		resource.add(new Link(String.format("%s/%s", categoriesIconsBaseUrl, category.getActiveIconUrl()), "activeIcon"));
    	}
    	if (category.getInactiveIconUrl() != null && !category.getInactiveIconUrl().isEmpty()) {
    		resource.add(new Link(String.format("%s/%s", categoriesIconsBaseUrl, category.getInactiveIconUrl()), "inactiveIcon"));
    	}
    	if (category.getMarkerIconUrl() != null && !category.getMarkerIconUrl().isEmpty()) {
    		resource.add(new Link(String.format("%s/%s", categoriesIconsBaseUrl, category.getMarkerIconUrl()), "markerIcon"));
    	}
        return resource;
    }
}