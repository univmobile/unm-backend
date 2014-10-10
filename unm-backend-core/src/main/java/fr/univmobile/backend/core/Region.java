package fr.univmobile.backend.core;

import net.avcompris.binding.annotation.XPath;
import fr.univmobile.commons.datasource.Entry;

public interface Region extends Entry<Region> {

	/**
	 * e.g. "ile_de_france"
	 */
	@XPath("atom:content/uid")
	String getUid();

	/**
	 * e.g. "Île de France"
	 */
	@XPath("atom:content/label")
	String getLabel();

	/**
	 * e.g.
	 * "http://univmobile.vswip.com/unm-backend-mock/listUniversities_ile_de_france"
	 */
	@XPath("atom:content/url")
	String getUrl();

	void setUrl(String url);

	@XPath("atom:content/universities/university")
	University[] getUniversities();

	int sizeOfUniversities();
	
	@XPath("count(atom:content/universities/university)")
	int getUniversityCount();
}
