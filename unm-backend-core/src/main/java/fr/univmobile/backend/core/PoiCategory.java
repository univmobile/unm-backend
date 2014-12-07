package fr.univmobile.backend.core;

import javax.annotation.Nullable;

import net.avcompris.binding.annotation.XPath;
import fr.univmobile.commons.datasource.Entry;

/**
 * Contient toutes les informations d'une categorie de POI. Les annotations
 * definissent le mapping avec les noeuds de la structure du fichier XML.
 *
 */
public interface PoiCategory extends Entry<PoiCategory> {

	/**
	 * L'identifiant unique de la categorie de POI e.g. 1
	 */
	@XPath("atom:content/@uid")
	int getUid();

	/**
	 * Le nom de la categorie e.g. "Expositions"
	 */
	@XPath("atom:content/@name")
	String getName();

	/**
	 * Description de la categorie. La description peut etre nulle.
	 * 
	 * @return
	 */
	@XPath("atom:content/description")
	@Nullable
	String getDescription();

	/**
	 * Get the url of the cursor to use for the map
	 * 
	 * @return
	 */
	@XPath("atom:content/cursorUrl")
	@Nullable
	String getCursorUrl();

	/**
	 * Permet de recuperer l'uid de la categorie parente de cette categorie. Si
	 * la methode retourne 0, la categorie est une categorie racine
	 * 
	 * @return
	 */
	@XPath("atom:content/@parentUid")
	int getParentUid();

	/**
	 * Permet de recuperer les uids des sous-categories directes de cette
	 * categorie.
	 * 
	 * @return
	 */
	@XPath("atom:content/child[@active = 'true']/@uid")
	int[] getChildren();

	/*
	 * Indique si la categorie est globalement active. Pour apparaitre au niveau
	 * de la liste de categorie disponible pour une universite, la categorie
	 * doit etre active au niveau de l'universite, mais aussi au niveau de cet
	 * attribut global.
	 * 
	 * @return
	 * 
	 * @XPath("atom:content/@active = 'true'") boolean isActive();
	 */

	/**
	 * Author: Mauricio 
	 * Indique si la categorie est globalement active. Pour
	 * apparaitre au niveau de la liste de categorie disponible pour une
	 * universite, la categorie doit etre active au niveau de l'universite, mais
	 * aussi au niveau de cet attribut global.
	 * 
	 * @return
	 */
	@XPath("atom:content/@active")
	boolean getActive();

	/**
	 * Permet de récupérer si le cagetory est actif ou non. Par exemple,
	 * certaines categories sont des categories de l'API "Que faire a Paris",
	 * cette methode retourne l'id de la categorie dans le system de l'API
	 * "Que faire a Paris". Si la methode retourne 0, aucun id externe
	 * disponible pour cette categorie
	 * 
	 * @return
	 */
	@XPath("atom:content/@externalUid")
	int getExternalUid();

}
