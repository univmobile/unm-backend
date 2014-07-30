package fr.univmobile.backend.core;

import fr.univmobile.commons.datasource.EntryBuilder;


/**
 * Setter methods that cannot exist in the {@link Region} interface (when data
 * is already stored), only in a builder (when data is composed before being
 * stored.)
 */
public interface RegionBuilder extends EntryBuilder<Region>, Region {

	RegionBuilder setUid(String uid);

	RegionBuilder setLabel(String label);
}
