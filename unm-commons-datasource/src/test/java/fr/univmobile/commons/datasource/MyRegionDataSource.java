package fr.univmobile.commons.datasource;

import fr.univmobile.commons.datasource.BackendDataSource;
import fr.univmobile.commons.datasource.Category;
import fr.univmobile.commons.datasource.PrimaryKey;
import fr.univmobile.commons.datasource.SearchAttribute;
import fr.univmobile.commons.datasource.Support;

@Category("regions")
@PrimaryKey("uid")
@Support(data = MyRegion.class, builder = MyRegionBuilder.class)
public interface MyRegionDataSource extends
		BackendDataSource<MyRegion, MyRegionBuilder> {

	@SearchAttribute("uid")
	MyRegion getByUid(String uid);

	boolean isNullByUid(String uid);
}
