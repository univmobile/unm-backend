package fr.univmobile.backend.core;

import static com.google.common.base.Preconditions.checkNotNull;
import static fr.univmobile.backend.core.impl.ConnectionType.H2;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.fail;

import java.io.File;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import org.apache.commons.io.FileUtils;
import org.junit.After;
import org.junit.Before;

import fr.univmobile.backend.core.impl.IndexationImpl;
import fr.univmobile.backend.core.impl.LogQueueDbImpl;
import fr.univmobile.backend.core.impl.SearchManagerImpl;
import fr.univmobile.backend.history.LogQueue;

public abstract class AbstractDbEnabledTest {

	protected AbstractDbEnabledTest(final File originalDataDir_users,
			final File originalDataDir_regions,
			final File originalDataDir_pois, //
			final File originalDataDir_comments) {

		this.originalDataDir_users = checkNotNull(originalDataDir_users);
		this.originalDataDir_regions = checkNotNull(originalDataDir_regions);
		this.originalDataDir_pois = checkNotNull(originalDataDir_pois);
		this.originalDataDir_comments = checkNotNull(originalDataDir_comments);
	}

	private final File originalDataDir_users;
	private final File originalDataDir_regions;
	private final File originalDataDir_pois;
	private final File originalDataDir_comments;

	protected Connection cxn;

	protected File dataDir_users;
	protected File dataDir_comments;
	protected File dataDir_pois;
	protected LogQueue logQueue;
	protected SearchManager searchManager;

	@After
	public final void tearDownDb() throws Exception {

		if (cxn != null) {

			cxn.close();

			cxn = null;
		}
	}

	@Before
	public final void setUpDb() throws Exception {

		final String classSimpleName = getClass().getSimpleName();

		// 1. ENSURE ALL LOCAL DB FILES ARE DELETED

		for (final String extension : new String[] { ".h2.db", ".mv.db" }) {

			final File dbFile = new File("target", classSimpleName + extension);

			FileUtils.deleteQuietly(dbFile);

			assertFalse(dbFile.exists());
		}

		// 2. ENSURE ALL LOCAL DB FILES ARE DELETED

		for (final File file : new File("target").listFiles()) {

			final String filename = file.getName();

			if (filename.startsWith(classSimpleName)
					&& filename.endsWith(".db")) {

				fail("Found a filename ending with .db: " + filename);
			}
		}

		// 3. CREATE NEW LOCAL DATABASE

		final String url = "jdbc:h2:./target/" + classSimpleName;

		cxn = DriverManager.getConnection(url);

		// 4. CREATE NEW LOCAL DATA DIRECTORIES

		dataDir_comments = new File("target", classSimpleName + "_comments");

		if (dataDir_comments.isDirectory()) {
			FileUtils.forceDelete(dataDir_comments);
		}

		FileUtils.copyDirectory(originalDataDir_comments, dataDir_comments);

		dataDir_users = new File("target", classSimpleName + "_users");

		if (dataDir_users.isDirectory()) {
			FileUtils.forceDelete(dataDir_users);
		}

		FileUtils.copyDirectory(originalDataDir_users, dataDir_users);

		final File dataDir_regions = new File("target", classSimpleName
				+ "_regions");

		if (dataDir_regions.isDirectory()) {
			FileUtils.forceDelete(dataDir_regions);
		}

		FileUtils.copyDirectory(originalDataDir_regions, dataDir_regions);

		dataDir_pois = new File("target", classSimpleName + "_pois");

		if (dataDir_pois.isDirectory()) {
			FileUtils.forceDelete(dataDir_pois);
		}

		FileUtils.copyDirectory(originalDataDir_pois, dataDir_pois);

		// 5. LOGQUEUE AND SEARCHMANAGER

		logQueue = new LogQueueDbImpl(H2, cxn);

		searchManager = new SearchManagerImpl(logQueue, H2, cxn);

		// 6. DB INIT: DATA INDEXATION

		final Indexation indexation = new IndexationImpl(dataDir_users,
				dataDir_regions, dataDir_pois, dataDir_comments, searchManager,
				H2, cxn);

		indexation.indexData(null);
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
