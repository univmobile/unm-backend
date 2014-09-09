package fr.univmobile.backend.core.impl;

import java.io.IOException;
import java.sql.Connection;

class AbstractDbManagerImpl extends DbEnabled {

	protected AbstractDbManagerImpl(final ConnectionType dbType,
			final Connection cxn) throws IOException {

		super(dbType, cxn, "core_sql.yaml");
	}
}
