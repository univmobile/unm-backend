package fr.univmobile.backend.core;

import fr.univmobile.commons.datasource.BackendDataSource;
import fr.univmobile.commons.datasource.Category;
import fr.univmobile.commons.datasource.PrimaryKey;
import fr.univmobile.commons.datasource.SearchAttribute;
import fr.univmobile.commons.datasource.Support;

@Category("pois")
@PrimaryKey("uid")
@Support(data = Poi.class, builder = PoiBuilder.class)
public interface PoiDataSource extends
		BackendDataSource<Poi, PoiBuilder> {

	@SearchAttribute("uid")
	Poi getByUid(int uid);

	boolean isNullByUid(int uid);
}
