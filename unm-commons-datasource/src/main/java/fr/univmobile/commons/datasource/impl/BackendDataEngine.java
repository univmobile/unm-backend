package fr.univmobile.commons.datasource.impl;

import fr.univmobile.commons.datasource.Entry;
import fr.univmobile.commons.datasource.EntryBuilder;

public interface BackendDataEngine<E extends Entry, EB extends EntryBuilder<E>> {

	void store(E data);

	E getById(String id);
}
