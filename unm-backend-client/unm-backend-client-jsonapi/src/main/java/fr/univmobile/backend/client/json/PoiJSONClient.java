package fr.univmobile.backend.client.json;

import java.io.IOException;

public interface PoiJSONClient {

	String getPoisJSON() throws IOException;

	String getPoisJSON(double lat, double lng) throws IOException;

	String getPoisByRegionJSON(String regionId) throws IOException;
	
	String getPoisByCategoryJSON(int categoryId) throws IOException;

	String getPoisByRegionAndCategoryJSON(String regionId, Integer categoryId) throws IOException;
	
	String getNearestPoisJSON(double lat, double lon, double metersAway) throws IOException;
}
