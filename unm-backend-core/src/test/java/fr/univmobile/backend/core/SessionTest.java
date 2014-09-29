package fr.univmobile.backend.core;

import static fr.univmobile.backend.core.impl.ConnectionType.H2;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;

import java.io.File;

import org.junit.Before;
import org.junit.Test;

import fr.univmobile.backend.core.impl.LogQueueDbImpl;
import fr.univmobile.backend.core.impl.SessionManagerImpl;
import fr.univmobile.commons.datasource.impl.BackendDataSourceFileSystem;
import fr.univmobile.commons.tx.Lock;
import fr.univmobile.commons.tx.TransactionManager;

public class SessionTest extends AbstractDbEnabledTest {

	public SessionTest() {

		super(new File("src/test/data/users/001"), //
				new File("src/test/data/regions/001"), //
				new File("src/test/data/pois/001"), //
				new File("src/test/data/comments/001"));
	}

	@Before
	public void setUp() throws Exception {

		users = BackendDataSourceFileSystem.newDataSource(UserDataSource.class,
				dataDir_users);

		sessionManager = new SessionManagerImpl(logQueue, users, H2, cxn);

		LogQueueDbImpl.setAnonymous();
	}

	private UserDataSource users;
	private SessionManager sessionManager;

	private static final String API_KEY = "abcdefgh";

	@Test
	public void testSessionManager_badLogin() throws Exception {

		assertEquals(0, getDbRowCount("unm_history"));

		final AppSession appSession = sessionManager.login_classic(API_KEY,
				"casimir", "xxx");

		assertNull(appSession);

		assertEquals(2, getDbRowCount("unm_history"));
	}

	@Test
	public void testSessionManager_login_OK() throws Exception {

		assertEquals(0, getDbRowCount("unm_history"));

		final AppSession appSession = sessionManager.login_classic(API_KEY,
				"crezvani", "Hello+World!");

		assertNotNull(appSession);

		assertEquals(2, getDbRowCount("unm_history"));

		assertEquals("crezvani", appSession.getUser().getUid());

		assertEquals(40, appSession.getId().length());
	}

	@Test
	public void testSessionManager_users_updateDescription() throws Exception {

		assertEquals(0, getDbRowCount("unm_history"));

		// final String appTokenId =
		sessionManager.login_classic(API_KEY, "crezvani", "Hello+World!");

		assertEquals(2, getDbRowCount("unm_history"));

		final User user = users.getByUid("crezvani");

		final UserBuilder builder = users.update(user);

		builder.setDescription("Du nouveau pour moi, je passe en deuxième année.");

		final TransactionManager tx = TransactionManager.getInstance();

		final Lock lock = tx.acquireLock(5000, "users", "toto");

		lock.save(builder);

		lock.commit();

		users.reload();

		assertEquals(2, getDbRowCount("unm_history")); // No change in logqueue
	}

	@Test
	public void testSessionManager_manager_updateDescription() throws Exception {

		assertEquals(0, getDbRowCount("unm_history"));

		final AppSession appSession = sessionManager.login_classic(API_KEY,
				"crezvani", "Hello+World!");

		assertEquals(2, getDbRowCount("unm_history"));

		final UserBuilder builder = users.update(appSession.getUser());

		builder.setDescription("Du nouveau pour moi, je passe en troisième année.");

		sessionManager.save(appSession, builder);

		/*
		 * final TransactionManager tx = TransactionManager.getInstance();
		 * 
		 * final Lock lock = tx.acquireLock(5000, "users", "toto");
		 * 
		 * lock.save(builder);
		 * 
		 * lock.commit();
		 * 
		 * users.reload();
		 */

		assertEquals(3, getDbRowCount("unm_history")); // Change in logqueue
	}

	@Test
	public void testSessionManager_login_OK_logout() throws Exception {

		assertEquals(0, getDbRowCount("unm_history"));

		final AppSession appSession = sessionManager.login_classic(API_KEY,
				"crezvani", "Hello+World!");

		assertEquals(2, getDbRowCount("unm_history"));

		sessionManager.logout(appSession);

		assertEquals(3, getDbRowCount("unm_history"));
	}

	@Test
	public void testSessionManager_login_OK_logout_twice() throws Exception {

		assertEquals(0, getDbRowCount("unm_history"));

		final AppSession appSession = sessionManager.login_classic(API_KEY,
				"crezvani", "Hello+World!");

		assertEquals(2, getDbRowCount("unm_history"));

		sessionManager.logout(appSession);

		assertEquals(3, getDbRowCount("unm_history"));

		sessionManager.logout(appSession);

		assertEquals(4, getDbRowCount("unm_history"));
	}

	@Test(expected = InvalidSessionException.class)
	public void testSessionManager_login_OK_logout_check() throws Exception {

		final AppSession appSession = sessionManager.login_classic(API_KEY,
				"crezvani", "Hello+World!");

		sessionManager.logout(appSession);

		appSession.check();
	}

	@Test
	public void testSessionManager_logout_manager_updateDescription_crash()
			throws Exception {

		assertEquals(0, getDbRowCount("unm_history"));

		final AppSession appSession = sessionManager.login_classic(API_KEY,
				"crezvani", "Hello+World!");

		assertEquals(2, getDbRowCount("unm_history"));

		sessionManager.logout(appSession);

		assertEquals(3, getDbRowCount("unm_history"));
	}

	@Test
	public void test_prepare() throws Exception {

		assertEquals(0, getDbRowCount("unm_loginconversations"));

		final LoginConversation conversation = sessionManager.prepare(API_KEY);

		assertEquals(1, getDbRowCount("unm_loginconversations"));

		final String loginToken = conversation.getLoginToken();

		final User user = users.getByRemoteUser("crezvani@univ-paris1.fr");

		sessionManager.updateLoginConversation(loginToken, user);

		assertEquals(1, getDbRowCount("unm_loginconversations"));
	}

	@Test
	public void test_prepare_retrieve() throws Exception {

		assertEquals(0, getDbRowCount("unm_loginconversations"));

		final LoginConversation conversation = sessionManager.prepare(API_KEY);

		assertEquals(1, getDbRowCount("unm_loginconversations"));

		final String loginToken = conversation.getLoginToken();

		final User user = users.getByRemoteUser("crezvani@univ-paris1.fr");

		sessionManager.updateLoginConversation(loginToken, user);

		assertEquals(1, getDbRowCount("unm_loginconversations"));
		
		xxx
	}
}
