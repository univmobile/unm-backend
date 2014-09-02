package fr.univmobile.commons.datasource;

import fr.univmobile.commons.datasource.Entry;
import net.avcompris.binding.annotation.XPath;

public interface MyPoi extends Entry<MyPoi> {

	/**
	 * e.g. { "paris1" }
	 */
	@XPath("atom:content/atom:universityId")
	String[] getUniversityIds();

	/**
	 * e.g. 1
	 */
	@XPath("atom:content/@uid")
	int getUid();
}
