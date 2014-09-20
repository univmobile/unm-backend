package fr.univmobile.backend.core.impl;


/**
 * password encryption that uses SHA-1 algorithm.
 * <p>
 * Hex Strings are 40-char long.
 */
final class EncryptSHA1 extends AbstractEncryptImpl {

	public EncryptSHA1() {

		super("SHA-1");
	}
}
