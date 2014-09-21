package fr.univmobile.backend.core;

public interface AppSession {

	String getId();

	User getUser();

	void check() throws InvalidSessionException;
}
