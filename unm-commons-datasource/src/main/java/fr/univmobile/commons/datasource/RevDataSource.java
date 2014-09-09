package fr.univmobile.commons.datasource;

import java.io.IOException;

public interface RevDataSource<E extends Entry<E>, EB extends EntryBuilder<E>>
		extends DataSource<E> {

	E getById(String id);

	E getParent(E data);

	boolean hasParent(E data);

	E getLatest(E data);

	boolean isLatest(E data);

	EB create();

	EB update(E data);

	E reload(E data) throws IOException;

	void reload() throws IOException;
}
