package fr.univmobile.commons.datasource;

import fr.univmobile.commons.datasource.EntryBuilder;


/**
 * Setter methods that cannot exist in the {@link MyPoi} interface (when data
 * is already stored), only in a builder (when data is composed before being
 * stored.)
 */
public interface MyPoiBuilder extends EntryBuilder<MyPoi>, MyPoi{

	MyPoiBuilder setUid(int uid);

	MyPoiBuilder setName(String name);
}
