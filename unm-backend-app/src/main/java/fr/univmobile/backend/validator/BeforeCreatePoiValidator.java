package fr.univmobile.backend.validator;

import fr.univmobile.backend.domain.Category;
import fr.univmobile.backend.domain.Poi;
import fr.univmobile.backend.domain.User;

import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;

public class BeforeCreatePoiValidator implements Validator {
    @Override
    public boolean supports(Class<?> aClass) {
        return Poi.class.isAssignableFrom(aClass);
    }

    @Override
    public void validate(Object o, Errors errors) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (!(auth instanceof AnonymousAuthenticationToken)){
            User user = (User) auth.getPrincipal();
            Poi poi = (Poi) o;
            if (!user.isSuperAdmin() && !poi.getUniversity().getId().equals(user.getUniversity().getId())){
                errors.rejectValue("university", null, null, "University must match the User university");
            }
            if (user.isStudent() && !poi.getCategory().getLegacy().startsWith(Category.getBonPlansLegacy())){
                errors.rejectValue("category", null, null, "The only category allowed for students is BON_PLANS");
            }
        } else {
            errors.rejectValue("createdBy", null, null, "User must be logged in");
        }
    }
}
