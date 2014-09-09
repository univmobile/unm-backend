package fr.univmobile.backend.sysadmin;

import static fr.univmobile.backend.core.impl.ConnectionType.H2;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;

import java.io.File;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import org.apache.commons.io.FileUtils;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

public class IndexationTest {

	@Before
	public void setUp() throws Exception {

		final File dbFile = new File("target/IndexationTest.h2.db");

		FileUtils.deleteQuietly(dbFile);

		assertFalse(dbFile.exists());

		final String url = "jdbc:h2:./target/IndexationTest";

		cxn = DriverManager.getConnection(url);
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
	public void testIndexationRun() throws Exception {

		assertFalse(doesTableExist("unm_categories"));
		assertFalse(doesTableExist("unm_revfiles"));
		assertFalse(doesTableExist("unm_entities_users"));
		assertFalse(doesTableExist("unm_entities_regions"));
		assertFalse(doesTableExist("unm_entities_pois"));
		assertFalse(doesTableExist("unm_entities_comments"));

		final AbstractTool indexation = new IndexationTool( //
				new File("src/test/data/001"), H2, cxn);

		indexation.run();

		assertEquals(4, getRowCount("unm_categories"));
		assertEquals(286, getRowCount("unm_revfiles"));
		assertEquals(286, getInt("SELECT COUNT(1) FROM unm_revfiles"
				+ " WHERE status = 1"));

		assertEquals(1,
				getInt("SELECT COUNT(1) FROM unm_revfiles"
						+ " WHERE parent_id IS NOT NULL"
						+ " AND category_id = 'users'"));
		assertEquals(1, getInt("SELECT COUNT(1) FROM unm_revfiles"
				+ " WHERE parent_id IS NOT NULL"));

		assertEquals(2, getRowCount("unm_entities_users"));
		assertEquals(3, getRowCount("unm_entities_regions"));
		assertEquals(277, getRowCount("unm_entities_pois"));
		assertEquals(3, getRowCount("unm_entities_comments"));

		assertEquals(2, getInt("SELECT COUNT(1) FROM unm_entities_users"
				+ " WHERE unm_status = 1"));

		assertEquals(0, getInt("SELECT COUNT(1) FROM unm_categories"
				+ " WHERE locked_since IS NOT NULL"));
		assertEquals(0, getInt("SELECT COUNT(1) FROM unm_revfiles"
				+ " WHERE locked_since IS NOT NULL"));
	}

	private int getInt(final String sql) throws SQLException {

		final Statement stmt = cxn.createStatement();
		try {
			final ResultSet rs = stmt.executeQuery(sql);
			try {

				rs.next();

				return rs.getInt(1);

			} finally {
				rs.close();
			}
		} finally {
			stmt.close();
		}
	}

	private int getRowCount(final String tablename) throws SQLException {

		return getInt("SELECT COUNT(1) FROM " + tablename);
	}

	private boolean doesTableExist(final String tablename) throws SQLException {

		final String NULL_CATALOG = null;
		final String NULL_SCHEMA_PATTERN = null;
		final String[] ALL_TYPES = null;

		final ResultSet rs = cxn.getMetaData().getTables(NULL_CATALOG,
				NULL_SCHEMA_PATTERN, tablename, ALL_TYPES);
		try {

			return rs.next() ? true : false; // Keep the long syntax for clarity

		} finally {
			rs.close();
		}
	}

	@Test
	public void testIndexationIdempotent() throws Exception {

		final AbstractTool indexation = new IndexationTool( //
				new File("src/test/data/001"), H2, cxn);

		indexation.run();

		indexation.run();
	}
}
