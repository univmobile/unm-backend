package fr.univmobile.backend.validator;

import fr.univmobile.backend.domain.UniversityLibrary;
import fr.univmobile.backend.domain.User;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;

public class BeforeCreateUniversityLibraryValidator implements Validator {
    @Override
    public boolean supports(Class<?> aClass) {
        return UniversityLibrary.class.isAssignableFrom(aClass);
    }

    @Override
    public void validate(Object o, Errors errors) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (!(auth instanceof AnonymousAuthenticationToken)){
            User user = (User) auth.getPrincipal();
            UniversityLibrary universityLibrary = (UniversityLibrary) o;
            if (!user.isSuperAdmin() && !user.isAdmin()){
                errors.rejectValue("university", null, null, "Permission denied");
            }
            if (user.isAdmin() && !universityLibrary.getUniversity().getId().equals(user.getUniversity().getId())){
                errors.rejectValue("university", null, null, "University must match the User university");
            }
        } else {
            errors.rejectValue("createdBy", null, null, "User must be logged in");
        }
    }
}
