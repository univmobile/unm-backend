package fr.univmobile.backend.core.impl;

import static com.google.common.base.Preconditions.checkNotNull;
import fr.univmobile.backend.core.AppSession;
import fr.univmobile.backend.core.User;

final class AppSessionImpl implements AppSession {

	public AppSessionImpl(final String id, final User user) {

		this.id = checkNotNull(id, "id");
		this.user = checkNotNull(user, "user");
	}

	private final String id;
	private  final User user;
	
	@Override
	public String getId() {
		
		return id;
	}
	
	@Override 
	public User getUser() {
		
		return user;
	}
}
