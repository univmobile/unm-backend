package fr.univmobile.backend.core;

import fr.univmobile.commons.datasource.BackendDataSource;
import fr.univmobile.commons.datasource.Category;
import fr.univmobile.commons.datasource.PrimaryKey;
import fr.univmobile.commons.datasource.SearchAttribute;
import fr.univmobile.commons.datasource.Support;

@Category("poitrees")
@PrimaryKey("uid")
// @Singleton
@Support(data = PoiTree.class, builder = PoiTreeBuilder.class)
public interface PoiTreeDataSource extends
		BackendDataSource<PoiTree, PoiTreeBuilder> {

	@SearchAttribute("uid")
	PoiTree getByUid(String uid);

	boolean isNullByUid(String uid);
}
