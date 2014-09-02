package fr.univmobile.backend.client.json;

import java.io.IOException;

public interface RegionJSONClient {

	String getRegionsJSON() throws IOException;

	String getUniversitiesJSONByRegion(String regionId) throws IOException;
}
