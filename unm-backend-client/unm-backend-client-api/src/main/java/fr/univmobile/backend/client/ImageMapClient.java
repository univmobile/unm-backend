package fr.univmobile.backend.client;

import java.io.IOException;

public interface ImageMapClient {

	ImageMap getImageMap(int id, int poiId) throws IOException, ClientException;
	
	ImageMap getImageMap(int id) throws IOException, ClientException;
}
