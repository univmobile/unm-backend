package fr.univmobile.backend.core;

import net.avcompris.binding.annotation.XPath;
import fr.univmobile.commons.datasource.Entry;

public interface PoiTree extends Entry<PoiTree> {

	@XPath("atom:content/poi")
	PoiTree[] getRoots();

	@XPath("@uid | atom:content/@uid")
	String getUid();

	@XPath("@name")
	String getName();

	@XPath("@typeId")
	int getPoiTypeId();

	@XPath("@typeLabel")
	int getPoiTypeLabel();

	@XPath("@categoryId")
	int getPoiCategoryId();

	boolean isNullPoiCategoryId();

	@XPath("@categoryLabel")
	String getPoiCategoryLabel();

	@XPath("atom:poi")
	PoiTree[] getChildren();
	
	@XPath("descendant-or-self::poi")
	int sizeOfAllPois();
	
	@XPath("atom:content/poi[@universityId = $arg0]/descendant-or-self::poi")
	int sizeOfAllPoisByUniversityId(String universityId);
}
