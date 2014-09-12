package fr.univmobile.backend.sysadmin;

import static fr.univmobile.backend.core.impl.ConnectionType.H2;
import static org.junit.Assert.*;

import java.io.File;
import java.sql.Connection;
import java.sql.DriverManager;

import org.apache.commons.io.FileUtils;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

public class CommentTest {

	@Before
	public void setUp() throws Exception {

		final File dbFile = new File("target/CommentTest.h2.db");

		FileUtils.deleteQuietly(dbFile);

		assertFalse(dbFile.exists());

		final String url = "jdbc:h2:./target/CommentTest";

		cxn = DriverManager.getConnection(url);

		final AbstractTool indexation = new IndexationTool( //
				new File("src/test/data/001"), H2, cxn);

		indexation.run();
	}

	@After
	public void tearDown() throws Exception {

		if (cxn != null) {

			cxn.close();

			cxn = null;
		}
	}

	private Connection cxn = null;

	@Test
	public void testGetComments() throws Exception {

		final AbstractTool commentTool = new CommentTool(30, null, H2, cxn);

		final Result result = commentTool.run();

		assertEquals(3, result.getRowCount());
	}
}
