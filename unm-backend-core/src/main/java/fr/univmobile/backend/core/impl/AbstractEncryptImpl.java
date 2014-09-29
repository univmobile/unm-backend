package fr.univmobile.backend.core.impl;

import static com.google.common.base.Preconditions.checkNotNull;
import static org.apache.commons.lang3.CharEncoding.UTF_8;
import static org.apache.commons.lang3.StringUtils.isBlank;

import java.io.UnsupportedEncodingException;
import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * base implementation of password encryption.
 */
abstract class AbstractEncryptImpl implements Encrypt {

	protected AbstractEncryptImpl(final String algorithm) {

		this.algorithm = checkNotNull(algorithm, "algorithm");

		newMessageDigest(); // Check the algorithm
	}

	private final String algorithm;

	private MessageDigest newMessageDigest() {

		try {

			return MessageDigest.getInstance(algorithm);

		} catch (final NoSuchAlgorithmException e) {

			log.fatal(e, e);

			throw new RuntimeException(e);
		}
	}

	private static final Log log = LogFactory.getLog(AbstractEncryptImpl.class);

	@Override
	public final String encrypt(final String saltPrefix, final String password) {

		checkNotNull(saltPrefix, "saltPrefix");
		checkNotNull(password, "password");

		if (isBlank(saltPrefix)) {
			throw new IllegalArgumentException("saltPrefix is empty");
		}

		if (isBlank(password)) {
			throw new IllegalArgumentException("saltPrefix is password");
		}

		if (!saltPrefix.equals(saltPrefix.trim())) {
			throw new IllegalArgumentException(
					"saltPrefix contains leading or trailing spaces: "
							+ saltPrefix);
		}

		if (!password.equals(password.trim())) {
			throw new IllegalArgumentException(
					"password contains leading or trailing spaces");
		}

		final String s = saltPrefix + password;

		// Hashing.sha256().hashString(s, Charset.forName(UTF_8)).toString();

		final MessageDigest md = newMessageDigest();

		final byte[] bytes;

		try {

			bytes = s.getBytes(UTF_8);

		} catch (final UnsupportedEncodingException e) {

			throw new RuntimeException(e);
		}

		final byte[] digest = md.digest(bytes);

		return new BigInteger(1, digest).toString(16);
	}
}
