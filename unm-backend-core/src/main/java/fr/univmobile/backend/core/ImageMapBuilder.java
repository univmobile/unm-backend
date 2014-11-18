package fr.univmobile.backend.core;

import fr.univmobile.commons.datasource.EntryBuilder;

public interface ImageMapBuilder extends EntryBuilder<ImageMap>, ImageMap {

	ImageMapBuilder setUid(int uid);
	
	ImageMapBuilder setName(String name);

}
