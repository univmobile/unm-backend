package fr.univmobile.backend.client.json;

import java.io.IOException;

public interface ImageMapJSONClient {

	String getImageMapJSON(int mapId, int poiId) throws IOException;

	String getImageMapJSON(int mapId) throws IOException;
}
