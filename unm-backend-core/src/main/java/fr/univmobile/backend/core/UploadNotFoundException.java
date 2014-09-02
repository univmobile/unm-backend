package fr.univmobile.backend.core;

public class UploadNotFoundException extends Exception {

	/**
	 * for serialization.
	 */
	private static final long serialVersionUID = -6645285906144800045L;

	public UploadNotFoundException(final String uploadPath, final Throwable cause) {

		super("Cannot find upload for path: " + uploadPath, cause);
	}
}
