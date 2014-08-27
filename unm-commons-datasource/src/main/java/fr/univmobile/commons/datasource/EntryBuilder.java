package fr.univmobile.commons.datasource;

import net.avcompris.binding.annotation.XPath;

public interface EntryBuilder<E extends Entry<E>> extends Entry<E> {

	boolean isNullId();

	EntryBuilder<E> setId(String id);

	@XPath("atom:link[@rel = 'self']/@href")
	String getSelf();

	boolean isNullSelf();

	EntryBuilder<E> setSelf(String id);

	EntryBuilder<E> setParentId(String parentId);

	EntryBuilder<E> setAuthorName(String authorName);

	EntryBuilder<E> setTitle(String title);

	// E save() throws IOException;

	// void dump(File file) throws IOException;
}
