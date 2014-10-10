package fr.univmobile.commons.datasource;

import fr.univmobile.commons.datasource.Entry;
import net.avcompris.binding.annotation.XPath;

public interface MyRegion extends Entry<MyRegion> {

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
}
