package fr.univmobile.backend.core;

import fr.univmobile.commons.datasource.EntryBuilder;


/**
 * Setter methods that cannot exist in the {@link Poi} interface (when data
 * is already stored), only in a builder (when data is composed before being
 * stored.)
 */
public interface PoiBuilder extends EntryBuilder<Poi>, Poi {

	PoiBuilder setUid(int uid);
	
	PoiBuilder setParentUid(int parentUid);

	PoiBuilder setName(String name);
}
