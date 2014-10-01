package fr.univmobile.backend.core.impl;

/**
 * for password encryption.
 */
public interface Encrypt {

	String encrypt(String saltPrefix, String password);
}
