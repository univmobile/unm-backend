package fr.univmobile.backend.model;

import fr.univmobile.backend.client.Poi;
import fr.univmobile.backend.client.PoiGroup;

/**
 * POI holder, bridge between client and application view layers.
 */
public class Pois {

	public Pois(final PoiGroup poiGroup) {

		this.poiGroup = poiGroup;
	}

	private final PoiGroup poiGroup;

	public String getName() {

		return poiGroup.getGroupLabel();
	}

	public Poi[] getPois() {

		return poiGroup.getPois();
	}
}
