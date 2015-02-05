package fr.univmobile.backend.hateoas.resource;

import org.springframework.hateoas.ResourceSupport;

public class CommentResource extends ResourceSupport {
    public String title;
    public String message;
    public Boolean active;
}