package fr.univmobile.backend.security;

import fr.univmobile.backend.SessionAuditorAware;
import fr.univmobile.backend.core.impl.LogQueueDbImpl;
import fr.univmobile.backend.domain.AuthenticatedSession;
import fr.univmobile.backend.domain.AuthenticatedSessionRepository;
import fr.univmobile.backend.domain.Token;
import fr.univmobile.backend.domain.TokenRepository;
import fr.univmobile.backend.domain.User;

import org.springframework.security.web.authentication.preauth.AbstractPreAuthenticatedProcessingFilter;

import javax.servlet.http.HttpServletRequest;

public class UnimobileTokenPreAuthenticationFilter extends AbstractPreAuthenticatedProcessingFilter {

    AuthenticatedSessionRepository sessionsRepository;
    SessionAuditorAware sessionAuditorAware;

    public void setTokenRepository(AuthenticatedSessionRepository sessionsRepository){
        this.sessionsRepository = sessionsRepository;
    }

    public void setSessionAuditorAware(SessionAuditorAware sessionAuditorAware){
        this.sessionAuditorAware = sessionAuditorAware;
    }

    @Override
    protected Object getPreAuthenticatedPrincipal(HttpServletRequest httpServletRequest) {
        String t = httpServletRequest.getHeader("Authentication-Token");
        String username = null;
        if (t == null || t.isEmpty()) {
        	return null;
        }
        AuthenticatedSession session = sessionsRepository.findByToken(t);
        if (session != null){
            User user = session.getUser();
            if (user != null){
                username = user.getUsername();
                sessionAuditorAware.setSessionUser(user);
                LogQueueDbImpl.setPrincipal(user.getUsername());
                httpServletRequest.getSession().setAttribute("user", user);
                httpServletRequest.getSession().setAttribute("remoteUserLoadedBySpringSecurity", user.getRemoteUser());
            } else {
            	httpServletRequest.getSession().removeAttribute("remoteUserLoadedBySpringSecurity");
            }
        } else {
        	//httpServletRequest.getSession().removeAttribute("remoteUserLoadedBySpringSecurity");
        }
        return username;
    }

    @Override
    protected Object getPreAuthenticatedCredentials(HttpServletRequest httpServletRequest) {
        String t = httpServletRequest.getHeader("Authentication-Token");
        String password = null;
        AuthenticatedSession session = sessionsRepository.findByToken(t);
        if (t == null || t.isEmpty()) {
        	return null;
        }
        if (session != null){
            User user = session.getUser();
            if (user != null){
                password = user.getPassword();
            }
        }
        return password;
    }
}