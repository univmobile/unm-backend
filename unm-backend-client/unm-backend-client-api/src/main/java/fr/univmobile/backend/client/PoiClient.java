package fr.univmobile.backend.client;

import java.io.IOException;

public interface PoiClient {

	Pois getPois() throws IOException;

	Pois getPois(double lat, double lng) throws IOException;
	
	Pois getPoisByRegion(String regionId) throws IOException;

	Poi getPoi(int id) throws IOException, ClientException;
}
