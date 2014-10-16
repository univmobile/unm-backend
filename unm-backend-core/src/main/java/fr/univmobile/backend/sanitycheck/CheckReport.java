package fr.univmobile.backend.sanitycheck;

/**
 * Object returned by the {@link BackendChecker} check methods.
 */
public abstract class CheckReport {

	public abstract boolean isSuccess();
}
