package fr.univmobile.backend.core;

import fr.univmobile.commons.datasource.EntryBuilder;

/**
 * Setter methods that cannot exist in the {@link User} interface (when data
 * is already stored), only in a builder (when data is composed before being
 * stored.)
 */
public interface UserBuilder extends EntryBuilder<User>, User {

	UserBuilder setUid(String uid);
}
