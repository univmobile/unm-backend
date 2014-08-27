package fr.univmobile.commons.tx;

import javax.annotation.Nullable;

public class TransactionException extends Exception {

	/**
	 * for serialization.
	 */
	private static final long serialVersionUID = 6962325760030091110L;

	public TransactionException(@Nullable final String message) {

		super(message);
	}
}
