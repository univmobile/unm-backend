package fr.univmobile.commons.datasource;

import fr.univmobile.commons.datasource.EntryBuilder;

/**
 * Setter methods that cannot exist in the {@link MyUser} interface (when data
 * is already stored), only in a builder (when data is composed before being
 * stored.)
 */
public interface MyUserBuilder extends EntryBuilder<MyUser>, MyUser {

	MyUserBuilder setUid(String uid);
}
