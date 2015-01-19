package fr.univmobile.backend.hateoas.resource;

import org.springframework.hateoas.ResourceSupport;

public class ImageMapResource extends ResourceSupport {
    public String name;
    public String url;
    public String description;
    public Boolean active;
}