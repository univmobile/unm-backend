package fr.univmobile.backend.core;

public interface AppSession {

	String getId();

	fr.univmobile.backend.domain.User getUser();

	void check() throws InvalidSessionException;
}
