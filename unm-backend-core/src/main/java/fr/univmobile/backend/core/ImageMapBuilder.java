package fr.univmobile.backend.core;

import fr.univmobile.commons.datasource.EntryBuilder;

public interface ImageMapBuilder extends EntryBuilder<ImageMap>, ImageMap {

	ImageMapBuilder setUid(int uid);

	ImageMapBuilder setName(String name);

	// Author: Mauricio (begin)

	ImageMapBuilder setActive(String active);

	ImageMapBuilder setImageUrl(String url);

	ImageMapBuilder setDescription(String description);

	ImageMapBuilder setPoiInfos(String poisInfos);

	// Author: Mauricio (end)
}
