package fr.univmobile.backend.sysadmin;

import static com.google.common.base.Preconditions.checkNotNull;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;

import javax.xml.parsers.ParserConfigurationException;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.joda.time.DateTime;
import org.xml.sax.SAXException;

import fr.univmobile.backend.core.Comment;
import fr.univmobile.backend.core.Comment.Context;
import fr.univmobile.backend.core.Comment.ContextType;
import fr.univmobile.backend.core.Poi;
import fr.univmobile.backend.core.Region;
import fr.univmobile.backend.core.User;
import fr.univmobile.commons.datasource.Entry;

/**
 * Code for the "index" command-line tool.
 */
class IndexationTool extends AbstractTool {

	public IndexationTool(final File dataDir, final ConnectionType dbType,
			final Connection cxn) throws IOException,
			ParserConfigurationException {

		super(dbType, cxn);

		this.dataDir = checkNotNull(dataDir, "dataDir");
	}

	private final File dataDir;

	@Override
	public void run() throws IOException, SQLException, SAXException {

		if (!doesTableExist(tablePrefix + "categories")) {
			executeUpdate("createTable_categories");
		}

		if (!doesTableExist(tablePrefix + "revfiles")) {
			executeUpdate("createTable_revfiles");
		}

		for (final String category : CATEGORIES) {

			if (!doesTableExist(tablePrefix + "entities_" + category)) {
				executeUpdate("createTable_entities_" + category);
			}
		}

		for (final String category : CATEGORIES) {

			createCategoryIfNeeded(category);
		}

		// 1. LOCK ALL

		for (final String category : CATEGORIES) {

			lockCategory(category);
		}

		// 2. CLEAR ALL

		for (final String category : CATEGORIES) {

			clearCategory(category);
		}

		// 3. INSERT REVFILES

		// final Map<String, Integer> counts = new HashMap<String, Integer>();

		for (final String category : CATEGORIES) {

			final File categoryDir = getCategoryDir(category);

			int count = 0;

			for (final File file : categoryDir.listFiles()) {

				loadRevfile(category, file.getName());

				++count;
			}

			// counts.put(category,count);

			System.out.println("  " + category + ": " + count);
		}

		// 4. REVFILE PARENTS

		for (final String category : CATEGORIES) {

			final File categoryDir = getCategoryDir(category);

			for (final File file : categoryDir.listFiles()) {

				insertRevfileParent(category, file.getName());
			}

			executeUpdate("unlockRevfiles", category);
		}

		// 5. ENTITIES

		loadEntities("users", User.class, new EntityLoader<User>() {
			@Override
			public void loadEntity(final User user, final int activeRevfileId)
					throws SQLException {

				executeUpdate("createUser", activeRevfileId, user.getUid(),
						user.getRemoteUser());
			}
		});

		loadEntities("regions", Region.class, new EntityLoader<Region>() {
			@Override
			public void loadEntity(final Region region,
					final int activeRevfileId) throws SQLException {

				executeUpdate("createRegion", activeRevfileId, region.getUid());
			}
		});

		loadEntities("pois", Poi.class, new EntityLoader<Poi>() {
			@Override
			public void loadEntity(final Poi poi, final int activeRevfileId)
					throws SQLException {

				executeUpdate("createPoi", activeRevfileId, poi.getUid());

			}
		});

		loadEntities("comments", Comment.class, new EntityLoader<Comment>() {
			@Override
			public void loadEntity(final Comment comment,
					final int activeRevfileId) throws SQLException {

				final DateTime initialPostedAt = comment.getPostedAt();
				final DateTime activePostedAt = comment.getPostedAt();
				final Context mainContext = comment.getMainContext();
				final ContextType contextType = mainContext.getType();
				final int poiUid;

				if (contextType == ContextType.LOCAL_POI) {
					poiUid = mainContext.getUid();
				} else {
					throw new IllegalStateException("Unknown contextType: "
							+ contextType);
				}

				executeUpdate("createComment", activeRevfileId,
						comment.getUid(), //
						initialPostedAt, activePostedAt, //
						poiUid);
			}
		});

		// 9. UNLOCK ALL

		unlockCategory("users");
		unlockCategory("regions");
		unlockCategory("pois");
		unlockCategory("comments");
	}

	private <U extends Entry<U>> void loadEntities(final String category,
			final Class<U> clazz, final EntityLoader<U> loader)
			throws SQLException, IOException, SAXException {

		final String sql_getRevfiles = getSql("getRevfiles");
		final String sql_getChildren = getSql("getChildren");

		final PreparedStatement pstmt1 = cxn.prepareStatement(sql_getRevfiles);
		try {
			final PreparedStatement pstmt2 = cxn
					.prepareStatement(sql_getChildren);
			try {

				final File categoryDir = getCategoryDir(category);

				pstmt1.setString(1, category);

				final ResultSet rs1 = pstmt1.executeQuery();
				try {

					while (rs1.next()) {

						final int revfileId = rs1.getInt(1);

						pstmt2.setInt(1, revfileId);

						final ResultSet rs2 = pstmt2.executeQuery();
						try {

							if (rs2.next()) { // id has children: continue
								continue;
							}

						} finally {
							rs2.close();
						}

						// id has no child:

						final String path = rs1.getString(2);

						final U entity = loadEntity(
								new File(categoryDir, path), clazz);

						loader.loadEntity(entity, revfileId);

					}

				} finally {
					rs1.close();
				}

			} finally {
				pstmt2.close();
			}
		} finally {
			pstmt1.close();
		}
	}

