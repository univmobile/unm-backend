package fr.univmobile.backend.core;

import fr.univmobile.commons.datasource.Category;
import fr.univmobile.commons.datasource.PrimaryKey;
import fr.univmobile.commons.datasource.RevDataSource;
import fr.univmobile.commons.datasource.SearchAttribute;
import fr.univmobile.commons.datasource.Support;

@Category("imagemaps")
@PrimaryKey("uid")
@Support(data = ImageMap.class, builder = ImageMapBuilder.class)
public interface ImageMapDataSource extends RevDataSource<ImageMap, ImageMapBuilder> {

	@SearchAttribute("uid")
	ImageMap getByUid(int uid);

	boolean isNullByUid(int uid);

}
