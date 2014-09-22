package fr.univmobile.backend.client;

import java.io.Serializable;


/**
 * An <code>AppToken</code> maintains the applicative session on the backend for
 * a Mobile application.
 */
public interface AppToken extends Serializable {

	String getId();

	User getUser();
}
