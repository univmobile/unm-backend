package fr.univmobile.commons.tx;

import javax.annotation.Nullable;

/**
 * Raise this exception when a lock takes too long a time to acquire. 
 */
public class LockTimeoutException extends TransactionException {

	/**
	 * for serialization.
	 */
	private static final long serialVersionUID = -466997198637505552L;

	public LockTimeoutException(@Nullable final String message) {

		super(message);
	}
}
