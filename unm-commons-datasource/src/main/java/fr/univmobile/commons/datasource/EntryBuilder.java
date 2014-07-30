package fr.univmobile.commons.datasource;

import net.avcompris.binding.annotation.XPath;

public interface EntryBuilder<T extends Entry> extends Entry {

	boolean isNullId();

	EntryBuilder<T> setId(String id);

	@XPath("atom:link[@rel = 'self']/@href")
	String getSelf();

	boolean isNullSelf();

	EntryBuilder<T> setSelf(String id);

	EntryBuilder<T> setParentId(String parentId);

	EntryBuilder<T> setAuthorName(String authorName);

	EntryBuilder<T> setTitle(String title);

	T save();

	// void dump(File file) throws IOException;
}