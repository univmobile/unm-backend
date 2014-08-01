package fr.univmobile.backend.client;

import java.io.IOException;

public interface RegionClient {

	Region[] getRegions() throws IOException;

	University[] getUniversitiesByRegion(String regionId) throws IOException;
}
