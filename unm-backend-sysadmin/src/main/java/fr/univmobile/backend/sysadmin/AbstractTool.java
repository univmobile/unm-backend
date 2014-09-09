package fr.univmobile.backend.sysadmin;

import static com.google.common.base.Preconditions.checkNotNull;
import static org.apache.commons.lang3.CharEncoding.UTF_8;
import static org.apache.commons.lang3.StringUtils.isBlank;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.HashMap;
import java.util.Map;

import javax.annotation.Nullable;
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

import fr.univmobile.commons.datasource.Entry;

/**
 * Common code for the command-line tools.
 */
abstract class AbstractTool {

	protected AbstractTool(final ConnectionType dbType, final Connection cxn)
			throws IOException, ParserConfigurationException {

		this.dbType = checkNotNull(dbType, "dbType");
		this.cxn = checkNotNull(cxn, "cxn");

		final Object yaml;

		final InputStream is = IndexationTool.class.getClassLoader()
				.getResourceAsStream("sql.yaml");
		try {

			final Reader reader = new InputStreamReader(is, UTF_8);

			yaml = YAML.load(reader);

		} finally {
			is.close();
		}

		sqlBundle = new DomYamlBinder().bind(yaml, SqlBundle.class);

		if (sqlBundle.isNullQueriesByDbType(this.dbType.toString())) {
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
	protected final ConnectionType dbType;
	private final SqlBundle sqlBundle;
	protected final Connection cxn;
	protected static final String tablePrefix = "unm_";
	private final DocumentBuilder documentBuilder;

	public static final String[] CATEGORIES = new String[] { "users",
			"regions", "pois", "comments" };

	private static final Log log = LogFactory.getLog(AbstractTool.class);

	@Nullable
	public abstract Result run() throws IOException, SQLException, SAXException;

	private final Map<String, String> sqlQueries = new HashMap<String, String>();

	protected final String getSql(final String queryId) {

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

		final String sql = sqlBundle.getQuery(queryId, dbType.toString());

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

	protected final int executeUpdate(final String queryId,
			final Object... params) throws SQLException {

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

	protected static void errorLogQuery(final String queryId, final String sql,
			final Object... params) {

		log.fatal("Query in error: " + queryId);

		for (int i = 0; i < params.length; ++i) {
			log.fatal("  Param #" + (i + 1) + ": " + params[i]);
		}

		log.fatal("SQL: " + sql);
	}

	protected static void setSqlParams(final PreparedStatement pstmt,
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

	protected final File getCategoryDir(final String category)
			throws SQLException {

		File categoryDir = categoryDirs.get(category);

		if (categoryDir != null) {
			return categoryDir;
		}

		final String categoryPath = executeQueryGetString("getCategoryPath",
				category);

		categoryDir = new File(categoryPath);

		categoryDirs.put(category, categoryDir);

		return categoryDir;
	}

	protected final void setCategoryDir(final String category,
			final File categoryDir) {

		categoryDirs.put(category, categoryDir);
	}

	private final Map<String, File> categoryDirs = new HashMap<String, File>();

	protected final String executeQueryGetString(final String queryId,
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

	protected final int executeQueryGetInt(final String queryId,
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

				return rs.getInt(1);

			} finally {
				rs.close();
			}

		} finally {
			pstmt.close();
		}
	}

	private final Document loadDocument(final File xmlFile) throws IOException,
			SAXException {

		final InputStream is = new FileInputStream(xmlFile);
		try {

			return documentBuilder.parse(is);

		} finally {
			is.close();
		}
	}

	protected final <U extends Entry<U>> U loadEntity(final File xmlFile,
			final Class<U> clazz) throws IOException, SAXException {

		return domBinder.bind(loadDocument(xmlFile), clazz);
	}

	protected final Entry<?> loadEntity(final File xmlFile) throws IOException,
			SAXException {

		return domBinder.bind(loadDocument(xmlFile), Entry.class);
	}
}
