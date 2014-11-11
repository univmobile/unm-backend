package fr.univmobile.backend.client;

public class PoiCategoryNotFoundException extends ClientException {

	private static final long serialVersionUID = -3266596240522361765L;

	public PoiCategoryNotFoundException(final int poiId) {

		super("Cannot find POI with id: " + poiId);
	}

}
