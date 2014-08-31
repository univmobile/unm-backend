package fr.univmobile.backend.client;

public class PoiNotFoundException extends ClientException {

	/**
	 * for serialization.
	 */
	private static final long serialVersionUID = -2277807832835501392L;

	public PoiNotFoundException(final int poiId) {

		super("Cannot find POI with id: " + poiId);
	}
}
