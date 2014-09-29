package fr.univmobile.backend.core;

import java.io.IOException;
import java.sql.SQLException;

import javax.annotation.Nullable;

import fr.univmobile.commons.tx.TransactionException;

public interface SessionManager {

	/**
	 * @return the appToken that will hold the applicative session.
	 */
	@Nullable
	AppSession login_classic(String apiKey, String login, String password)
			throws IOException, SQLException;

	void logout(AppSession appSession) throws IOException, SQLException;

	// User getCurrentUser(AppSession appSession) throws IOException,
	// SQLException;

	void save(AppSession session, UserBuilder user)
			throws TransactionException, IOException, SQLException,
			InvalidSessionException;

	AppSession getAppSession(String sessionId) throws IOException,
			SQLException, InvalidSessionException;

	LoginConversation prepare(String apiKey) throws IOException, SQLException;

	void updateLoginConversation(final String loginToken, final User user)
			throws IOException, SQLException;
	
	@Nullable
	AppSession retrieve(String apiKey, String loginToken, String key)
			throws IOException, SQLException;
}
