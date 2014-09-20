package fr.univmobile.backend.core.impl;

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
import java.util.HashMap;
import java.util.Map;

import javax.annotation.Nullable;
import javax.xml.parsers.ParserConfigurationException;

import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.joda.time.DateTime;
import org.xml.sax.SAXException;

import fr.univmobile.backend.core.Comment;
import fr.univmobile.backend.core.Comment.Context;
import fr.univmobile.backend.core.Comment.ContextType;
import fr.univmobile.backend.core.Indexation;
import fr.univmobile.backend.core.IndexationObserver;
import fr.univmobile.backend.core.Poi;
import fr.univmobile.backend.core.Region;
import fr.univmobile.backend.core.SearchEntry;
import fr.univmobile.backend.core.SearchManager;
import fr.univmobile.backend.core.User;
import fr.univmobile.commons.datasource.Entry;

/**
 * Data indexation, from XML to DataBase.
 */
public class IndexationImpl extends AbstractImpl implements Indexation {

	private final String tablePrefix= DbEnabled.TABLE_PREFIX;
	
	public IndexationImpl(final File dataDir, //
			final SearchManager searchManager, //
			final ConnectionType dbType, final Connection cxn)
			throws IOException, ParserConfigurationException {

		this( //
				new File(dataDir, "users"), //
				new File(dataDir, "regions"), //
				new File(dataDir, "pois"), //
				new File(dataDir, "comments"), //
				searchManager, dbType, cxn);
	}

	public IndexationImpl(//
			final File usersDir, final File regionsDir, //
			final File poisDir, final File commentsDir, //
			final SearchManager searchManager, //
			final ConnectionType dbType, final Connection cxn)
			throws IOException, ParserConfigurationException {

		super(dbType, cxn, "indexation_sql.yaml", "core_sql.yaml");

		checkNotNull(usersDir, "usersDir");
		checkNotNull(regionsDir, "regionsDir");
		checkNotNull(poisDir, "poisDir");
		checkNotNull(commentsDir, "commentsDir");

		categoryDirs.put("users", usersDir);
		categoryDirs.put("regions", regionsDir);
		categoryDirs.put("pois", poisDir);
		categoryDirs.put("comments", commentsDir);

		this.searchManager = checkNotNull(searchManager, "searchManager");

		// searchManager = new SearchManagerImpl(dbType, cxn);
	}

	private final SearchManager searchManager;

	private final Map<String, File> categoryDirs = new HashMap<String, File>();

	private File getCategoryDir(final String category) {

		final File categoryDir = categoryDirs.get(category);

		if (categoryDir != null) {
			return categoryDir;
		}

		throw new IllegalArgumentException("Unknown category: " + category);
	}

