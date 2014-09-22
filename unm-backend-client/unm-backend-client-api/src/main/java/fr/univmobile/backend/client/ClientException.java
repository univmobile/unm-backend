package fr.univmobile.backend.client;

import javax.annotation.Nullable;

/**
 * Exception class of the client layer. The client layer is in the application
 * layer.
 */
public class ClientException extends Exception {

	/**
	 * for serialization.
	 */
	private static final long serialVersionUID = 2589556862838545813L;

	public ClientException(@Nullable final String message) {

		super(message);
	}
	
	public ClientException(@Nullable final Throwable cause) {
		
		super(cause);
	}
}
