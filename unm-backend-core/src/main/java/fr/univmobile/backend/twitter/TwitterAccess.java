package fr.univmobile.backend.twitter;

import java.io.IOException;

import javax.annotation.Nullable;

public interface TwitterAccess {

	String FOLLOWERS_IDS_URL = "https://api.twitter.com/1.1/followers/ids.json";

	String USERS_SHOW_URL = "https://api.twitter.com/1.1/users/show.json";

	int[] getFollowersIds_byScreenName(String screenName) throws IOException;

	@Nullable
	TwitterUser getUsersShow_byUserId(int userId) throws IOException;
}
