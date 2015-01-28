package fr.univmobile.backend.security;

import fr.univmobile.backend.domain.User;
import org.springframework.security.web.authentication.preauth.AbstractPreAuthenticatedProcessingFilter;

import javax.servlet.http.HttpServletRequest;

public class UnimobilePreAuthenticationFilter extends AbstractPreAuthenticatedProcessingFilter {

    @Override
    protected Object getPreAuthenticatedPrincipal(HttpServletRequest httpServletRequest) {
        String username = null;
        User user = (User) httpServletRequest.getSession().getAttribute("delegationUser");
        if (user != null){
            username = user.getUsername();
        }
        return username;
    }

    @Override
    protected Object getPreAuthenticatedCredentials(HttpServletRequest httpServletRequest) {
        String password = null;
        User user = (User) httpServletRequest.getSession().getAttribute("delegationUser");
        if (user != null){
            password = user.getPassword();
        }
        return password;
    }
}
