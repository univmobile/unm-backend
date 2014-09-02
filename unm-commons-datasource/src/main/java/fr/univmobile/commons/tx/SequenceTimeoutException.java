package fr.univmobile.commons.tx;


/**
 * Raise this exception when a sequence implementation doesn’t return in a given
 * time.
 */
public class SequenceTimeoutException extends TransactionException {

	/**
	 * for serialization.
	 */
	private static final long serialVersionUID = -5872424226300768992L;
}
