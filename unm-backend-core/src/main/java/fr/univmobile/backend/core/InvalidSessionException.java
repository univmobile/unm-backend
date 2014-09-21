package fr.univmobile.backend.core;

import javax.annotation.Nullable;

public class InvalidSessionException extends Exception {

	/**
	 * for serialization.
	 */
	private static final long serialVersionUID = 1765433706277958996L;
	
	public InvalidSessionException(@Nullable final String message) {
		
		super(message);
	}
}
