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

import fr.univmobile.backend.core.impl.ConnectionType;
import fr.univmobile.backend.core.impl.DbEnabled;
import fr.univmobile.commons.datasource.Entry;

/**
 * Common code for the command-line tools.
 */
abstract class AbstractTool extends DbEnabled {

	protected AbstractTool(final ConnectionType dbType, final Connection cxn)
			throws IOException, ParserConfigurationException {

		super(dbType, cxn, "sysadmin_sql.yaml");

		final DocumentBuilderFactory documentBuilderFactory = DocumentBuilderFactory
				.newInstance();

		documentBuilderFactory.setNamespaceAware(true);
		documentBuilderFactory.setValidating(false);

		documentBuilder = documentBuilderFactory.newDocumentBuilder();
	}

	private final DomBinder domBinder = DefaultDomBinder.getInstance();
	private final DocumentBuilder documentBuilder;

	public static final String[] CATEGORIES = new String[] { "users",
			"regions", "pois", "comments" };

	private static final Log log = LogFactory.getLog(AbstractTool.class);

	@Nullable
	public abstract Result run() throws IOException, SQLException, SAXException;

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