	private void clearCategory(final String category) throws SQLException {

		executeUpdate("clearCategory_1_detachRevfiles", category);

		// This SQL query cannot be stored in sql.yaml, since the table’s name
		// is itself dynamic.
		final String sql = "DELETE FROM " + tablePrefix + "entities_"
				+ category;

		final Statement stmt = cxn.createStatement();
		try {

			stmt.executeUpdate(sql);

		} finally {
			stmt.close();
		}

		executeUpdate("clearCategory_2_deleteRevfiles", category);
	}

	private void loadRevfile(final String category, final String path)
			throws SQLException, IOException, SAXException {

		final DateTime now = new DateTime();

		final File categoryDir = getCategoryDir(category);

		final File file = new File(categoryDir, path);

		if (!file.isFile()) {
			throw new FileNotFoundException("Not a file: "
					+ file.getCanonicalPath());
		}

		final Entry<?> entry = loadEntity(file);

		final String atomId = entry.getId();

		final String sql = getSql("createRevfile");

		final PreparedStatement pstmt = cxn.prepareStatement(sql);
		try {

			pstmt.setString(1, category);
			pstmt.setString(2, path);
			pstmt.setString(3, atomId);
			pstmt.setTimestamp(4, new Timestamp(now.toDate().getTime()));
			pstmt.setInt(5, STATUS_ACTIVE);

			try {

				pstmt.executeUpdate();

			} catch (final SQLException e) {

				System.err.println("Cannot insert revfile into DataBase:");
				System.err.println("  category: " + category);
				System.err.println("  path: " + path);
				System.err.println("  atomId: " + atomId);
				System.err.println(e);

				throw e;
			}

		} finally {
			pstmt.close();
		}
	}

	private void insertRevfileParent(final String category, final String path)
			throws SQLException, IOException, SAXException {

		final File categoryDir = getCategoryDir(category);

		final File file = new File(categoryDir, path);

		if (!file.isFile()) {
			throw new FileNotFoundException("Not a file: "
					+ file.getCanonicalPath());
		}

		final Entry<?> entry = loadEntity(file);

		if (entry.isNullParent()) {
			return;
		}

		if (log.isDebugEnabled()) {
			log.debug("Setting parent for revfile: " + entry.getId());
		}

		final String parentAtomId = entry.getParentId();

		final int parentRevfileId = getRevfileIdByAtomId(category, parentAtomId);

		executeUpdate("setRevfileParentId", parentRevfileId, category, path);
	}

	private int getRevfileIdByAtomId(final String category, final String atomId)
			throws SQLException {

		return executeQueryGetInt("getRevfileIdByAtomId", category, atomId);
	}

	public static final int STATUS_ACTIVE = 1;

	private boolean doesTableExist(final String tablename) throws SQLException {

		final String NULL_CATALOG = null;
		final String NULL_SCHEMA_PATTERN = null;
		final String[] ALL_TYPES = null;

		final String tablenamePattern;

		switch (dbType) {

		case MYSQL:
			tablenamePattern = tablename;
			break;
		case H2:// H2: Use uppercase when unquoted
			tablenamePattern = tablename.toUpperCase();
			break;
		default:
			throw new IllegalStateException("Unknown dbType: " + dbType);
		}

		final ResultSet rs = cxn.getMetaData().getTables(NULL_CATALOG,
				NULL_SCHEMA_PATTERN, tablenamePattern, ALL_TYPES);
		try {

			return rs.next() ? true : false; // Keep the long syntax for clarity

		} finally {
			rs.close();
		}
	}

	private static final Log log = LogFactory.getLog(IndexationTool.class);

	private boolean doesCategoryExist(final String category)
			throws SQLException {

		final String sql = getSql("getCategoryPath");

		final PreparedStatement pstmt = cxn.prepareStatement(sql);
		try {

			pstmt.setString(1, category);

			final ResultSet rs = pstmt.executeQuery();
			try {

				return rs.next() ? true : false;

			} finally {
				rs.close();
			}

		} finally {
			pstmt.close();
		}
	}

	private void createCategoryIfNeeded(final String category)
			throws SQLException, IOException {

		if (doesCategoryExist(category)) {
			return;
		}

		final File categoryDir = new File(dataDir, category);

		if (!categoryDir.isDirectory()) {
			throw new FileNotFoundException("Dir for category: " + category
					+ " is not a directory: " + categoryDir.getCanonicalPath());
		}

		setCategoryDir(category, categoryDir);

		executeUpdate("createCategory", category,
				categoryDir.getCanonicalPath());
	}

	private void lockCategory(final String category) throws SQLException {

		final DateTime now = new DateTime();

		final int result = executeUpdate("lockCategory", now, category);

		if (result != 1) {
			throw new RuntimeException("Cannot lock category: " + category
					+ " (result: " + result + ", should be: 1)");
		}
	}

	private void unlockCategory(final String category) throws SQLException {

		executeUpdate("unlockCategory", category);
	}

	private interface EntityLoader<U> {

		void loadEntity(U entity, int activeRevfileId) throws SQLException;
	}
}
