package fr.univmobile.backend.hateoas.assembler;

import fr.univmobile.backend.CommentController;
import fr.univmobile.backend.domain.User;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.hateoas.Link;
import org.springframework.hateoas.mvc.ResourceAssemblerSupport;
import fr.univmobile.backend.hateoas.resource.UserResource;

public class UserResourceAssembler extends ResourceAssemblerSupport<User, UserResource> {

    private static final String USERS_PATH = "api/users/";
    private static final String UNIVERITY_PATH = "/university";
    private static final String UPDATED_BY_PATH = "/updatedBy";
    private static final String SECONDARY_UNIVERITY_PATH = "/secondaryUniversity";
    private static final String CREATED_BY_PATH = "/createdBy";

    @Value("${baseURL}")
    private String baseUrl;

    public UserResourceAssembler() {
        super(CommentController.class, UserResource.class);
    }

    @Override
    public UserResource toResource(User user) {
        UserResource userResource = new UserResource();

        userResource.username = user.getUsername();
        userResource.displayName = user.getDisplayName();
        userResource.title = user.getTitle();
        userResource.email = user.getEmail();
        userResource.profileImageUrl = user.getProfileImageUrl();
        userResource.description = user.getDescription();
        userResource.admin = user.isAdmin();
        userResource.student = user.isStudent();
        userResource.superAdmin = user.isSuperAdmin();

        userResource.add(new Link(baseUrl + USERS_PATH + user.getId()));
        userResource.add(new Link(baseUrl + USERS_PATH + user.getId() + UNIVERITY_PATH, "university"));
        userResource.add(new Link(baseUrl + USERS_PATH + user.getId() + UPDATED_BY_PATH, "updatedBy"));
        userResource.add(new Link(baseUrl + USERS_PATH + user.getId() + SECONDARY_UNIVERITY_PATH, "secondaryUniversity"));
        userResource.add(new Link(baseUrl + USERS_PATH + user.getId() + CREATED_BY_PATH, "createdBy"));

        return userResource;
    }
}
