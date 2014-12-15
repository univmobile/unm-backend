package fr.univmobile.backend.core;

import javax.annotation.Nullable;

import net.avcompris.binding.annotation.XPath;
import fr.univmobile.commons.datasource.Entry;

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
	@XPath("atom:content/poi")
	PoiInfo[] getPoiInfos();

	public interface PoiInfo {

		@XPath("@uid")
		int getId();

		@XPath("@coordinates")
		String getCoordinates();

	}

	// Author: Mauricio
	@XPath("atom:content/@active")
	String getActive();

}
