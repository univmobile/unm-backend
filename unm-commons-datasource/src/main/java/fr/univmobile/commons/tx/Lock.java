package fr.univmobile.commons.tx;

import java.io.IOException;

import fr.univmobile.commons.datasource.Entry;
import fr.univmobile.commons.datasource.EntryBuilder;

public interface Lock {

	/**
	 * Update all links to resources tagged by the corresponding "save()"
	 * methods.
	 */
	void commit() throws IOException;

	void release() throws IOException;

	<E extends Entry<E>> E save(EntryBuilder<E> entryBuilder) throws IOException;
}
