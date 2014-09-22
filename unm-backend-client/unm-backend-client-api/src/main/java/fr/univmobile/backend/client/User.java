package fr.univmobile.backend.client;

import java.io.Serializable;

import javax.annotation.Nullable;

public interface User extends Serializable {

	String getUid();
	
	// String[] getShibbolethRemoteUsers();

	String getDisplayName();

	String getMail();

	@Nullable
	String getImgUrl();

	Field[] getFields();

	/**
	 * public description.
	 */
	@Nullable
	String getDescription();

	/**
	 * private notes.
	 */
	@Nullable
	String getNotes();

	Bookmark[] getBookmarks();

	Comment[] getMostRecentComments();

	interface Field extends Serializable {

		FieldType getType();

		boolean isPublic();

		String getValue();
	}

	enum FieldType {

		PHONE, MOBILE, ADDRESS, BIRTHDAY, STUDENT_ID;
	}

	interface Bookmark extends Serializable {

		BookmarkType getType();

		String getEntityId();
	}

	enum BookmarkType {

		POI, COMMENT, NEWS;
	}
}
