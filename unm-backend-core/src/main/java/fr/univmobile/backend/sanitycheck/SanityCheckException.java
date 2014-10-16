package fr.univmobile.backend.sanitycheck;

import javax.annotation.Nullable;

public class SanityCheckException extends RuntimeException {

	/**
	 * for serialization.
	 */
	private static final long serialVersionUID = -5479964781687429002L;

	public SanityCheckException(@Nullable final String message) {

		super(message);
	}
}
