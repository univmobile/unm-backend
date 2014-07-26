package fr.univmobile.backend.core.impl;

import fr.univmobile.backend.core.Entry;

public interface BackendDataEngine<T extends Entry> {

	void store(T data);
	
	T getById(String id);
}
