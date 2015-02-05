package fr.univmobile.backend.security;

import fr.univmobile.backend.SessionAuditorAware;
import fr.univmobile.backend.core.impl.LogQueueDbImpl;
import fr.univmobile.backend.domain.Token;
import fr.univmobile.backend.domain.TokenRepository;
import fr.univmobile.backend.domain.User;

import org.springframework.security.web.authentication.preauth.AbstractPreAuthenticatedProcessingFilter;

import javax.servlet.http.HttpServletRequest;

public class UnimobileTokenPreAuthenticationFilter extends AbstractPreAuthenticatedProcessingFilter {

    TokenRepository tokenRepository;
    SessionAuditorAware sessionAuditorAware;

    public void setTokenRepository(TokenRepository tokenRepository){
        this.tokenRepository = tokenRepository;
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
        Token token = tokenRepository.findByToken(t);
        if (token != null){
            User user = token.getUser();
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
        Token token = tokenRepository.findByToken(t);
        if (t == null || t.isEmpty()) {
        	return null;
        }
        if (token != null){
            User user = token.getUser();
            if (user != null){
                password = user.getPassword();
            }
        }
        return password;
    }
}