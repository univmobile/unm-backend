package fr.univmobile.backend.core;

import java.io.IOException;
import java.sql.SQLException;

import javax.annotation.Nullable;

public interface SessionManager {

	/**
	 * @return the appToken that will hold the applicative session.
	 */
	@Nullable
	AppSession login_classic(String apiKey, String login, String password)
			throws IOException, SQLException;

	void logout(AppSession appSession) throws IOException, SQLException;

	//User getCurrentUser(AppSession appSession) throws IOException, SQLException;

	void save(UserBuilder user) throws IOException, SQLException;
}
