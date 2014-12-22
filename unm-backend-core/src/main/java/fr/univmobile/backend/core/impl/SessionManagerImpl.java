package fr.univmobile.backend.core.impl;

import static com.google.common.base.Preconditions.checkNotNull;

import java.io.IOException;
import java.io.Serializable;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;
import java.util.UUID;

import javax.annotation.Nullable;
import javax.sql.DataSource;

import org.apache.commons.lang3.NotImplementedException;
import org.apache.commons.lang3.RandomStringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.joda.time.DateTime;

import com.google.common.collect.Lists;

import fr.univmobile.backend.core.AppSession;
import fr.univmobile.backend.core.InvalidSessionException;
import fr.univmobile.backend.core.LoginConversation;
import fr.univmobile.backend.core.SessionManager;
import fr.univmobile.backend.core.UserBuilder;
import fr.univmobile.backend.domain.User;
import fr.univmobile.backend.domain.UserRepository;
import fr.univmobile.backend.history.LogQueue;
import fr.univmobile.backend.history.LoggableString;
import fr.univmobile.backend.history.Logged;
import fr.univmobile.commons.tx.Lock;
import fr.univmobile.commons.tx.TransactionException;
import fr.univmobile.commons.tx.TransactionManager;

public class SessionManagerImpl extends AbstractDbManagerImpl implements
		SessionManager {

	private static final Log log = LogFactory.getLog(SessionManagerImpl.class);

	public SessionManagerImpl(final LogQueue logQueue,
			final UserRepository users,  
			final ConnectionType dbType,
			final Connection cxn) throws IOException {

		super(dbType, cxn);

		this.logQueue = checkNotNull(logQueue, "logQueue");
		this.users = checkNotNull(users, "users");
	}

	public SessionManagerImpl(final LogQueue logQueue,
			final UserRepository users,
			final ConnectionType dbType,
			final DataSource ds) throws IOException {

		super(dbType, ds);

		this.logQueue = checkNotNull(logQueue, "logQueue");
		this.users = checkNotNull(users, "users");
	}

	private final LogQueue logQueue;
	private final UserRepository users;

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

			if (log.isInfoEnabled()) {
				log.info("Invalid log-in for: " + login);
			}

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

		User user = users.findByUsername(login);
		if (user == null) {
			user = users.findByRemoteUser(login);
		}

		if (users == null) {
			if (log.isInfoEnabled()) {
				log.info("No user with login: " + login);
			}
			return null; // Bad login
		}
		
		if (user.getPassword() == null) {
			if (log.isInfoEnabled()) {
				log.info("No stored password for user with login: " + login);
			}
			return null; // No active password
		}

		final String p = user.getPassword();

		/*
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
		*/
		
		if (log.isDebugEnabled()) {
			// log.debug("saltPrefix: " + saltPrefix);
			// log.debug("inputPassword: " + inputPassword);
			// log.debug("encryptedInputPassword: " + encryptedInputPassword);
		}

		//if (!encryptedInputPassword.equals(encryptedStoredPassword)) {
		if (!inputPassword.equals(p)) {
			if (log.isInfoEnabled()) {
				log.info("Password do not match for login: " + login);
			}
			return null;
		}

		/*
		if (user.isNullPrimaryUser()) {

			return user;
		}

		return recursiveGetPrimaryUser(Lists.newArrayList(user));
		*/
		return user;
	}

	/*
	private User recursiveGetPrimaryUser(final List<User> users)
			throws IOException {
	
		final User user = users.get(users.size() - 1);

		if (user.isNullPrimaryUser()) {
			return user;
		}

		final String primaryUserUid = user.getPrimaryUser().getUid();

		for (final User u : users) {
			if (primaryUserUid.equals(u.getUid())) {
				throw new StackOverflowError("primaryUserUid: "
						+ primaryUserUid + " for user.uid: " + user.getUid());
			}
		}

		final User primaryUser = this.users.getByUid(primaryUserUid);

		users.add(primaryUser);

		return recursiveGetPrimaryUser(users);
	}
	*/
	
	@Override
	public void logout(final AppSession appSession) throws IOException,
			SQLException {

		final String userUid = appSession.getUser().getUsername();
		final String sessionId = appSession.getId();

		if (!isValid(appSession)) {

			logQueue.log(new LoggableString(
					"LOGOUT:INVALID:{uid=\"\", token=%s}", userUid, sessionId));

			return;
		}

		executeUpdate("endSession", new DateTime(), sessionId);

		logQueue.log(new LoggableString("LOGOUT:INVALID:{uid=\"\", token=%s}",
				userUid, sessionId));
	}

	@Override
	public void save(final AppSession appSession, final UserBuilder user)
			throws TransactionException, IOException, SQLException,
			InvalidSessionException {
		
		appSession.check();

		final TransactionManager tx = TransactionManager.getInstance();

		final Lock lock = tx.acquireLock(5000, "users", "toto");

		lock.save(user);

		lock.commit();

		logQueue.log(new LoggableString("USER:UPDATE:{uid=\"%s\"}", //
				user.getUid()));

		//users.reload();
	}

	/**
	 * create a new, unique, primary key, appToken in the DataBase.
	 */
	private String newAppToken(final User user) throws IOException,
			SQLException {

		final String uuid = newUUID(40);

		executeUpdate("insertAppToken", uuid, user.getUsername(), new DateTime());

		return uuid;
	}

	private static String newUUID(final int length) {

		String uuid = UUID.randomUUID().toString();

		uuid += "-" + RandomStringUtils.randomAlphanumeric(length);

		return uuid.substring(0, length);
	}

	private boolean isValid(final AppSession session) throws SQLException {

		final String sessionId = session.getId();

		final int result = executeQueryGetInt("checkSession", sessionId);

		return result == 1;
	}

	private final class AppSessionImpl implements AppSession {

		public AppSessionImpl(final String id, final User user) {

			this.id = checkNotNull(id, "id");
			this.user = checkNotNull(user, "user");
		}

		private final String id;
		private final User user;

		@Override
		public String getId() {

			return id;
		}

		@Override
		public User getUser() {

			return user;
		}

		@Override
		public void check() throws InvalidSessionException {

			final boolean valid;

			try {

				valid = SessionManagerImpl.this.isValid(this);

			} catch (final SQLException e) {
				throw new RuntimeException(e);
			}

			if (!valid) {

				throw new InvalidSessionException("appSession.id: " + id);
			}
		}
	}

	@Override
	public AppSession getAppSession(final String sessionId) throws IOException,
			SQLException, InvalidSessionException {

		checkNotNull(sessionId, "sessionId");

		final String userUid;

		try {

			userUid = executeQueryGetStringNullable("getActiveSession_userUid",
					sessionId);

		} catch (final SQLException e) {

			throw new InvalidSessionException("sessionId: " + sessionId);
		}

		if (userUid == null) {

			throw new InvalidSessionException("sessionId: " + sessionId);
		}

		final User user = users.findByUsername(userUid);

		return new AppSessionImpl(sessionId, user);
	}

	@Override
	public LoginConversation prepare(final String apiKey) throws IOException,
			SQLException {

		final String loginToken = newUUID(40);
		final String key = newUUID(40);

		executeUpdate("insertLoginConversation", loginToken, key,
				new DateTime());

		return new LoginConversationImpl(loginToken, key);
	}

	private static final class LoginConversationImpl implements
			LoginConversation, Serializable {

		/**
		 * for serialization.
		 */
		private static final long serialVersionUID = 8164762659083068376L;

		public LoginConversationImpl(final String loginToken, final String key) {

			this.loginToken = checkNotNull(loginToken, "loginToken");
			this.key = checkNotNull(key, "key");
		}

		private final String loginToken;
		private final String key;

		@Override
		public String getLoginToken() {

			return loginToken;
		}

		@Override
		public String getKey() {

			return key;
		}
	}

	@Override
	public void updateLoginConversation(final String loginToken, final User user)
			throws IOException, SQLException {

		checkNotNull(loginToken, "loginToken");
		checkNotNull(user, "user");

		final String uid = user.getUsername();

		final int result = executeUpdate("updateLoginConversation", uid,
				loginToken);

		final int REF = 1;

		if (result != REF) {

			throw new IllegalStateException("Illegal result: " + result
					+ ", should be: " + REF + " for loginToken: " + loginToken
					+ ", user: " + uid);
		}
	}

	@Override
	public AppSession retrieve(String apiKey, String loginToken, String key)
			throws IOException, SQLException {

		checkNotNull(apiKey, "apiKey");
		checkNotNull(loginToken, "loginToken");
		checkNotNull(key, "key");

		LogQueueDbImpl.setAnonymous();
		
		final Logged logged = logQueue.log(new LoggableString(
				"RETRIEVE:{apiKey=%s, loginToken=\"%s\"}", apiKey, loginToken));

		final User user = retrieve_validate(loginToken, key);

		if (user == null) {

			if (log.isInfoEnabled()) {
				log.info("Invalid login token: " + loginToken);
			}

			logQueue.log(logged, new LoggableString(
					"RETRIEVE:INVALID:%s:{loginToken=\"%s\"}", logged,
					loginToken));

			return null;
		}

		final String appToken = newAppToken(user);

		final AppSession appSession = new AppSessionImpl(appToken, user);

		logQueue.log(logged, new LoggableString(
				"RETRIEVE:%s:{loginToken=\"%s\", appToken:%s}", logged,
				loginToken, appToken));

		return appSession;
	}

	@Nullable
	private User retrieve_validate(final String loginToken, final String key)
			throws IOException, SQLException {

		final String userUid = executeQueryGetStringNullable(
				"getPreparedUserUid", loginToken, key);

		if (userUid == null) {
			return null; // Bad loginToken+key
		}

		final User user =  users.findByUsername(userUid);

		if (user == null) {
			if (log.isInfoEnabled()) {
				log.info("No user: " + userUid + " for loginToken: "
						+ loginToken);
			}
			return null; // Bad login
		}

		/*
		if (user.isNullPrimaryUser()) {

			return user;
		}

		return recursiveGetPrimaryUser(Lists.newArrayList(user));
		*/
		return user;
	}
}
