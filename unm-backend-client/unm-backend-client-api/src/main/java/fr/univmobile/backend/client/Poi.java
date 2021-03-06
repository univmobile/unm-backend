package fr.univmobile.backend.client;

import javax.annotation.Nullable;


public interface Poi {

	int getId();

	String getName();

	@Nullable
	String getAddress();
	
	@Nullable
	String getPhone();
	
	@Nullable
	String getFloor();
	
	@Nullable
	String getEmail();
	
	@Nullable
	String getFax();
	
	@Nullable
	String getOpeningHours();
	
	@Nullable
	String getItinerary();
	
	String getCoordinates();
	
	String getLatitude();
	
	String getLongitude();

	String getCommentsUrl();

	@Nullable
	String getUrl();
	
	@Nullable
	String getImageUrl();
	
	int getImageWidth();
	
	int getImageHeight();
	
	/**
	 * e.g. "green"
	 */
	String getMarkerType();
	
	/**
	 * e.g. "A"
	 */
	String getMarkerIndex();
	
	String[] getUniversityIds();
}
