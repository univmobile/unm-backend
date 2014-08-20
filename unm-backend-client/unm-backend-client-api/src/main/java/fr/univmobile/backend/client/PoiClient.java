package fr.univmobile.backend.client;

import java.io.IOException;

public interface PoiClient {

	PoiGroup[] getPois() throws IOException;
}
