package fr.univmobile.backend.core;

import java.io.File;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import org.junit.After;
import org.junit.Before;

public abstract class AbstractDbEnabledTest {

	@Before
	public final void setUpDb() throws Exception {

		final String url = "jdbc:h2:./target/" + getClass().getSimpleName();

		cxn = DriverManager.getConnection(url);
	}

	protected Connection cxn;

	@After
	public final void tearDownDb() throws Exception {

		if (cxn != null) {

			cxn.close();

			cxn = null;
		}
	}

	// private final TransactionManager tx = TransactionManager.getInstance();

	protected static int getFileCount(final File dir) throws IOException {

		return dir.listFiles().length;
	}

	protected final int getDbRowCount(final String tablename)
			throws SQLException {

		return executeDbQueryInt("SELECT COUNT(1) FROM " + tablename);
	}

	protected final int executeDbQueryInt(final String query)
			throws SQLException {

		final Statement stmt = cxn.createStatement();
		try {
			final ResultSet rs = stmt.executeQuery(query);
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
}
