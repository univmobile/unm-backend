package fr.univmobile.backend.core;

import static org.junit.Assert.*;

import java.io.File;
import java.util.Iterator;
import java.util.Map;
import java.util.NoSuchElementException;

import net.avcompris.binding.helper.BinderUtils;

import org.junit.Before;
import org.junit.Test;

import fr.univmobile.commons.datasource.impl.BackendDataSourceFileSystem;

public class UsersTest {

	@Before
	public void setUp() throws Exception {

		users = BackendDataSourceFileSystem.newDataSource(UserDataSource.class,
				new File("src/test/data/users/001"));
	}

	private UserDataSource users;

	@Test
	public void test_getByRemoteUser_crezvaniAttributes() throws Exception {

		final User crezvani = users.getByRemoteUser("crezvani@univ-paris1.fr");

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

		final User crezvani = users
				.getById("fr.univmobile:unm-backend:test/users/001:crezvani_1");

		assertEquals("crezvani", crezvani.getUid());

		assertTrue(users.isLatest(crezvani));

		assertSame(crezvani, users.getLatest(crezvani));

		assertFalse(users.hasParent(crezvani));

		assertTrue(crezvani.isNullParent());
		
		assertTrue(crezvani.isAdmin());
		assertFalse(crezvani.isSuperadmin());
		assertFalse(crezvani.isStudent());
		
		assertEquals("ubo", crezvani.getPrimaryUniversity().getId());
		assertEquals("Université de Bretagne Occidentale", crezvani.getPrimaryUniversity().getTitle());
		
		assertEquals(1, crezvani.getSecondaryUniversities().length);

		assertEquals("rennes1", crezvani.getSecondaryUniversities()[0].getId());
		assertEquals("Université de Rennes 1", crezvani.getSecondaryUniversities()[0].getTitle());
}

	@Test(expected = NoSuchElementException.class)
	public void test_crezvani_getParent() throws Exception {

		final User crezvani = users.getByUid("crezvani");

		assertEquals("crezvani", crezvani.getUid());

		users.getParent(crezvani);
	}

	@Test
	public void test_nullPassword_detach() throws Exception {

		final User crezvani = users.getByUid("crezvani");

		assertFalse(crezvani.isNullPassword());
		
		assertNotNull(crezvani.getPassword());

		BinderUtils.detach(crezvani);

		final User dandriana = users.getByUid("dandriana");
	
		assertTrue(dandriana.isNullPassword());
		
		//assertNull(dandriana.getPassword());
		
		BinderUtils.detach(dandriana);
	}

	@Test
	public void test_dandriana_getParent() throws Exception {

		final User dandriana = users.getByUid("dandriana");

		assertTrue(users.isLatest(dandriana));

		assertSame(dandriana, users.getLatest(dandriana));

		assertTrue(users.hasParent(dandriana));

		assertFalse(dandriana.isNullParent());

		final User parent = users.getParent(dandriana);

		assertFalse(users.isLatest(parent));

		assertSame(dandriana, users.getLatest(parent));

		assertFalse(users.hasParent(parent));

		assertTrue(parent.isNullParent());
	}

	@Test
	public void test_getAll() throws Exception {

		final Map<String, User> allUsers = users.getAllBy(String.class, "uid");

		assertEquals(2, allUsers.size());

		for (final User user : allUsers.values()) {

			assertTrue(users.isLatest(user));
		}

		final Iterator<User> it = allUsers.values().iterator();

		final User u0 = it.next();
		final User u1 = it.next();

		final User crezvani;
		final User dandriana;

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

	@Test
	public void test_profileImageUrl() throws Exception {

		final User crezvani = users
				.getById("fr.univmobile:unm-backend:test/users/001:crezvani_1");

		assertNull(crezvani.getProfileImageUrl());
	}
}
