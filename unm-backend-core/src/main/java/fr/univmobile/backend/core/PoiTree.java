package fr.univmobile.backend.core;

import net.avcompris.binding.annotation.XPath;
import fr.univmobile.commons.datasource.Entry;

public interface PoiTree extends Entry {

	@XPath("atom:content/atom:poi")
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
	
	@XPath("descendant-or-self::atom:poi")
	int sizeOfAllPois();
	
	@XPath("atom:content/atom:poi[@universityId = $arg0]/descendant-or-self::atom:poi")
	int sizeOfAllPoisByUniversityId(String universityId);
}
