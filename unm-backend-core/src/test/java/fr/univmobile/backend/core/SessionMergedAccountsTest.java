package fr.univmobile.backend.core;

import static fr.univmobile.backend.core.impl.ConnectionType.H2;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.io.File;

import org.junit.Before;
import org.junit.Test;

import fr.univmobile.backend.core.impl.LogQueueDbImpl;
import fr.univmobile.backend.core.impl.SessionManagerImpl;
import fr.univmobile.backend.domain.UserRepository;
import fr.univmobile.commons.datasource.impl.BackendDataSourceFileSystem;

public class SessionMergedAccountsTest extends AbstractDbEnabledTest {

	public SessionMergedAccountsTest() {

		super(new File("src/test/data/users/002"), //
				new File("src/test/data/regions/001"), //
				new File("src/test/data/pois/001"), //
				new File("src/test/data/comments/001"));
	}

	@Before
	public void setUp() throws Exception {

		/*users = BackendDataSourceFileSystem.newDataSource(UserDataSource.class,
				dataDir_users);*/
		
		sessionManager = new SessionManagerImpl(logQueue, users, H2, cxn);

		LogQueueDbImpl.setAnonymous();
	}

	private UserRepository users;
	private SessionManager sessionManager;

	private static final String API_KEY = "abcdefgh";

	@Test
	public void testSessionManager_merged_uid1() throws Exception {

		assertEquals(0, getDbRowCount("unm_history"));

		final AppSession appSession = sessionManager.login_classic(API_KEY,
				"dandriana", "dummy");

		assertNotNull(appSession);

		final fr.univmobile.backend.domain.User user = appSession.getUser();

		assertEquals("dandriana", user.getUsername());

		//assertTrue(user.isNullPrimaryUser());

		//assertEquals(1, user.sizeOfSecondaryUsers());
		/*assertEquals("dandrianavalontsalama",
				user.getSecondaryUsers()[0].getUid());*/
	}

	@Test
	public void testSessionManager_merged_uid2() throws Exception {

		assertEquals(0, getDbRowCount("unm_history"));

		final AppSession appSession = sessionManager.login_classic(API_KEY,
				"dandrianavalontsalama", "dummy2");

		assertNotNull(appSession);

		final fr.univmobile.backend.domain.User user = appSession.getUser();

		assertNotEquals("dandrianavalontsalama", user.getUsername());

		assertEquals("dandriana", user.getUsername());

		//assertTrue(user.isNullPrimaryUser());

		//assertEquals(1, user.sizeOfSecondaryUsers());
		//assertEquals("dandrianavalontsalama",
		//		user.getSecondaryUsers()[0].getUid());
	}

	@Test
	public void testGetUser_no_login_uid1() throws Exception {

		assertEquals(0, getDbRowCount("unm_history"));

		final fr.univmobile.backend.domain.User user = users.findByUsername("dandriana");

		assertEquals("dandriana", user.getUsername());

		assertNotEquals("dandrianavalontsalama", user.getUsername());

		//assertTrue(user.isNullPrimaryUser());
	}

	@Test
	public void testGetUser_no_login_uid2() throws Exception {

		assertEquals(0, getDbRowCount("unm_history"));

		final fr.univmobile.backend.domain.User user = users.findByUsername("dandrianavalontsalama");

		assertEquals("dandrianavalontsalama", user.getUsername());

		assertNotEquals("dandriana", user.getUsername());

		//assertFalse(user.isNullPrimaryUser());
	}
}
