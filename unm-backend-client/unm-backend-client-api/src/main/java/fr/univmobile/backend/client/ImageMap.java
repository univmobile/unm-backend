package fr.univmobile.backend.client;

import javax.annotation.Nullable;

/**
 * Contains all the information to display on a map after a user is reading the QR Code
 */
public interface ImageMap {
	
	int getId();
	
	String getName();
	
	@Nullable
	String getDescription();
	
	/**
	 * Url of the map to display (URL of an image)
	 * @return
	 */
	String getImageUrl();

	/**
	 * The Poi linked to the flashed QR Code 
	 * @return
	 */
	ImageMapPoi getSelectedPoi();
	
	/**
	 * The coordinates of the poi are the 
	 * @return
	 */
	ImageMapPoi[] getPois();
}
