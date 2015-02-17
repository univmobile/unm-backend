package fr.univmobile.backend.batch;

import fr.univmobile.backend.domain.User;
import org.springframework.data.domain.AuditorAware;
import org.springframework.stereotype.Component;

@Component
public class SessionAuditorAware implements AuditorAware<User> {
	
	private User sessionUser;
	
	public User getSessionUser() {
		return sessionUser;
	}

	public void setSessionUser(User user) {
		this.sessionUser = user;
	}

	@Override
	public User getCurrentAuditor() {
		return this.sessionUser;
	}

}
