package fr.univmobile.backend.hateoas.resource;

import org.springframework.hateoas.ResourceSupport;

public class UserResource extends ResourceSupport {
    public String username;
    public String displayName;
    public String title;
    public String email;
    public String profileImageUrl;
    public String description;
    public Boolean admin;
    public Boolean student;
    public Boolean superAdmin;
}
