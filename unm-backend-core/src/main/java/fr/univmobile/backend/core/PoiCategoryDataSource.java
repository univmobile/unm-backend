package fr.univmobile.backend.core;

import java.util.List;

import fr.univmobile.commons.datasource.Category;
import fr.univmobile.commons.datasource.PrimaryKey;
import fr.univmobile.commons.datasource.RevDataSource;
import fr.univmobile.commons.datasource.SearchAttribute;
import fr.univmobile.commons.datasource.Support;

@Category("pois")
@PrimaryKey("uid")
@Support(data = PoiCategory.class, builder = PoiCategoryBuilder.class)
public interface PoiCategoryDataSource extends RevDataSource<PoiCategory, PoiCategoryBuilder> {

	@SearchAttribute("uid")
	PoiCategory getByUid(int uid);

	/**
	 * Recupere toutes les sous-categories directes de la categorie dont l'ID est passe en parametre.
	 * @param parentUid
	 * @return
	 */
	@SearchAttribute("parentUid")
	List<PoiCategory> getByParentUid(int parentUid);

	boolean isNullByUid(int uid);

}
