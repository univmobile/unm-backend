package fr.univmobile.backend.sysadmin;

import static com.google.common.base.Preconditions.checkNotNull;
import static org.apache.commons.lang3.CharEncoding.UTF_8;
import static org.apache.commons.lang3.StringUtils.isBlank;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.util.HashMap;
import java.util.Map;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import net.avcompris.binding.dom.DomBinder;
import net.avcompris.binding.dom.impl.DefaultDomBinder;
import net.avcompris.binding.yaml.impl.DomYamlBinder;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.joda.time.DateTime;
import org.jvyaml.YAML;
import org.w3c.dom.Document;
import org.xml.sax.SAXException;

import fr.univmobile.backend.core.Comment;
import fr.univmobile.backend.core.Poi;
import fr.univmobile.backend.core.Region;
import fr.univmobile.backend.core.User;
import fr.univmobile.commons.datasource.Entry;

/**
 * Code for the "index" command-line tool.
 */
class Indexation extends Tool {

	public Indexation(final File dataDir, final ConnectionType dbType,
			final Connection cxn) throws IOException,
			ParserConfigurationException {

		this.dataDir = checkNotNull(dataDir, "dataDir");
		this.dbType = checkNotNull(dbType, "dbType").toString();
		this.cxn = checkNotNull(cxn, "cxn");

		final Object yaml;

		final InputStream is = Indexation.class.getClassLoader()
				.getResourceAsStream("sql.yaml");
		try {

			final Reader reader = new InputStreamReader(is, UTF_8);

			yaml = YAML.load(reader);

		} finally {
			is.close();
		}

		sqlBundle = new DomYamlBinder().bind(yaml, SqlBundle.class);

		if (sqlBundle.isNullQueriesByDbType(this.dbType)) {
			throw new RuntimeException("Cannot find SQL queries for dbType: "
					+ dbType);
		}

		final DocumentBuilderFactory documentBuilderFactory = DocumentBuilderFactory
				.newInstance();

		documentBuilderFactory.setNamespaceAware(true);
		documentBuilderFactory.setValidating(false);

		documentBuilder = documentBuilderFactory.newDocumentBuilder();
	}

	private final DomBinder domBinder = DefaultDomBinder.getInstance();
	private final File dataDir;
	private final String dbType;
	private final SqlBundle sqlBundle;
	private final Connection cxn;
	private static final String tablePrefix = "unm_";

	public static final String[] CATEGORIES = new String[] { "users",
			"regions", "pois", "comments" };

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

		for (final String category : CATEGORIES) {

			final File categoryDir = new File(getCategoryPath(category));

			for (final File file : categoryDir.listFiles()) {

				loadRevfile(category, file.getName());
			}
		}

		// 4. REVFILE PARENTS

