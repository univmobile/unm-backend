package fr.univmobile.backend.history;

import java.io.Serializable;

/**
 * Holder for business actions such as:
 * <ul>
 * <li>Authenticate to the application
 * <li>Disconnect from the application
 * <li>Create a new user
 * <li>Post a new comment
 * <li>Perform a search
 * </ul>
 */
public abstract class Loggable implements Serializable {

	/**
	 * for serialization.
	 */
	private static final long serialVersionUID = 7146546121642739533L;
	
	public abstract String getMessage(int maxLength);
}
