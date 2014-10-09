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
import fr.univmobile.backend.client.SSOConfiguration;
import fr.univmobile.backend.client.SessionClient;
import fr.univmobile.backend.client.SessionClient.LoginConversation;
import fr.univmobile.backend.client.User;
import fr.univmobile.backend.json.JSONList;
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

		final JSONList twitterFollowers = new JSONList();

		jsonUser.put("twitterFollowers", twitterFollowers);

		for (final User.TwitterFollower twitterFollower : user
				.getTwitterFollowers()) {

			twitterFollowers.add(new JSONMap() //
					.put("screenName", twitterFollower.getScreenName()) //
					.put("name", twitterFollower.getName()));
		}

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

	@Override
	public String prepareJSON(final String apiKey) throws IOException {

		if (log.isDebugEnabled()) {
			log.debug("prepareJSON():" + apiKey + "...");
		}

		final LoginConversation conversation;

		try {

			conversation = sessionClient.prepare(apiKey);

		} catch (final ClientException e) {

			log.fatal(e);

			throw new RuntimeException(e);
		}

		return new JSONMap() //
				.put("loginToken", conversation.getLoginToken()) //
				.put("key", conversation.getKey()) //
				.toJSONString();
	}

	@Override
	public String retrieveJSON(final String apiKey, final String loginToken,
			final String key) throws IOException {

		if (log.isDebugEnabled()) {
			log.debug("retrieveJSON():" + loginToken + "...");
		}

		final AppToken token;

		try {

			token = sessionClient.retrieve(apiKey, loginToken, key);

		} catch (final ClientException e) {

			log.fatal(e);

			throw new RuntimeException(e);
		}

		return tokenJSON(token);
	}

	@Override
	public String getSSOConfigurationJSON() throws IOException {

		if (log.isDebugEnabled()) {
			log.debug("getSSOConfigurationJSON()...");
		}

		final SSOConfiguration ssoConfiguration;

		try {

			ssoConfiguration = sessionClient.getSSOConfiguration();

		} catch (final ClientException e) {

			log.fatal(e);

			throw new RuntimeException(e);
		}

		final JSONMap sso = new JSONMap();

		sso.put("url", ssoConfiguration.getURL());

		sso.put("target",
				new JSONMap().put("url", ssoConfiguration.getTargetURL()));

		sso.put("callback",
				new JSONMap().put("url", ssoConfiguration.getCallbackURL()));

		return new JSONMap().put("sso", sso).toJSONString();
	}
}
