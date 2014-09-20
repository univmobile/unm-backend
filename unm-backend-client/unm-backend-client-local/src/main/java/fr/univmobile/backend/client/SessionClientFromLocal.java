package fr.univmobile.backend.client;

import static com.google.common.base.Preconditions.checkNotNull;

import java.io.IOException;

import com.avcompris.lang.NotImplementedException;

import fr.univmobile.backend.core.SessionManager;

public class SessionClientFromLocal extends AbstractClientFromLocal implements
		SessionClient {

	public SessionClientFromLocal(final String baseURL,
			final SessionManager sessionManager) {

		super(baseURL);

		this.sessionManager = checkNotNull(sessionManager, "sessionManager");
	}

	private final SessionManager sessionManager;

	@Override
	public LoginConversation prepare(final String apiToken) throws IOException,
			ClientException {

		throw new NotImplementedException();
	}

	@Override
	public AppToken retrieve(final String apiToken, final String loginToken,
			final String key) throws IOException, ClientException {

		throw new NotImplementedException();
	}

	@Override
	public AppToken login(final String apiToken, final String login,
			final String password) throws IOException, ClientException {

		throw new NotImplementedException();
	}
}
