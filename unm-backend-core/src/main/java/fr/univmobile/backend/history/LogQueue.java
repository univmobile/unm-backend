package fr.univmobile.backend.history;

/**
 * A <code>LogQueue</code> object is responsible for, generally asynchronously,
 * logging business commands or info to an audit-oriented storage, prior to some
 * Business Intelligence processing.
 */
public interface LogQueue {

	/**
	 * log a business action.
	 * 
	 * @param loggable
	 *            the description of the business action.
	 * @return a track record that may be used as a reference.
	 */
	Logged log(Loggable loggable);

	/**
	 * log a business action.
	 * 
	 * @param loggable
	 *            the description of the business action.
	 * @param a
	 *            track record of a previous logged business action, so the two
	 *            logged lines may later be associated.
	 */
	void log(Logged logged, Loggable loggable);
}
