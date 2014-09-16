package fr.univmobile.backend.core;

import static org.apache.commons.lang3.StringUtils.isBlank;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.io.File;
import java.io.IOException;

import org.junit.Before;
import org.junit.Test;

import static fr.univmobile.backend.core.impl.ConnectionType.H2;
import fr.univmobile.backend.core.impl.LogQueueDbImpl;
import fr.univmobile.backend.core.impl.UserManagerImpl;
import fr.univmobile.commons.datasource.impl.BackendDataSourceFileSystem;

public class CreateUsersTest extends AbstractDbEnabledTest {

	public CreateUsersTest() {

		super(new File("src/test/data/users/001"), new File(
				"src/test/data/regions/001"),
				new File("src/test/data/pois/001"), new File(
						"src/test/data/comments/001"));
	}

	@Before
	public void setUp() throws Exception {

		/*
		 * final File originalDataDir = new File("src/test/data/users/001");
		 * 
		 * tmpDataDir = new File("target/CreateUsersTest");
		 * 
		 * if (tmpDataDir.isDirectory()) { FileUtils.forceDelete(tmpDataDir); }
		 * 
		 * FileUtils.copyDirectory(originalDataDir, tmpDataDir);
		 */
		users = BackendDataSourceFileSystem.newDataSource(UserDataSource.class,
				dataDir_users);
	}

	private UserDataSource users;

	// private File tmpDataDir;

	private int countFiles() throws IOException {

		int total = 0;

		for (final File file : dataDir_users.listFiles()) {

			if (!file.isFile() || !file.getName().endsWith(".xml")) {
				continue;
			}

			++total;
		}

		return total;
	}

	@Test
	public void test_counts() throws Exception {

		assertEquals(2, users.getAllBy(String.class, "uid").size());

		assertEquals(3, countFiles());
	}

	@Test
	public void test_create() throws Exception {

		LogQueueDbImpl.setPrincipal("crezvani");
		
		assertEquals(0, getDbRowCount("unm_history"));

		final UserBuilder user = users.create();

		assertEquals("users", user.getCategory());

		user.setUid("toto");

		user.setAuthorName("dandriana").setTitle("dandriana");

		assertEquals("dandriana", user.getAuthorName());

		assertTrue(user.isNullId());
		assertTrue(user.isNullSelf());

		final UserManager userManager = new UserManagerImpl(
				logQueue, users, searchManager,H2, cxn);
		
		final User saved = userManager.addUser(user);

		assertEquals(2, getDbRowCount("unm_history"));

		assertFalse(user.isNullId());
		assertFalse(user.isNullSelf());
		assertFalse(isBlank(user.getSelf()));

		assertEquals(saved.getId(), user.getSelf());

		assertEquals("dandriana", saved.getAuthorName());
		assertEquals("dandriana", saved.getTitle());

		assertEquals(3, users.getAllBy(String.class, "uid").size());
		assertEquals(4, countFiles());
	}
}
