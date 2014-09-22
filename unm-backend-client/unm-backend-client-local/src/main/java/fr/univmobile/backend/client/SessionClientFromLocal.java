package fr.univmobile.backend.client;

import static com.google.common.base.Preconditions.checkNotNull;

import java.io.IOException;
import java.sql.SQLException;

import javax.annotation.Nullable;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.avcompris.lang.NotImplementedException;

import fr.univmobile.backend.core.AppSession;
import fr.univmobile.backend.core.InvalidSessionException;
import fr.univmobile.backend.core.SessionManager;
import fr.univmobile.commons.DataBeans;

public class SessionClientFromLocal extends AbstractClientFromLocal implements
		SessionClient {

	public SessionClientFromLocal(final String baseURL,
			final SessionManager sessionManager) {

		super(baseURL);

		this.sessionManager = checkNotNull(sessionManager, "sessionManager");
	}

	private final SessionManager sessionManager;

	@Override
	public LoginConversation prepare(final String apiKey) throws IOException,
			ClientException {

		throw new NotImplementedException();
	}

	@Override
	public AppToken retrieve(final String apiKey, final String loginToken,
			final String key) throws IOException, ClientException {

		throw new NotImplementedException();
	}

	private static final Log log = LogFactory
			.getLog(SessionClientFromLocal.class);

	@Override
	@Nullable
	public AppToken login(final String apiKey, final String login,
			final String password) throws IOException, ClientException {

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

		final fr.univmobile.backend.core.User dsUser = session.getUser();

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

	private static User getUser(final fr.univmobile.backend.core.User dsUser) {

		final MutableUser user = DataBeans.instantiate(MutableUser.class) //
				.setUid(dsUser.getUid());

		if (!dsUser.isNullMail()) {
			user.setMail(dsUser.getMail());
		}

		return user;
	}
}

interface MutableAppToken extends AppToken {

	MutableAppToken setUser(User user);

	MutableAppToken setId(String id);
}

interface MutableUser extends User {

	MutableUser setUid(String uid);

	MutableUser setMail(String mail);
}
