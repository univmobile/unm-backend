package fr.univmobile.commons.tx;

import fr.univmobile.commons.datasource.Entry;
import fr.univmobile.commons.datasource.EntryBuilder;

public interface Lock {

	/**
	 * Update all links to resources tagged by the corresponding "save()"
	 * methods.
	 */
	void commit() throws TransactionException;

	void release() throws TransactionException;

	<E extends Entry<E>> E save(EntryBuilder<E> entryBuilder) throws TransactionException;
}
