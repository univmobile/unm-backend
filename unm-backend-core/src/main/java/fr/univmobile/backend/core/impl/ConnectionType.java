package fr.univmobile.backend.core.impl;

/**
 * Different SQL connection types: MySQL, H2…
 */
public enum ConnectionType {

	MYSQL("mysql"), H2("h2");

	private ConnectionType(final String label) {

		this.label = label;
	}
	
	private final String label;

	@Override
	public String toString() {

		return label;
	}
}
