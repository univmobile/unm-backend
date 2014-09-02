package fr.univmobile.backend.core;

import static org.apache.commons.lang3.StringUtils.isBlank;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.io.File;
import java.io.IOException;

import org.apache.commons.io.FileUtils;
import org.junit.Before;
import org.junit.Test;

import fr.univmobile.commons.datasource.impl.BackendDataSourceFileSystem;
import fr.univmobile.commons.tx.Lock;
import fr.univmobile.commons.tx.TransactionManager;

public class CreateUsersTest {

	@Before
	public void setUp() throws Exception {

		final File originalDataDir = new File("src/test/data/users/001");

		tmpDataDir = new File("target/CreateUsersTest");

		if (tmpDataDir.isDirectory()) {
			FileUtils.forceDelete(tmpDataDir);
		}

		FileUtils.copyDirectory(originalDataDir, tmpDataDir);

		users = BackendDataSourceFileSystem.newDataSource(UserDataSource.class,
				tmpDataDir);
	}

	private UserDataSource users;

	private File tmpDataDir;

	private int countFiles() throws IOException {

		int total = 0;

		for (final File file : tmpDataDir.listFiles()) {

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

		final UserBuilder user = users.create();

		assertEquals("users", user.getCategory());

		user.setUid("toto");

		user.setAuthorName("dandriana").setTitle("dandriana");

		assertEquals("dandriana", user.getAuthorName());

		assertTrue(user.isNullId());
		assertTrue(user.isNullSelf());

		final TransactionManager tx = TransactionManager.getInstance();

		final Lock lock = tx.acquireLock(5000, "users", "toto");

		final User saved = lock.save(user);

		lock.commit();

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
