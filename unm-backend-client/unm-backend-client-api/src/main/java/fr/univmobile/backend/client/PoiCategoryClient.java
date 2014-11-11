package fr.univmobile.backend.client;

import java.io.IOException;

public interface PoiCategoryClient {

	PoiCategory getPoiCategory(int id) throws IOException, ClientException;
}
