package fr.univmobile.backend.core.impl;


/**
 * password encryption that uses SHA-256 algorithm.
 * <p>
 * Hex Strings are 64-char long.
 */
final class EncryptSHA256 extends AbstractEncryptImpl {

	public EncryptSHA256() {

		super("SHA-256");
	}
}
