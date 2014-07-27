package fr.univmobile.backend.core.impl;

import fr.univmobile.backend.core.Entry;
import fr.univmobile.backend.core.EntryBuilder;

public interface BackendDataEngine<E extends Entry, EB extends EntryBuilder<E>> {

	void store(E data);

	E getById(String id);
}
