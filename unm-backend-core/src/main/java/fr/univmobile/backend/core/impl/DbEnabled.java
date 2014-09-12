package fr.univmobile.backend.core.impl;

import static com.google.common.base.Preconditions.checkNotNull;
import static org.apache.commons.lang3.CharEncoding.UTF_8;
import static org.apache.commons.lang3.StringUtils.isBlank;
import static org.apache.commons.lang3.StringUtils.join;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.util.HashMap;
import java.util.Map;

import javax.annotation.Nullable;
import javax.sql.DataSource;

import net.avcompris.binding.yaml.impl.DomYamlBinder;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.joda.time.DateTime;
import org.jvyaml.YAML;

public abstract class DbEnabled {

	public static final String[] CATEGORIES = new String[] { "users",
			"regions", "pois", "comments" };

	/**
	 * @param resourceName
	 *            the name of the "xxx_sql.yaml" resource to load into memory,
	 *            that holds all the SQL queries we’ll be using.
	 */
	protected DbEnabled(final ConnectionType dbType, final Connection cxn,
			final String... resourceNames) throws IOException {

		this.dbType = checkNotNull(dbType, "dbType");

		checkNotNull(cxn, "cxn");

		this.alwaysOpenedConnection = newAlwaysOpenedConnection(cxn);
		this.ds = null;

		sqlBundle = loadSqlBundle(dbType, resourceNames);
	}

	protected DbEnabled(final ConnectionType dbType, final DataSource ds,
			final String... resourceNames) throws IOException {

		this.dbType = checkNotNull(dbType, "dbType");

		this.ds = checkNotNull(ds, "dataSource");
		this.alwaysOpenedConnection = null;

		sqlBundle = loadSqlBundle(dbType, resourceNames);
	}

	private static SqlBundle loadSqlBundle(final ConnectionType dbType,
			final String... resourceNames) throws IOException {

		if (resourceNames.length == 1) {

			return loadSqlBundle(dbType, resourceNames[0]);
		}

		final SqlBundleComposite sqlBundle = new SqlBundleComposite();

		for (final String resourceName : resourceNames) {

			sqlBundle.add(loadSqlBundle(dbType, resourceName));
		}

		return sqlBundle;
	}

	private static SqlBundle loadSqlBundle(final ConnectionType dbType,
			final String resourceName) throws IOException {

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

		final SqlBundle sqlBundle = new DomYamlBinder().bind(yaml,
				SqlBundle.class);

		if (sqlBundle.isNullQueriesByDbType(dbType.toString())) {
			throw new RuntimeException("Cannot find SQL queries for dbType: "
					+ dbType);
		}

		return sqlBundle;
	}

	private final SqlBundle sqlBundle;
	protected final ConnectionType dbType;

	@Nullable
	private final DataSource ds;

	@Nullable
	private final Connection alwaysOpenedConnection;

	protected static final String tablePrefix = "unm_";

	protected final Connection getConnection() throws SQLException {

		if (alwaysOpenedConnection != null) {
			return alwaysOpenedConnection;
		}

		if (ds != null) {
			return ds.getConnection();
		}

		throw new IllegalStateException("cxn == null && ds == null");
	}

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

	/**
	 * Set an int array in place of a "?" question mark within an existing SQL
	 * query.
	 * 
	 * @return the new SQL query.
	 */
	static final String setIntArray(final String sql, final int index,
			final int... array) {

		int offset = 0;

		for (int count = 0;; ++offset) {

			offset = sql.indexOf('?', offset);

			if (offset == -1) {
				throw new ArrayIndexOutOfBoundsException("index: " + index
						+ ", sql: " + sql);
			}

			++count;

			if (count >= index) {
				break;
			}
		}

		final StringBuilder sb = new StringBuilder(sql.substring(0, offset));

		sb.append(join(array, ','));

		return sb.append(sql.substring(offset + 1)).toString();
	}

	protected final int executeUpdate(final String queryId,
			final Object... params) throws SQLException {

		final String sql = getSql(queryId);

		final Connection cxn = getConnection();
		try {
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
		} finally {
			cxn.close();
		}
	}

	protected final int executeUpdateGetAutoIncrement(final String queryId,
			final Object... params) throws SQLException {

		final String sql = getSql(queryId);

		final Connection cxn = getConnection();
		try {
			final PreparedStatement pstmt = queryId.startsWith("createpk") ? cxn
					.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)
					: cxn.prepareStatement(sql);
			try {

				setSqlParams(pstmt, params);

				try {

					pstmt.executeUpdate();

					final ResultSet rs = pstmt.getGeneratedKeys();
					try {

						rs.next();

						return rs.getInt(1);

					} finally {
						rs.close();
					}

				} catch (final SQLException e) {

					errorLogQuery(queryId, sql, params);

					throw e;
				}

			} finally {
				pstmt.close();
			}
		} finally {
			cxn.close();
		}
	}

	protected final String executeQueryGetString(final String queryId,
			final Object... params) throws SQLException {

		final String sql = getSql(queryId);

		final Connection cxn = getConnection();
		try {
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
		} finally {
			cxn.close();
		}
	}

	protected final int executeQueryGetInt(final String queryId,
			final Object... params) throws SQLException {

		final String sql = getSql(queryId);

		final Connection cxn = getConnection();
		try {
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
		} finally {
			cxn.close();
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

	private static Connection newAlwaysOpenedConnection(final Connection cxn) {

		final Object proxy = Proxy.newProxyInstance(
				DbEnabled.class.getClassLoader(),
				new Class<?>[] { Connection.class }, new InvocationHandler() {

					@Override
					public Object invoke(final Object proxy,
							final Method method, final Object[] args)
							throws Throwable {

						if ("close".equals(method.getName())) {

							return null; // do nothing
						}

						try {

							return method.invoke(cxn, args); // delegation

						} catch (final InvocationTargetException e) {

							throw e.getTargetException();
						}
					}
				});

		return (Connection) proxy;
	}
}
