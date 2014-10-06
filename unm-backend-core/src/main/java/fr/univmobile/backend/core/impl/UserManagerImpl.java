package fr.univmobile.backend.core.impl;

import static com.google.common.base.Preconditions.checkNotNull;

import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;

import javax.sql.DataSource;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import fr.univmobile.backend.core.SearchManager;
import fr.univmobile.backend.core.User;
import fr.univmobile.backend.core.UserBuilder;
import fr.univmobile.backend.core.UserDataSource;
import fr.univmobile.backend.core.UserManager;
import fr.univmobile.backend.history.LogQueue;
import fr.univmobile.backend.history.LoggableString;
import fr.univmobile.backend.history.Logged;
import fr.univmobile.commons.tx.Lock;
import fr.univmobile.commons.tx.TransactionException;
import fr.univmobile.commons.tx.TransactionManager;

public class UserManagerImpl extends AbstractDbManagerImpl implements
		UserManager {

	public UserManagerImpl(final LogQueue logQueue, final UserDataSource users,
			final SearchManager searchManager, final ConnectionType dbType,
			final Connection cxn) throws IOException {

		super(dbType, cxn);

		this.logQueue = checkNotNull(logQueue, "logQueue");
		this.users = checkNotNull(users, "users");
		// this.searchManager =
		checkNotNull(searchManager, "searchManager");

		// tx = TransactionManager.getInstance();
	}

	public UserManagerImpl(final LogQueue logQueue, final UserDataSource users,
			final SearchManager searchManager, final ConnectionType dbType,
			final DataSource ds) throws IOException {

		super(dbType, ds);

		this.logQueue = checkNotNull(logQueue, "logQueue");
		this.users = checkNotNull(users, "users");
		// this.searchManager =
		checkNotNull(searchManager, "searchManager");

		// tx = TransactionManager.getInstance();
	}

	private final LogQueue logQueue;
	// private final SearchManager searchManager;
	private final UserDataSource users;
	// private final TransactionManager tx;

	private static final Log log = LogFactory.getLog(UserManagerImpl.class);

	@Override
	public User getByUid(final String uid) {

		return users.getByUid(uid);
	}

	@Override
	public User addUser(final UserBuilder user) throws IOException,
			SQLException, TransactionException {

		if (log.isDebugEnabled()) {
			log.debug("addUser:" + user.getUid());
		}

		final Logged logged = logQueue.log(new LoggableString(
				"USER:ADD:{uid=%s}", user.getUid()));

		final TransactionManager tx = TransactionManager.getInstance();

		final Lock lock = tx.acquireLock(5000, "users", "toto");

		final User saved = lock.save(user);

		lock.commit();

		logQueue.log(logged,
				new LoggableString("USER:ADD:%s:{uid=%s}", logged, user
						.getUid()));

		users.reload(saved);
		
		return saved;
	}
}
