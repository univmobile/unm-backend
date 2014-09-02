package fr.univmobile.commons.datasource;

import fr.univmobile.commons.datasource.BackendDataSource;
import fr.univmobile.commons.datasource.Category;
import fr.univmobile.commons.datasource.PrimaryKey;
import fr.univmobile.commons.datasource.SearchAttribute;
import fr.univmobile.commons.datasource.Support;

@Category("pois")
@PrimaryKey("uid")
@Support(data = MyPoi.class, builder = MyPoiBuilder.class)
public interface MyPoiDataSource extends
		BackendDataSource<MyPoi, MyPoiBuilder> {

	@SearchAttribute("uid")
	MyPoi getByUid(int uid);

	boolean isNullByUid(int uid);
}
