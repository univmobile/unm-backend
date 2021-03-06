package fr.univmobile.commons.datasource;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;

import java.io.File;
import java.util.Iterator;
import java.util.Map;
import java.util.NoSuchElementException;

import org.junit.Before;
import org.junit.Test;

import fr.univmobile.commons.datasource.impl.BackendDataSourceFileSystem;

public class MyUsersTest {

	@Before
	public void setUp() throws Exception {

		users = BackendDataSourceFileSystem.newDataSource(MyUserDataSource.class,
				new File("src/test/data/myusers/001"));
	}

	private MyUserDataSource users;

	@Test
	public void test_getByRemoteUser_crezvaniAttributes() throws Exception {

		final MyUser crezvani = users.getByRemoteUser("crezvani@univ-paris1.fr");

		assertEquals("crezvani", crezvani.getUid());
	}

	@Test
	public void test_crezvani_not_isNullByRemoteUser() throws Exception {

		assertFalse(users.isNullByRemoteUser("crezvani@univ-paris1.fr"));
	}

	@Test
	public void test_tformica_isNullByRemoteUser() throws Exception {

		assertTrue(users.isNullByRemoteUser("tformica@univ-paris1.fr"));
	}

	@Test
	public void test_getById_crezvaniAttributes() throws Exception {

		final MyUser crezvani = users
				.getById("fr.univmobile:unm-backend:test/users/001:crezvani_1");

		assertEquals("crezvani", crezvani.getUid());

		assertTrue(users.isLatest(crezvani));

		assertSame(crezvani, users.getLatest(crezvani));

		assertFalse(users.hasParent(crezvani));

		assertTrue(crezvani.isNullParent());
	}

	@Test(expected = NoSuchElementException.class)
	public void test_crezvani_getParent() throws Exception {

		final MyUser crezvani = users.getByUid("crezvani");

		assertEquals("crezvani", crezvani.getUid());

		users.getParent(crezvani);
	}

	@Test
	public void test_dandriana_getParent() throws Exception {

		final MyUser dandriana = users.getByUid("dandriana");

		assertTrue(users.isLatest(dandriana));

		assertSame(dandriana, users.getLatest(dandriana));

		assertTrue(users.hasParent(dandriana));

		assertFalse(dandriana.isNullParent());

		final MyUser parent = users.getParent(dandriana);

		assertFalse(users.isLatest(parent));

		assertSame(dandriana, users.getLatest(parent));

		assertFalse(users.hasParent(parent));

		assertTrue(parent.isNullParent());
	}

	@Test
	public void test_getAll() throws Exception {

		final Map<String, MyUser> allUsers = users.getAllBy(String.class, "uid");

		assertEquals(2, allUsers.size());

		for (final MyUser user : allUsers.values()) {

			assertTrue(users.isLatest(user));
		}

		final Iterator<MyUser> it = allUsers.values().iterator();

		final MyUser u0 = it.next();
		final MyUser u1 = it.next();

		final MyUser crezvani;
		final MyUser dandriana;

		if ("dandriana".equals(u0.getUid())) {
			dandriana = u0;
			crezvani = u1;
		} else {
			dandriana = u1;
			crezvani = u0;
		}

		assertEquals("crezvani", crezvani.getUid());
		assertEquals("dandriana", dandriana.getUid());
	}
}
