package fr.univmobile.backend.client;

import static org.apache.commons.lang3.StringUtils.isBlank;

import java.io.IOException;

import javax.annotation.Nullable;
import javax.inject.Inject;

import net.avcompris.binding.annotation.XPath;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.avcompris.lang.NotImplementedException;

import fr.univmobile.backend.client.json.SessionJSONClient;

public class SessionClientFromJSON extends
		AbstractClientFromJSON<SessionJSONClient> implements SessionClient {

	@Inject
	public SessionClientFromJSON(final SessionJSONClient jsonClient) {

		super(jsonClient);
	}

	private static Log log = LogFactory.getLog(SessionClientFromJSON.class);

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

	@Override
	@Nullable
	public AppToken login(final String apiKey, final String login,
			final String password) throws IOException, ClientException {

		if (log.isDebugEnabled()) {
			log.debug("login():" + login + "...");
		}

		final String json = jsonClient.loginJSON(apiKey, login, password);

		System.out.println("json: "+json);
		if (isBlank(json)) {
			return null;
		}

		return unmarshall(json, AppTokenJSON.class);
	}

	@Override
	public void logout(final String apiKey, final String appTokenId)
			throws IOException, ClientException {

		if (log.isDebugEnabled()) {
			log.debug("logout():" + appTokenId + "...");
		}

		jsonClient.logoutJSON(apiKey, appTokenId);
	}

	@XPath("/*")
	public interface AppTokenJSON extends AppToken {

		@XPath("@id")
		@Override
		String getId();

		@XPath("user")
		@Override
		UserJSON getUser();

		@XPath("@id")
		@Override
		String toString();
		
		interface UserJSON extends User {

			@XPath("@uid")
			@Override
			String getUid();

			@XPath("@mail")
			@Override
			String getMail();
			
			@XPath("@displayName")
			@Override
			String getDisplayName();
		}
	}
}
