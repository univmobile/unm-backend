package fr.univmobile.backend.core;

import net.avcompris.binding.annotation.XPath;

public interface EntryBuilder<T extends Entry> extends Entry {

	@XPath("atom:author/atom:name")
	EntryBuilder<T> setAuthorName(String authorName);

	EntryBuilder<T> setTitle(String title);

	T save();

	// void dump(File file) throws IOException;
}
