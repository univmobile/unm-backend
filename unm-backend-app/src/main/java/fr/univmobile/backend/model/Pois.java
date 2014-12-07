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
	/*
	 * private final Poi[] pois; private final String name;
	 * 
	 * public Pois(/*final PoiGroup poiGroup final List<Poi> pois, final String
	 * name) {
	 * 
	 * this.pois = new Poi[pois.size()]; for (int i = 0; i < pois.size(); i++)
	 * this.pois[i] = pois.get(i); this.name = name;
	 * 
	 * // this.poiGroup = poiGroup; }
	 * 
	 * // private final PoiGroup poiGroup;
	 * 
	 * public String getName() {
	 * 
	 * return name; // return poiGroup.getGroupLabel(); }
	 * 
	 * public Poi[] getPois() {
	 * 
	 * return pois; // return poiGroup.getPois(); }
	 */
}
