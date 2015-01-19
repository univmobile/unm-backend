package fr.univmobile.backend.hateoas.resource;

import org.springframework.hateoas.ResourceSupport;

import java.util.List;

public class CategoryResource extends ResourceSupport {
    public String name;
    public String description;
    public Boolean active;
    public List<Long> legacyIds;
}
