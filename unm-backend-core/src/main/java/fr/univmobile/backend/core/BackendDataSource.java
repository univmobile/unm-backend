package fr.univmobile.backend.core;

import java.util.Map;

public interface BackendDataSource<T extends Entry> {

	T getById(String id);

	T getParent(T data);

	boolean hasParent(T data);

	T getLatest(T data);

	boolean isLatest(T data);
	
	public Map<String,T> getAllBy(String attributeName);
}
