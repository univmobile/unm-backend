package fr.univmobile.commons.datasource;

import fr.univmobile.commons.datasource.EntryBuilder;


/**
 * Setter methods that cannot exist in the {@link MyRegion} interface (when data
 * is already stored), only in a builder (when data is composed before being
 * stored.)
 */
public interface MyRegionBuilder extends EntryBuilder<MyRegion>, MyRegion {

	MyRegionBuilder setUid(String uid);

	MyRegionBuilder setLabel(String label);
}
