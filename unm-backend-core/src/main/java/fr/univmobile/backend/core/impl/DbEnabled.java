package fr.univmobile.backend.core.impl;

import static com.google.common.base.Preconditions.checkNotNull;
import static org.apache.commons.lang3.CharEncoding.UTF_8;
import static org.apache.commons.lang3.StringUtils.isBlank;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.HashMap;
import java.util.Map;

import net.avcompris.binding.yaml.impl.DomYamlBinder;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.joda.time.DateTime;
import org.jvyaml.YAML;

public abstract class DbEnabled {

	/**
	 * @param resourceName
	 *            the name of the "xxx_sql.yaml" resource to load into memory,
	 *            that holds all the SQL queries we’ll be using.
	 */
	protected DbEnabled(final ConnectionType dbType, final Connection cxn,
			final String resourceName) throws IOException {

		this.dbType = checkNotNull(dbType, "dbType");
		this.cxn = checkNotNull(cxn, "cxn");

		final Object yaml;

		final InputStream is = DbEnabled.class.getClassLoader()
				.getResourceAsStream(resourceName);

		if (is == null) {
			throw new FileNotFoundException("Unable to load resource: "
					+ resourceName);
		}

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
	}

	private final SqlBundle sqlBundle;
	protected final ConnectionType dbType;
	protected final Connection cxn;
	protected static final String tablePrefix = "unm_";

	private static final Log log = LogFactory.getLog(DbEnabled.class);

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
}
