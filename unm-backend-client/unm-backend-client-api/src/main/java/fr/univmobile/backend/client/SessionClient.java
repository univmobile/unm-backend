package fr.univmobile.backend.client;

import java.io.IOException;

import javax.annotation.Nullable;

public interface SessionClient {

	/**
	 * in a Shibboleth-login process, call this method prior to redirect to a
	 * Shibboleth-secured backend page. You will be given a loginToken + key,
	 * valid 30’, that you can use to retrieve the Shibboleth-validated AppToken
	 * after the whole authentication process takes place.
	 * <p>
	 * After the Shibboleth authentication has succeeded,
	 * call {@link #retrieve(String, String, String)}
	 */
	LoginConversation prepare(String apiToken) throws IOException,
			ClientException;

	/**
	 * A pair of information to authenticate the client to the backend. 
	 */
	interface LoginConversation {

		/**
		 * Use this <code>loginToken</code> when requesting the backend.
		 * <p>
		 * Also, when the backend communicates with the client (e.g. via a
		 * redirect), the <code>loginToken</code> will be passed as an
		 * identifier.
		 */
		String getLoginToken();

		/**
		 * Use this <code>key</code> when requesting the backend.
		 */
		String getKey();
	}
	
	/**
	 * in a Shibboleth-login process, call this method when the
	 * actual Shibboleth authentication succeeded.
	 * <p>
	 * You must call {@link #prepare(String)} prior to this.
	 * 
	 * @return <code>null</code> if the authentication failed or did not
	 * actually take place.
	 */
	@Nullable
	AppToken retrieve(String apiToken, String loginToken, String key) throws IOException, ClientException;

	/**
	 * @return <code>null</code> if the credentials are incorrect.
	 */
	@Nullable
	AppToken login(String apiToken, String login, String password)
			throws IOException, ClientException;
}
