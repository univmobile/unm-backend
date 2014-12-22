package fr.univmobile.backend.domain.test;

import org.springframework.data.domain.AuditorAware;

import fr.univmobile.backend.domain.User;

public class TestAuditorAware implements AuditorAware<User> {
	
	private User testingUser;
	
	public User getTestingUser() {
		return testingUser;
	}

	public void setTestingUser(User testingUser) {
		this.testingUser = testingUser;
	}

	@Override
	public User getCurrentAuditor() {
		return this.testingUser;
	}

}
