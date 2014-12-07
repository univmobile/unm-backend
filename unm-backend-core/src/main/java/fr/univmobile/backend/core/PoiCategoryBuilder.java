package fr.univmobile.backend.core;

import fr.univmobile.commons.datasource.EntryBuilder;

/**
 * Setter methods that cannot exist in the {@link PoiCategory} interface (when data
 * is already stored), only in a builder (when data is composed before being
 * stored.)
 */
public interface PoiCategoryBuilder extends EntryBuilder<PoiCategory>, PoiCategory {

	PoiCategoryBuilder setUid(int uid);
	
	PoiCategoryBuilder setParentUid(int parentUid);

	PoiCategoryBuilder setName(String name);

	// Author: Mauricio
	PoiCategoryBuilder setDescription(String description);
	PoiCategoryBuilder setExternalUid(int externalUid);
	PoiCategoryBuilder setActive(boolean active);
	
}
