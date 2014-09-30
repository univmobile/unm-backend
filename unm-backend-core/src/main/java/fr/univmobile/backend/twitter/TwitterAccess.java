package fr.univmobile.backend.twitter;

import static fr.univmobile.commons.http.HttpUtils.wget;

import java.io.FileNotFoundException;
import java.io.IOException;

import javax.annotation.Nullable;

import net.avcompris.binding.annotation.XPath;
import net.avcompris.binding.json.JsonBinder;
import net.avcompris.binding.json.impl.DomJsonBinder;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.json.simple.JSONValue;

import fr.univmobile.commons.http.Authorization;

public abstract class TwitterAccess {

	public static final String FOLLOWERS_IDS_URL = "https://api.twitter.com/1.1/followers/ids.json";

	public static final String USERS_SHOW_URL = "https://api.twitter.com/1.1/users/show.json";

	protected TwitterAccess() {

	}

	protected static final JsonBinder binder = new DomJsonBinder();

	protected abstract Authorization getAuthorization() throws IOException;

	public final int[] getFollowersIds_byScreenName(final String screenName)
			throws IOException {

		final String json = wget(getAuthorization(), FOLLOWERS_IDS_URL,
				"screen_name", screenName, "count", 20);

		final Object jsonObject = JSONValue.parse(json);

		final FollowersIds followersIds = binder.bind(jsonObject,
				FollowersIds.class);

		return followersIds.getIds();
	}

	private static final Log log = LogFactory.getLog(TwitterAccess.class);

	@Nullable
	public final TwitterUser getUsersShow_byUserId(final int userId)
			throws IOException {

		final String json;

		try {

			json = wget(getAuthorization(), USERS_SHOW_URL, "user_id", userId);

		} catch (final FileNotFoundException e) {

			log.error("getUsersShow_byUserId(" + userId + ") can't get remote resource", e);

			return null;
		}

		final Object jsonObject = JSONValue.parse(json);

		return binder.bind(jsonObject, UsersShow.class);
	}

	@XPath("/json")
	private interface FollowersIds {

		@XPath("ids")
		int[] getIds();
	}

	@XPath("/json")
	private interface UsersShow extends TwitterUser {

		@Override
		@XPath("@id")
		int getId();

		@Override
		@XPath("@name")
		String getName();

		@Override
		@XPath("@screen_name")
		String getScreenName();
	}
}
