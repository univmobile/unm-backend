package fr.univmobile.backend.client;

import java.io.IOException;

public interface PoiClient {

	Pois getPois() throws IOException;

	Pois getPois(double lat, double lng) throws IOException;
	
	Pois getPoisByRegion(String regionId) throws IOException;

	Poi getPoi(int id) throws IOException, ClientException;

	Pois getPoisByCategory(int categoryId) throws IOException;
	
	Pois getPoisByRegionAndCategory(String regionId, Integer categoryId) throws IOException;
	
	Pois getPoisByRegionAndCategory(String regionUid, Integer categoryId, int[] excludeCategories, String filterByUniversity, boolean completeWholeTree) throws IOException;

	Pois getNearestPois(double lat, double lon, double metersAway) throws IOException;
}
