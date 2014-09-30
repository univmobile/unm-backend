package fr.univmobile.backend.twitter;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertNotNull;

import org.junit.Before;
import org.junit.Test;

import fr.univmobile.testutil.PropertiesUtils;

public class TwitterAccessTest {

	@Before
	public void setUp() throws Exception {

		final String consumerKey = PropertiesUtils
				.getSettingsTestRefProperty("twitter.consumerKey.ref");
		final String consumerSecret = PropertiesUtils
				.getSettingsTestRefProperty("twitter.consumerSecret.ref");

		twitter = new ApplicationOnly(consumerKey, consumerSecret);
	}

	private TwitterAccess twitter;

	@Test
	public void testGetFollowers() throws Exception {

		final int[] ids = twitter.getFollowersIds_byScreenName("dandriana");

		assertNotEquals(0, ids.length);

		assertEquals(20, ids.length);

		assertNotEquals(0, ids[0]);

		for (final int id : ids) {

			final TwitterUser user = twitter.getUsersShow_byUserId(id);

			if (user == null) {
				continue;
			}

			assertEquals(id, user.getId());

			assertNotNull(user.getName());

			assertNotNull(user.getScreenName());

			// System.out.println(user.getName() + " / " +
			// user.getScreenName());
		}
	}
}
