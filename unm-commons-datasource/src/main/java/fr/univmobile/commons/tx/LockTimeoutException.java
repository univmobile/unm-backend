package fr.univmobile.commons.tx;

import javax.annotation.Nullable;

public class LockTimeoutException extends TransactionException {

	/**
	 * for serialization.
	 */
	private static final long serialVersionUID = -466997198637505552L;

	public LockTimeoutException(@Nullable final String message) {

		super(message);
	}
}
