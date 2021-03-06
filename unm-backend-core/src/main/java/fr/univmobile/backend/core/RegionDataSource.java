package fr.univmobile.backend.core;

import fr.univmobile.commons.datasource.RevDataSource;
import fr.univmobile.commons.datasource.Category;
import fr.univmobile.commons.datasource.PrimaryKey;
import fr.univmobile.commons.datasource.SearchAttribute;
import fr.univmobile.commons.datasource.Support;

@Category("regions")
@PrimaryKey("uid")
@Support(data = Region.class, builder = RegionBuilder.class)
public interface RegionDataSource extends
		RevDataSource<Region, RegionBuilder> {

	@SearchAttribute("uid")
	Region getByUid(String uid);

	boolean isNullByUid(String uid);
}
