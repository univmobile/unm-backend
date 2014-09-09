package fr.univmobile.backend.core.impl;

import static com.google.common.base.Preconditions.checkNotNull;

import java.sql.Connection;

public abstract class AbstractDbDataSource {

	protected AbstractDbDataSource(final ConnectionType dbType,
			final Connection cxn) {
		
		this.dbType=checkNotNull(dbType,"dbType");
		this.cxn=checkNotNull(cxn,"cxn");
	}
	
	private final ConnectionType dbType;
	private final Connection cxn;
}