		for (final String category : CATEGORIES) {

			final File categoryDir = new File(getCategoryPath(category));

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

				executeUpdate("createComment", activeRevfileId,
						comment.getUid());
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

						final File file = new File(categoryDir, path);

						final Document document;

						final InputStream is = new FileInputStream(file);
						try {

							document = documentBuilder.parse(is);

						} finally {
							is.close();
						}

						final U entity = domBinder.bind(document, clazz);

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

	private final Map<String, File> categoryDirs = new HashMap<String, File>();

	private File getCategoryDir(final String category) {

		final File categoryDir = categoryDirs.get(category);

		if (categoryDir == null) {
			throw new IllegalStateException(
					"Path has not been initialized for category: " + category);
		}

		return categoryDir;
	}

	private final DocumentBuilder documentBuilder;

	private void loadRevfile(final String category, final String path)
			throws SQLException, IOException, SAXException {

		final DateTime now = new DateTime();

		final File categoryDir = getCategoryDir(category);

		final File file = new File(categoryDir, path);

		if (!file.isFile()) {
			throw new FileNotFoundException("Not a file: "
					+ file.getCanonicalPath());
		}

		final Document document;

		final InputStream is = new FileInputStream(file);
		try {

			document = documentBuilder.parse(is);

		} finally {
			is.close();
		}

		final Entry<?> entry = domBinder.bind(document, Entry.class);

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

		final Document document;

		final InputStream is = new FileInputStream(file);
		try {

			document = documentBuilder.parse(is);

		} finally {
			is.close();
		}

		final Entry<?> entry = domBinder.bind(document, Entry.class);

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

		final ResultSet rs = cxn.getMetaData().getTables(NULL_CATALOG,
				NULL_SCHEMA_PATTERN, tablename, ALL_TYPES);
		try {

			return rs.next() ? true : false; // Keep the long syntax for clarity

		} finally {
			rs.close();
		}
	}

	private int executeUpdate(final String queryId, final Object... params)
			throws SQLException {

		final String sql = getSql(queryId);

		final PreparedStatement pstmt = cxn.prepareStatement(sql);
		try {

			setSqlParams(pstmt, params);

			try {

				return pstmt.executeUpdate();

			} catch (final SQLException e) {

				errorLogQuery(queryId, sql, params);

				throw e;
			}

		} finally {
			pstmt.close();
		}
	}

	private static void setSqlParams(final PreparedStatement pstmt,
			final Object... params) throws SQLException {

		for (int i = 0; i < params.length; ++i) {

			final int index = i + 1;

			final Object param = params[i];

			if (param instanceof String) {

				pstmt.setString(index, (String) param);

			} else if (param instanceof Integer) {

				pstmt.setInt(index, (Integer) param);

			} else if (param instanceof DateTime) {

				pstmt.setTimestamp(index, new Timestamp(((DateTime) param)
						.toDate().getTime()));

			} else {

				throw new IllegalArgumentException("Param #" + index
						+ " should be String, Integer or DateTime: " + param);
			}
		}
	}

	private static final Log log = LogFactory.getLog(Indexation.class);

	private final Map<String, String> sqlQueries = new HashMap<String, String>();

	private String getSql(final String queryId) {

		if (log.isDebugEnabled()) {
			log.debug("getSql():" + queryId);
		}

		final String cachedSql = sqlQueries.get(queryId);

		if (cachedSql != null) {

			if (log.isDebugEnabled()) {
				log.debug("SQL (cached): " + cachedSql);
			}

			return cachedSql;
		}

		final String sql = sqlBundle.getQuery(queryId, dbType);

		if (isBlank(sql)) {
			throw new RuntimeException("Cannot find SQL query for id: "
					+ queryId + " (dbType: " + dbType + ")");
		}

		final String filteredSql = sql.replace("${prefix}", tablePrefix);

		if (log.isDebugEnabled()) {
			log.debug("SQL (new): " + filteredSql);
		}

		return filteredSql;
	}

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

	private String getCategoryPath(final String category) throws SQLException {

		return executeQueryGetString("getCategoryPath", category);
	}

	private String executeQueryGetString(final String queryId,
			final Object... params) throws SQLException {

		final String sql = getSql(queryId);

		final PreparedStatement pstmt = cxn.prepareStatement(sql);
		try {

			setSqlParams(pstmt, params);

			final ResultSet rs;
			try {

				rs = pstmt.executeQuery();

			} catch (final SQLException e) {

				errorLogQuery(queryId, sql, params);

				throw e;
			}
			try {

				if (!rs.next()) {

					log.fatal("Query did not return any result: " + queryId);

					errorLogQuery(queryId, sql, params);

					throw new RuntimeException("Cannot execute query: "
							+ queryId);
				}

				return rs.getString(1);

			} finally {
				rs.close();
			}

		} finally {
			pstmt.close();
		}
	}

	private int executeQueryGetInt(final String queryId, final Object... params)
			throws SQLException {

		final String sql = getSql(queryId);

		final PreparedStatement pstmt = cxn.prepareStatement(sql);
		try {

			setSqlParams(pstmt, params);

			final ResultSet rs;
			try {

				rs = pstmt.executeQuery();

			} catch (final SQLException e) {

				errorLogQuery(queryId, sql, params);

				throw e;
			}
			try {

				if (!rs.next()) {

					log.fatal("Query did not return any result: " + queryId);

					errorLogQuery(queryId, sql, params);

					throw new RuntimeException("Cannot execute query: "
							+ queryId);
				}

				return rs.getInt(1);

			} finally {
				rs.close();
			}

		} finally {
			pstmt.close();
		}
	}

	private static void errorLogQuery(final String queryId, final String sql,
			final Object... params) {

		log.fatal("Query in error: " + queryId);

		for (int i = 0; i < params.length; ++i) {
			log.fatal("  Param #" + (i + 1) + ": " + params[i]);
		}

		log.fatal("SQL: " + sql);
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

		categoryDirs.put(category, categoryDir);

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
