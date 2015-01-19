package fr.univmobile.backend.hateoas.assembler;

import fr.univmobile.backend.ImageMapController;
import fr.univmobile.backend.domain.ImageMap;
import fr.univmobile.backend.hateoas.resource.ImageMapResource;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.hateoas.Link;
import org.springframework.hateoas.mvc.ResourceAssemblerSupport;

public class ImageMapResourceAssembler extends ResourceAssemblerSupport<ImageMap, ImageMapResource> {

    private static final String IMAGE_MAP_PATH = "/api/imageMaps/";
    private static final String UPDATED_BY_PATH = "/updatedBy";
    private static final String POIS_PATH = "/pois";
    private static final String CREATED_BY_PATH = "/createdBy";

    @Value("${baseURL}")
    private String baseUrl;

    public ImageMapResourceAssembler() {
        super(ImageMapController.class, ImageMapResource.class);
    }

    @Override
    public ImageMapResource toResource(ImageMap imageMap) {
        ImageMapResource imageMapResource = null;
        if (imageMap != null) {
            imageMapResource = new ImageMapResource();
            imageMapResource.name = imageMap.getName();
            imageMapResource.url = imageMap.getUrl();
            imageMapResource.description = imageMap.getDescription();
            imageMapResource.active = imageMap.isActive();

            imageMapResource.add(new Link(baseUrl + IMAGE_MAP_PATH + imageMap.getId()));
            imageMapResource.add(new Link(baseUrl + IMAGE_MAP_PATH + imageMap.getId() + UPDATED_BY_PATH, "updatedBy"));
            imageMapResource.add(new Link(baseUrl + IMAGE_MAP_PATH + imageMap.getId() + POIS_PATH, "pois"));
            imageMapResource.add(new Link(baseUrl + IMAGE_MAP_PATH + imageMap.getId() + CREATED_BY_PATH, "createdBy"));
        }
        return imageMapResource;
    }
}