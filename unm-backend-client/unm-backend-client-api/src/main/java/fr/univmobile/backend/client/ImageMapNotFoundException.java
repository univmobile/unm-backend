package fr.univmobile.backend.client;

public class ImageMapNotFoundException extends ClientException {

	private static final long serialVersionUID = -6117418140898905691L;

	public ImageMapNotFoundException(final int imageMapId) {
		
		super("Cannot find ImageMap with id: " + imageMapId);
	}

}
