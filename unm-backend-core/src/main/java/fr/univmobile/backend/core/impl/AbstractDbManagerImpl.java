package fr.univmobile.backend.core.impl;

import java.io.IOException;
import java.sql.Connection;

import javax.sql.DataSource;

class AbstractDbManagerImpl extends DbEnabled {

	private static final String QUERIES = "core_sql.yaml";

	protected AbstractDbManagerImpl(final ConnectionType dbType,
			final Connection cxn) throws IOException {

		super(dbType, cxn, QUERIES);
	}

	protected AbstractDbManagerImpl(final ConnectionType dbType,
			final DataSource ds) throws IOException {

		super(dbType, ds, QUERIES);
	}
}
