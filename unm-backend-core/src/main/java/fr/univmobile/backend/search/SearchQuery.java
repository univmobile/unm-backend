package fr.univmobile.backend.search;

import java.io.Serializable;

/**
 * One of the 
 * differences between {@link SearchContext} and {@link SearchQuery} is
 * that a {@link SearchQuery} object is serializable, whereas
 * a {@link SearchContext} object will generally be a list of references linked
 * to a DataBase.
 */
public class SearchQuery implements Serializable {

	/**
	 * for serialization.
	 */
	private static final long serialVersionUID = -2642780013760447467L;

}
