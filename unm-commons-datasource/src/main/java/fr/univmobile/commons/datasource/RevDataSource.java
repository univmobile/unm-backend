package fr.univmobile.commons.datasource;

import java.io.IOException;

import org.w3c.dom.Document;

public interface RevDataSource<E extends Entry<E>, EB extends EntryBuilder<E>>
		extends DataSource<E> {

	E getById(String id);

	E getParent(E data);

	boolean hasParent(E data);

	E getLatest(E data);

	boolean isLatest(E data);

	EB create();

	// Author: Mauricio (begin)
	
	Document getDocument();
	
	EB createBuilder(Document document);
	
	// Author: Mauricio (end)

	EB update(E data);

	E reload(E data) throws IOException;

	void reload() throws IOException;
}
