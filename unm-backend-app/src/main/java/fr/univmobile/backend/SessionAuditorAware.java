package fr.univmobile.backend;

import org.springframework.data.domain.AuditorAware;

import fr.univmobile.backend.domain.User;

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
