package fr.univmobile.backend.history;

import java.io.Serializable;

public abstract class Logged implements Serializable {

	/**
	 * for serialization.
	 */
	private static final long serialVersionUID = 5528148165027606842L;

	public final long getTime() {

		return time;
	}

	private final long time = System.currentTimeMillis();
}
