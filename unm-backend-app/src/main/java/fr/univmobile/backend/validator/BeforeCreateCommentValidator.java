package fr.univmobile.backend.validator;

import fr.univmobile.backend.admin.GeocampusPoiManageJSONController;
import fr.univmobile.backend.domain.Comment;
import fr.univmobile.backend.domain.User;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;

public class BeforeCreateCommentValidator implements Validator {
	
	private static final Log log = LogFactory.getLog(BeforeCreateCommentValidator.class);
	
    @Override
    public boolean supports(Class<?> aClass) {
        return Comment.class.isAssignableFrom(aClass);
    }

    @Override
    public void validate(Object o, Errors errors) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (!(auth instanceof AnonymousAuthenticationToken)){
            User user = (User) auth.getPrincipal();
            Comment comment = (Comment) o;
            if (user.isStudent() && comment.getCreatedBy() != null && comment.getCreatedBy().getId() != user.getId() ){
            	errors.rejectValue("createdBy", null, null, "Cannot comment on behalf of another user");
            }
            if (!user.isSuperAdmin() && !comment.getPoi().getUniversity().getId().equals(user.getUniversity().getId())){
                errors.rejectValue("poi", null, null, "Poi must match the User university");
            }
        } else {
            errors.rejectValue("createdBy", null, null, "User must be logged in");
        }
    }
}
