package fr.univmobile.backend.client;

import javax.annotation.Nullable;

public interface Pois {

	@Nullable
	MapInfo getMapInfo();

	PoiGroup[] getGroups();

	interface MapInfo {

		int getPreferredZoom();

		double getLat();

		double getLng();
	}
}
