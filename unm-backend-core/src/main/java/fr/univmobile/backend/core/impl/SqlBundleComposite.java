package fr.univmobile.backend.core.impl;

import static fr.univmobile.backend.core.impl.ConnectionType.MYSQL;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import com.avcompris.lang.NotImplementedException;

class SqlBundleComposite implements SqlBundle {

	public void add(final SqlBundle sqlBundle) {

		for (final String queryId : sqlBundle.getQueriesByDbType(
				MYSQL.toString()).getQueryIds()) {

			if (queryIds.contains(queryId)) {
				throw new IllegalArgumentException("Duplicate queryId: "
						+ queryId);
			}

			queryIds.add(queryId);
		}

		sqlBundles.add(sqlBundle);
	}

	private final Set<String> queryIds = new HashSet<String>();

	private final List<SqlBundle> sqlBundles = new ArrayList<SqlBundle>();

	@Override
	public SqlQueries getQueriesByDbType(final String dbType) {

		throw new NotImplementedException();
	}

	@Override
	public boolean isNullQueriesByDbType(final String dbType) {

		throw new NotImplementedException();
	}

	@Override
	public int sizeOfQueriesByDbType(final String dbType) {

		throw new NotImplementedException();
	}

	@Override
	public boolean isNullQuery(final String queryId, final String dbType) {

		for (final SqlBundle sqlBundle : sqlBundles) {

			if (!sqlBundle.isNullQuery(queryId, dbType)) {
				return false;
			}
		}

		return true;
	}

	@Override
	public String getQuery(final String queryId, final String dbType) {

		for (final SqlBundle sqlBundle : sqlBundles) {

			if (sqlBundle.isNullQuery(queryId, dbType)) {
				continue;
			}

			return sqlBundle.getQuery(queryId, dbType);
		}

		throw new RuntimeException("Unknown queryId: " + queryId
				+ " for dbType:" + dbType);
	}
}
