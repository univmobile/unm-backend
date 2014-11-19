package fr.univmobile.backend.core;

import javax.annotation.Nullable;

import fr.univmobile.commons.datasource.Entry;
import net.avcompris.binding.annotation.XPath;

public interface ImageMap extends Entry<ImageMap> {

	/**
	 * Unique identifier of the map in image format
	 * e.g. 1
	 */
	@XPath("atom:content/@uid")
	int getUid();

	/**
	 * Name of the image map
	 */
	@XPath("atom:content/@name")
	String getName();
	
	/**
	 * Get the image of the maps 
	 */
	@XPath("atom:content/@url")
	String getImageUrl();

	/**
	 * Description of the image map
	 */
	@XPath("atom:content/description")
	@Nullable
	String getDescription();

	/**
	 * Get all the pois of the map
	 * @return
	 */
	@XPath("atom:content/poi[@active = 'true']")
	PoiInfo[] getPoiInfos();

	public interface PoiInfo {

		@XPath("@uid")
		int getId();

		@XPath("@coordinates")
		String getCoordinates();

	}

	/**
	 * Flag if the image map is active globally 
	 */
	@XPath("atom:content/@active = 'true'")
	boolean isActive();

}
