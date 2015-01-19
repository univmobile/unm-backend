package fr.univmobile.backend.hateoas.resource;

import org.springframework.hateoas.ResourceSupport;

public class ImageMapDataResource extends ResourceSupport {
    public ImageMapResource imageMap;
    public PoiResource selectedPoi;
}
