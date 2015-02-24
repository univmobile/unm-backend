package fr.univmobile.backend.validator;

import fr.univmobile.backend.domain.Bookmark;
import fr.univmobile.backend.domain.User;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;

public class BeforeCreateBookmarkValidator implements Validator {

    @Override
    public boolean supports(Class<?> aClass) {
        return Bookmark.class.isAssignableFrom(aClass);
    }

    @Override
    public void validate(Object o, Errors errors) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (!(auth instanceof AnonymousAuthenticationToken)){
            User user = (User) auth.getPrincipal();
            Bookmark bookmark = (Bookmark) o;
            if (user.isStudent() && !user.getId().equals(bookmark.getUser().getId()) ){
                errors.rejectValue("user", null, null, "Cannot save a bookmark on behalf of another user");
            }
        } else {
            errors.rejectValue("createdBy", null, null, "User must be logged in");
        }
    }
}