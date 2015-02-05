package fr.univmobile.backend.hateoas.assembler;

import fr.univmobile.backend.CommentController;
import fr.univmobile.backend.domain.Comment;
import fr.univmobile.backend.hateoas.resource.CommentResource;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.hateoas.Link;
import org.springframework.hateoas.mvc.ResourceAssemblerSupport;

public class CommentResourceAssembler extends ResourceAssemblerSupport<Comment, CommentResource> {

    private static final String USERS_PATH = "api/comments/";
    private static final String CREATED_BY_PATH = "/createdBy";
    private static final String UPDATED_BY_BY_PATH = "/updatedBy";
    private static final String POI_PATH = "/poi";

    @Value("${baseURL}")
    private String baseUrl;

    public CommentResourceAssembler() {
        super(CommentController.class, CommentResource.class);
    }

    @Override
    public CommentResource toResource(Comment comment) {
        CommentResource commentResource = null;
        if (comment != null) {
            commentResource = new CommentResource();
            commentResource.title = comment.getTitle();
            commentResource.message = comment.getMessage();
            commentResource.active = comment.isActive();

            commentResource.add(new Link(baseUrl + USERS_PATH + comment.getId()));
            commentResource.add(new Link(baseUrl + USERS_PATH + comment.getId() + CREATED_BY_PATH, "createdBy"));
            commentResource.add(new Link(baseUrl + USERS_PATH + comment.getId() + UPDATED_BY_BY_PATH, "updatedBy"));
            commentResource.add(new Link(baseUrl + USERS_PATH + comment.getId() + POI_PATH, "poi"));
        }
        return commentResource;
    }
}