	@Override
	public void indexData(@Nullable final IndexationObserver observer)
			throws IOException, SQLException, SAXException {

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

		if (!doesTableExist(tablePrefix + "searchtokens")) {
			executeUpdate("createTable_searchtokens");
		}

		if (!doesTableExist(tablePrefix + "search")) {
			executeUpdate("createTable_search");
		}

		if (!doesTableExist(tablePrefix + "history")) {
			executeUpdate("createTable_history");
		}

		if (!doesTableExist(tablePrefix + "sessions")) {
			executeUpdate("createTable_sessions");
		}

		// 1. LOCK ALL

		for (final String category : CATEGORIES) {

			lockCategory(category);
		}

		// 2. CLEAR ALL

		searchManager.flushCache();
		
		final String[] reverseCategories = ArrayUtils.clone(CATEGORIES);

		ArrayUtils.reverse(reverseCategories);

		for (final String category : reverseCategories) {

			clearCategory(category);
		}

		executeUpdate("clearSearch");
		executeUpdate("clearSearchTokens");
		
		executeUpdate("clearHistory");

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

			if (observer != null) {
				observer.notifyCategorySize(category, count);
			}

			// System.out.println("  " + category + ": " + count);
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
					throws SQLException, IOException {

				executeUpdate("createPoi", activeRevfileId, poi.getUid());

				final int entityId = poi.getUid();

				final SearchEntry searchEntry = new SearchEntry("pois",
						entityId);

				searchEntry.addField("name", poi.getName());
				searchEntry.addField("description", poi.getDescription());

				final Poi.Address[] addresses = poi.getAddresses();

				if (addresses.length != 0) {
					final Poi.Address address = addresses[0];

					searchEntry.addField("address", address.getFullAddress());
					searchEntry.addField("floor", address.getFloor());
					searchEntry.addField("openingHours",
							address.getOpeningHours());
					searchEntry.addField("itinerary", address.getItinerary());
				}

				searchManager.inject(searchEntry);
			}
		});

		loadEntities("comments", Comment.class, new EntityLoader<Comment>() {
			@Override
			public void loadEntity(final Comment comment,
					final int activeRevfileId) throws SQLException, IOException {

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

				final int entityId = executeUpdateGetAutoIncrement( //
						"createpkComment", activeRevfileId, comment.getUid(), //
						initialPostedAt, activePostedAt, //
						poiUid);

				final SearchEntry searchEntry = new SearchEntry("comments",
						entityId);

				searchEntry.addField("postedBy", comment.getPostedBy());
				searchEntry.addField("message", comment.getMessage());

				searchManager.inject(searchEntry);
			}
		});

		// 8. UNLOCK ALL

		unlockCategory("users");
		unlockCategory("regions");
		unlockCategory("pois");
		unlockCategory("comments");

		// 9. END

		// return null;
	}

	private <U extends Entry<U>> void loadEntities(final String category,
			final Class<U> clazz, final EntityLoader<U> loader)
			throws SQLException, IOException, SAXException {

		final String sql_getRevfiles = getSql("getRevfiles");
		final String sql_getChildren = getSql("getChildRevfiles");

		final Connection cxn = getConnection();
		try {
			final PreparedStatement pstmt1 = cxn
					.prepareStatement(sql_getRevfiles);
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

							final U entity = loadEntity(new File(categoryDir,
									path), clazz);

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
		} finally {
			cxn.close();
		}
	}

	private void clearCategory(final String category) throws SQLException {

		executeUpdate("clearCategory_1_detachRevfiles", category);

		// This SQL query cannot be stored in sql.yaml, since the table’s name
		// is itself dynamic.
		final String sql = "DELETE FROM " + tablePrefix + "entities_"
				+ category;

		final Connection cxn = getConnection();
		try {
			final Statement stmt = cxn.createStatement();
			try {

				stmt.executeUpdate(sql);

			} finally {
				stmt.close();
			}
		} finally {
			cxn.close();
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

		final Connection cxn = getConnection();
		try {
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
		} finally {
			cxn.close();
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

		final Connection cxn = getConnection();
		try {

			final ResultSet rs = cxn.getMetaData().getTables(NULL_CATALOG,
					NULL_SCHEMA_PATTERN, tablenamePattern, ALL_TYPES);
			try {

				return rs.next() ? true : false; // Keep the long syntax for
													// clarity

			} finally {
				rs.close();
			}
		} finally {
			cxn.close();
		}
	}

	private static final Log log = LogFactory.getLog(IndexationImpl.class);

	private boolean doesCategoryExist(final String category)
			throws SQLException {

		final String sql = getSql("getCategoryPath");

		final Connection cxn = getConnection();
		try {
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
		} finally {
			cxn.close();
		}
	}

	private void createCategoryIfNeeded(final String category)
			throws SQLException, IOException {

		if (doesCategoryExist(category)) {
			return;
		}

		final File categoryDir = // new File(dataDir, category);
		getCategoryDir(category);

		if (!categoryDir.isDirectory()) {
			throw new FileNotFoundException("Dir for category: " + category
					+ " is not a directory: " + categoryDir.getCanonicalPath());
		}

		// setCategoryDir(category, categoryDir);

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

		void loadEntity(U entity, int activeRevfileId) throws SQLException,
				IOException;
	}
}
