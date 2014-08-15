package fr.univmobile.backend.core;

import fr.univmobile.commons.datasource.EntryBuilder;


/**
 * Setter methods that cannot exist in the {@link PoiTree} interface (when data
 * is already stored), only in a builder (when data is composed before being
 * stored.)
 */
public interface PoiTreeBuilder extends EntryBuilder<PoiTree>, PoiTree {

	PoiTreeBuilder setUid(int uid);

	PoiTreeBuilder setName(String name);
}
