package fr.univmobile.backend.client.test;

import static fr.univmobile.backend.core.impl.ConnectionType.H2;
import static fr.univmobile.testutil.TestUtils.copyDirectory;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;

import java.io.File;
import java.sql.Connection;
import java.sql.DriverManager;

import org.apache.commons.io.FileUtils;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import fr.univmobile.backend.client.AppToken;
import fr.univmobile.backend.client.SessionClient;
import fr.univmobile.backend.client.SessionClientFromJSON;
import fr.univmobile.backend.client.SessionClientFromLocal;
import fr.univmobile.backend.client.json.SessionJSONClient;
import fr.univmobile.backend.client.json.SessionJSONClientImpl;
import fr.univmobile.backend.core.Indexation;
import fr.univmobile.backend.core.SessionManager;
import fr.univmobile.backend.core.UserDataSource;
import fr.univmobile.backend.core.impl.IndexationImpl;
import fr.univmobile.backend.core.impl.LogQueueDbImpl;
import fr.univmobile.backend.core.impl.SearchManagerImpl;
import fr.univmobile.backend.core.impl.SessionManagerImpl;
import fr.univmobile.backend.history.LogQueue;
import fr.univmobile.commons.datasource.impl.BackendDataSourceFileSystem;

public class SessionThroughJSONTest {

	@Before
	public void setUp() throws Exception {

		final File tmpDataDir_comments = copyDirectory(new File(
				"src/test/data/users/001"), new File(
				"target/SessionClientFromLocalTest"));

		final UserDataSource users = BackendDataSourceFileSystem.newDataSource(
				UserDataSource.class, tmpDataDir_comments);

		final File dbFile = new File("target/SessionThroughJSONTest.h2.db");

		FileUtils.deleteQuietly(dbFile);

		assertFalse(dbFile.exists());

		final String url = "jdbc:h2:./target/SessionThroughJSONTest";

		cxn = DriverManager.getConnection(url);

		final LogQueue logQueue = new LogQueueDbImpl(H2, cxn);

		final SearchManagerImpl searchManager = new SearchManagerImpl(logQueue,
				H2, cxn);

		final Indexation indexation = new IndexationImpl(//
				new File("src/test/data/users/001"), //
				new File("src/test/data/regions/001"), //
				new File("src/test/data/pois/001"), //
				new File("src/test/data/comments/001"), //
				searchManager, H2, cxn);

		indexation.indexData(null);

		final SessionManager sessionManager = new SessionManagerImpl(logQueue,
				users, H2, cxn);

		final SessionClient sessionClient = new SessionClientFromLocal(
				"http://dummy/", sessionManager);

		sessionJSONClient = new SessionJSONClientImpl(sessionClient);

		client = new SessionClientFromJSON(sessionJSONClient);

		LogQueueDbImpl.setAnonymous();// Principal(null); // "crezvani");
	}

	private SessionJSONClient sessionJSONClient;
	private SessionClient client;
	private Connection cxn;

	@After
	public void tearDown() throws Exception {

		if (cxn != null) {

			cxn.close();

			cxn = null;
		}
	}

	private static final String API_KEY = "123456";

	@Test
	public void testThroughJSON_login_OK() throws Exception {

		final AppToken appToken = client.login(API_KEY, "crezvani",
				"Hello+World!");

		assertNotNull(appToken);
	}

	@Test
	public void testThroughJSON_login_invalid() throws Exception {

		final AppToken appToken = client.login(API_KEY, "crezvani", "toto");

		assertNull(appToken);
	}

	@Test
	public void testThroughJSON_login_logout() throws Exception {

		final AppToken appToken = client.login(API_KEY, "crezvani",
				"Hello+World!");

		assertNotNull(appToken);

		client.logout(API_KEY, appToken.getId());
	}
}
