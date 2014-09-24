package fr.univmobile.backend.client.json;

import static com.google.common.base.Preconditions.checkNotNull;

import java.io.IOException;

import javax.annotation.Nullable;
import javax.inject.Inject;
import javax.inject.Named;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import fr.univmobile.backend.client.AppToken;
import fr.univmobile.backend.client.ClientException;
import fr.univmobile.backend.client.SessionClient;
import fr.univmobile.backend.client.User;
import fr.univmobile.backend.json.JSONMap;

public class SessionJSONClientImpl implements SessionJSONClient {

	@Inject
	public SessionJSONClientImpl(@Named("SessionJSONClientImpl")//
			final SessionClient sessionClient) {

		this.sessionClient = checkNotNull(sessionClient, "sessionClient");
	}

	private final SessionClient sessionClient;

	private static final Log log = LogFactory
			.getLog(SessionJSONClientImpl.class);

	@Override
	public String loginJSON(final String apiKey, final String login,
			final String password) throws IOException {

		if (log.isDebugEnabled()) {
			log.debug("loginJSON():" + login + "...");
		}

		final AppToken token;

		try {

			token = sessionClient.login(apiKey, login, password);

		} catch (final ClientException e) {

			log.fatal(e);

			throw new RuntimeException(e);
		}

		return tokenJSON(token);
	}

	private static String tokenJSON(@Nullable final AppToken token) {

		if (token == null) {
			return ""; // empty String
		}

		final JSONMap json = new JSONMap() //
				.put("id", token.getId());

		final User user = token.getUser();

		final JSONMap jsonUser = new JSONMap() //
				.put("uid", user.getUid()) //
				.put("mail", user.getMail()) //
				.put("displayName", user.getDisplayName());

		json.put("user", jsonUser);

		return json.toJSONString();
	}

	@Override
	public String getAppTokenJSON(final String apiKey, final String appTokenId)
			throws IOException {

		if (log.isDebugEnabled()) {
			log.debug("getAppTokenJSON():" + appTokenId + "...");
		}

		final AppToken token;

		try {

			token = sessionClient.getAppToken(apiKey, appTokenId);

		} catch (final ClientException e) {

			log.fatal(e);

			throw new RuntimeException(e);
		}

		return tokenJSON(token);
	}

	@Override
	public String logoutJSON(final String apiKey, final String appTokenId)
			throws IOException {

		if (log.isDebugEnabled()) {
			log.debug("logoutJSON():" + appTokenId + "...");
		}

		try {

			sessionClient.logout(apiKey, appTokenId);

		} catch (final ClientException e) {

			log.fatal(e);

			throw new RuntimeException(e);
		}

		return ""; // empty String
	}
}
