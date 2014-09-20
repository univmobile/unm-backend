package fr.univmobile.backend.core.impl;

/**
 * for password encryption.
 */
interface Encrypt {

	String encrypt(String saltPrefix, String password);
}
