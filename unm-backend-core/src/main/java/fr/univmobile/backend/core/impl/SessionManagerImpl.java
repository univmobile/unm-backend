package fr.univmobile.backend.core.impl;

import static com.google.common.base.Preconditions.checkNotNull;

import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.UUID;

import javax.annotation.Nullable;
import javax.sql.DataSource;

import org.apache.commons.lang3.NotImplementedException;
import org.apache.commons.lang3.RandomStringUtils;
import org.joda.time.DateTime;

import fr.univmobile.backend.core.AppSession;
import fr.univmobile.backend.core.SessionManager;
import fr.univmobile.backend.core.User;
import fr.univmobile.backend.core.User.Password;
import fr.univmobile.backend.core.UserBuilder;
import fr.univmobile.backend.core.UserDataSource;
import fr.univmobile.backend.history.LogQueue;
import fr.univmobile.backend.history.LoggableString;
import fr.univmobile.backend.history.Logged;

public class SessionManagerImpl extends AbstractDbManagerImpl implements
		SessionManager {

	public SessionManagerImpl(final LogQueue logQueue,
			final UserDataSource users, final ConnectionType dbType,
			final Connection cxn) throws IOException {

		super(dbType, cxn);

		this.logQueue = checkNotNull(logQueue, "logQueue");
		this.users = checkNotNull(users, "users");
	}

	public SessionManagerImpl(final LogQueue logQueue,
			final UserDataSource users, final ConnectionType dbType,
			final DataSource ds) throws IOException {

		super(dbType, ds);

		this.logQueue = checkNotNull(logQueue, "logQueue");
		this.users = checkNotNull(users, "users");
	}

	private final LogQueue logQueue;
	private final UserDataSource users;

	private final Encrypt encryptSHA1 = new EncryptSHA1();
	private final Encrypt encryptSHA256 = new EncryptSHA256();

	@Nullable
	@Override
	public AppSession login_classic(final String apiKey, final String login,
			final String password) throws IOException, SQLException {

		checkNotNull(apiKey, "apiKey");
		checkNotNull(login, "login");
		checkNotNull(password, "password");

		final Logged logged = logQueue.log(new LoggableString(
				"LOGIN:{apiKey=%s, login=\"%s\"}", apiKey, login));

		final User user = login_classic_validate(login, password);

		if (user == null) {

			logQueue.log(logged, new LoggableString(
					"LOGIN:INVALID:%s:{login=\"%s\"}", logged, login));

			return null;
		}

		final String appToken = newAppToken(user);

		final AppSession appSession = new AppSessionImpl(appToken, user);

		logQueue.log(logged, new LoggableString(
				"LOGIN:OK:%s:{login=\"%s\", appToken:%s}", logged, login,
				appToken));

		return appSession;
	}

	@Nullable
	private User login_classic_validate(final String login,
			final String inputPassword) throws IOException, SQLException {

		if (users.isNullByUid(login) && users.isNullByRemoteUser(login)) {
			return null; // Bad login
		}

		final User user = users.isNullByUid(login) //
		? users.getByRemoteUser(login) //
				: users.getByUid(login);

		if (user.isNullPassword()) {
			return null; // No active password
		}

		final Password p = user.getPassword();

		final String encryptionAlgorithm = p.getEncryptionAlgorithm();
		final String encryptedStoredPassword = p.getEncrypted();
		final String saltPrefix = p.getSaltPrefix();

		final Encrypt encrypt;

		if ("SHA-1".equalsIgnoreCase(encryptionAlgorithm)) {
			encrypt = encryptSHA1;
		} else if ("SHA-256".equalsIgnoreCase(encryptionAlgorithm)) {
			encrypt = encryptSHA256;
		} else {
			throw new NotImplementedException("Unknown encryption algorithm: "
					+ encryptionAlgorithm);
		}

		final String encryptedInputPassword = encrypt.encrypt(saltPrefix,
				inputPassword);

		if (!encryptedInputPassword.equals(encryptedStoredPassword)) {
			return null;
		}

		return user;
	}

	@Override
	public void logout(final AppSession appSession) throws IOException,
			SQLException {

		throw new NotImplementedException("xx");
	}

	/*
	 * @Override public User getCurrentUser(final AppSession appSession) throws
	 * IOException, SQLException {
	 * 
	 * throw new NotImplementedException(); }
	 */

	@Override
	public void save(final UserBuilder user) throws IOException, SQLException {

		throw new NotImplementedException("xx");
	}

	/**
	 * create a new, unique, primary key, appToken in the DataBase.
	 */
	private String newAppToken(final User user) throws IOException,
			SQLException {

		final int LENGTH = 64;

		final String uuid = UUID.randomUUID() + "-"
				+ RandomStringUtils.randomAlphanumeric(LENGTH);

		executeUpdate("insertAppToken", uuid.substring(0, LENGTH),
				user.getUid(), new DateTime());

		return uuid;
	}
}
