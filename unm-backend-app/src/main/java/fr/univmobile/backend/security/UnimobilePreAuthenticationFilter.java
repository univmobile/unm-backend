package fr.univmobile.backend.security;

import fr.univmobile.backend.domain.User;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.security.web.authentication.preauth.AbstractPreAuthenticatedProcessingFilter;

import javax.servlet.http.HttpServletRequest;

public class UnimobilePreAuthenticationFilter extends AbstractPreAuthenticatedProcessingFilter {

	private static final Log log = LogFactory
			.getLog(UnimobilePreAuthenticationFilter.class);

    @Override
    protected Object getPreAuthenticatedPrincipal(HttpServletRequest httpServletRequest) {
        String username = null;
        User user = (User) httpServletRequest.getSession().getAttribute("delegationUser");
        if (user != null){
        	httpServletRequest.getSession().setAttribute("remoteUserLoadedBySpringSecurity", user.getRemoteUser());
            username = user.getUsername();
        } else {
        	httpServletRequest.getSession().removeAttribute("remoteUserLoadedBySpringSecurity");
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
