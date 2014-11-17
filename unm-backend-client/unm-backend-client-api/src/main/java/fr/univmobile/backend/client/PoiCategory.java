package fr.univmobile.backend.client;

import javax.annotation.Nullable;

public interface PoiCategory {

	/**
	 * L'identifiant unique de la categorie de POI
	 * e.g. 1
	 */
	int getId();

	/**
	 * Le nom de la categorie
	 * e.g. "Expositions"
	 */
	String getName();

	/**
	 * Description de la categorie. La description peut etre nulle.
	 * @return
	 */
	@Nullable
	String getDescription();
	
	/**
	 * Curseur a utiliser pour l'affichage des POIs de la categorie sur la map
	 * @return
	 */
	@Nullable
	String getCursorUrl();
	
	/**
	 * Recupere la liste des categories enfants
	 * @return
	 */
	PoiCategory[] getChildCategories();

}
