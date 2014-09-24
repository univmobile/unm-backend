package fr.univmobile.backend.client;

import java.io.IOException;

public interface PoiClient {

	Pois getPois() throws IOException;

	Pois getPois(double lat, double lng) throws IOException;

	Poi getPoi(int id) throws IOException, ClientException;
}
