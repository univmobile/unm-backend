package fr.univmobile.commons.datasource.impl;

import java.io.IOException;

import fr.univmobile.commons.datasource.Entry;
import fr.univmobile.commons.datasource.EntryBuilder;

interface EntryBuilderImpl<E extends Entry<E>> extends EntryBuilder<E> {

	/**
	 * save the entry to the FileSystem.
	 */
	E save() throws IOException;
	
	/**
	 * save the entry to the in-memory ID list.
	 */
	void cache();
}
