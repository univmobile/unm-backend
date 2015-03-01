package fr.univmobile.backend.client;

import static com.google.common.base.Preconditions.checkNotNull;

import java.io.IOException;
import java.sql.SQLException;

import javax.annotation.Nullable;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import fr.univmobile.backend.client.SessionClient.LoginConversation;
import fr.univmobile.backend.client.User.TwitterFollower;
import fr.univmobile.backend.core.AppSession;
import fr.univmobile.backend.core.InvalidSessionException;
import fr.univmobile.backend.core.SessionManager;
import fr.univmobile.backend.twitter.TwitterAccess;
import fr.univmobile.backend.twitter.TwitterUser;
import fr.univmobile.commons.DataBeans;

public class SessionClientFromLocal extends AbstractClientFromLocal implements
		SessionClient {

	public SessionClientFromLocal(
			final String baseURL, //
			final String ssoBaseURL, final String shibbolethTargetBaseURL,
			final String shibbolethCallbackURL, //
			final SessionManager sessionManager, final TwitterAccess twitter) {

		super(baseURL);

		this.ssoBaseURL = checkNotNull(ssoBaseURL, "ssoBaseURL");
		this.shibbolethTargetBaseURL = checkNotNull(shibbolethTargetBaseURL,
				"shibbolethTargetBaseURL");
		this.shibbolethCallbackURL = checkNotNull(shibbolethCallbackURL,
				"shibbolethCallbackURL");
		this.sessionManager = checkNotNull(sessionManager, "sessionManager");
		this.twitter = checkNotNull(twitter, "twitter");
	}

	private final String ssoBaseURL;
	private final String shibbolethTargetBaseURL;
	private final String shibbolethCallbackURL;
	private final SessionManager sessionManager;
	private final TwitterAccess twitter;

	@Override
	public LoginConversation prepare(final String apiKey) throws IOException,
			ClientException {

		final fr.univmobile.backend.core.LoginConversation dsConversation;

		try {

			dsConversation = sessionManager.prepare(apiKey);

		} catch (final SQLException e) {

			log.fatal(e);

			throw new ClientException(e);
		}

		return DataBeans.instantiate(MutableLoginConversation.class) //
				.setLoginToken(dsConversation.getLoginToken()) //
				.setKey(dsConversation.getKey());
	}

	@Override
	public AppToken retrieve(final String apiKey, final String loginToken,
			final String key) throws IOException, ClientException {

		final AppSession session;

		try {

			session = sessionManager.retrieve(apiKey, loginToken, key);

		} catch (final SQLException e) {

			log.fatal(e);

			throw new ClientException(e);
		}

		if (session == null) {

			return null;
		}

		final fr.univmobile.backend.domain.User dsUser = session.getUser();

		final String sessionId = session.getId();

		return DataBeans.instantiate(MutableAppToken.class) //
				.setId(sessionId) //
				.setUser(getUser(dsUser));
	}

	private static final Log log = LogFactory
			.getLog(SessionClientFromLocal.class);

	@Override
	@Nullable
	public AppToken login(final String apiKey, final String login,
			final String password) throws IOException, ClientException {

		if (log.isInfoEnabled()) {
			log.info("login():" + login + "...");
		}

		final AppSession session;

		try {

			session = sessionManager.login_classic(apiKey, login, password);

		} catch (final SQLException e) {

			log.fatal(e);

			throw new ClientException(e);
		}

		if (session == null) {

			return null;
		}

		final fr.univmobile.backend.domain.User dsUser = session.getUser();

		final String sessionId = session.getId();

		return DataBeans.instantiate(MutableAppToken.class) //
				.setId(sessionId) //
				.setUser(getUser(dsUser));
	}

	@Override
	@Nullable
	public AppToken getAppToken(final String apiKey, final String appTokenId)
			throws IOException, ClientException {

		final AppSession session;

		try {

			session = sessionManager.getAppSession(appTokenId);

		} catch (final InvalidSessionException e) {

			log.error(e);

			return null;

		} catch (final SQLException e) {

			log.fatal(e);

			throw new ClientException(e);
		}

		if (session == null) {

			return null;
		}

		final fr.univmobile.backend.domain.User dsUser = session.getUser();

		final String sessionId = session.getId();

		return DataBeans.instantiate(MutableAppToken.class) //
				.setId(sessionId) //
				.setUser(getUser(dsUser));
	}

	@Override
	public void logout(final String apiKey, final String appTokenId)
			throws IOException, ClientException {

		final AppSession appSession;

		try {

			appSession = sessionManager.getAppSession(appTokenId);

		} catch (final SQLException e) {

			log.fatal(e);

			throw new ClientException(e);

		} catch (final InvalidSessionException e) {

			if (log.isInfoEnabled()) {
				log.info("Invalid session: " + appTokenId + " -- Logging out.");
			}

			return; // do nothing
		}

		try {

			sessionManager.logout(appSession);

		} catch (final SQLException e) {

			log.fatal(e);

			throw new ClientException(e);
		}
	}

	private User getUser(final fr.univmobile.backend.domain.User dsUser)
			throws IOException {

		final MutableUser user = DataBeans.instantiate(MutableUser.class) //
				.setUid(String.valueOf(dsUser.getId())) //
				.setDisplayName(dsUser.getDisplayName());

		if (dsUser.getEmail() != null) {
			user.setMail(dsUser.getEmail());
		}

		if (log.isDebugEnabled()) {
			log.debug("getUser(" + dsUser.getUsername()
					);
		}

		/*if (!dsUser.isNullTwitterScreenName()) {

			final String twitterScreenName = dsUser.getTwitterScreenName();

			final int[] followerIds = twitter
					.getFollowersIds_byScreenName(twitterScreenName);

			for (final int followerId : followerIds) {

				final TwitterUser twitterUser = twitter
						.getUsersShow_byUserId(followerId);

				if (twitterUser == null) {
					continue;
				}

				final MutableTwitterFollower twitterFollower = DataBeans
						.instantiate(MutableTwitterFollower.class) //
						.setId(twitterUser.getId()) //
						.setScreenName(twitterUser.getScreenName()) //
						.setName(twitterUser.getName());

				user.addToTwitterFollowers(twitterFollower);
			}
		}*/

		return user;
	}

	@Override
	public SSOConfiguration getSSOConfiguration() throws IOException,
			ClientException {

		return DataBeans
				.instantiate(MutableSSOConfiguration.class)

				.setURL(ssoBaseURL
						+ "?target=${target.url}&entityID=${shibboleth.entityProvider}") //

				.setTargetURL(
						shibbolethTargetBaseURL
								+ "?loginToken=${loginToken}&callback=${callback.url}") //

				.setCallbackURL(shibbolethCallbackURL);
	}
}

interface MutableAppToken extends AppToken {

	MutableAppToken setUser(User user);

	MutableAppToken setId(String id);
}

interface MutableUser extends User {

	MutableUser setUid(String uid);

	MutableUser setMail(@Nullable String mail);

	MutableUser setDisplayName(String displayName);

	MutableUser addToTwitterFollowers(TwitterFollower twitterFollower);
}

interface MutableTwitterFollower extends TwitterFollower {

	MutableTwitterFollower setId(int id);

	MutableTwitterFollower setScreenName(String screenName);

	MutableTwitterFollower setName(String name);
}

interface MutableLoginConversation extends LoginConversation {

	MutableLoginConversation setLoginToken(String loginToken);

	MutableLoginConversation setKey(String key);
}

interface MutableSSOConfiguration extends SSOConfiguration {

	MutableSSOConfiguration setURL(String url);

	MutableSSOConfiguration setTargetURL(String targetURL);

	MutableSSOConfiguration setCallbackURL(String callbackURL);
}
