package fr.univmobile.backend.sanitycheck;

import java.io.File;

import org.junit.Test;

public class BackendCheckerTest {

	@Test(expected = SanityCheckException.class)
	public void testSanityCheck_users_crash_001() throws Exception {

		final BackendChecker checker = new BackendChecker();

		checker.checkUsersDirectory(new File(
				"src/test/sanitycheck/users_crash_001"));
	}

	@Test
	public void testSanityCheck_OK() throws Exception {

		final BackendChecker checker = new BackendChecker();

		checker.checkUsersDirectory(new File("src/test/data/users/001"));
		checker.checkUsersDirectory(new File("src/test/data/users/002"));
		checker.checkUsersDirectory(new File("src/test/data/users/003"));
	}
}
