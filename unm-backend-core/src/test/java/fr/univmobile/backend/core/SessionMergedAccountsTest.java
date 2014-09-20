package fr.univmobile.backend.core;

import static fr.univmobile.backend.core.impl.ConnectionType.H2;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import java.io.File;

import org.junit.Before;
import org.junit.Test;

import fr.univmobile.backend.core.impl.LogQueueDbImpl;
import fr.univmobile.backend.core.impl.SessionManagerImpl;
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

		users = BackendDataSourceFileSystem.newDataSource(UserDataSource.class,
				dataDir_users);

		sessionManager = new SessionManagerImpl(logQueue, users, H2, cxn);

		LogQueueDbImpl.setAnonymous();
	}

	private UserDataSource users;
	private SessionManager sessionManager;

	private static final String API_KEY = "abcdefgh";

	@Test
	public void testSessionManager_uid1() throws Exception {

		assertEquals(0, getDbRowCount("unm_history"));

		final AppSession appSession = sessionManager.login_classic(API_KEY,
				"dandriana", "Hello+World!");

		assertNotNull(appSession);
	}

	@Test
	public void testSessionManager_uid2() throws Exception {

		assertEquals(0, getDbRowCount("unm_history"));

		final AppSession appSession = sessionManager.login_classic(API_KEY,
				"dandriana", "Hello+World!");

		assertNotNull(appSession);
	}
}
