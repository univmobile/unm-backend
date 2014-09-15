package fr.univmobile.backend.core.impl;

import static com.google.common.base.Preconditions.checkNotNull;

import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;

import javax.annotation.Nullable;
import javax.sql.DataSource;

import org.joda.time.DateTime;

import fr.univmobile.backend.history.LogQueue;
import fr.univmobile.backend.history.Loggable;
import fr.univmobile.backend.history.Logged;

public class LogQueueDbImpl extends AbstractDbManagerImpl implements LogQueue {

	public LogQueueDbImpl(final ConnectionType dbType, final DataSource ds)
			throws IOException {

		super(dbType, ds);
	}

	public LogQueueDbImpl(final ConnectionType dbType, final Connection cxn)
			throws IOException {

		super(dbType, cxn);
	}

	private static final int MAX_LENGTH = 255;

	public static void setPrincipal(final String userUid) {

		checkNotNull(userUid, "userUid");

		threadLocalPincipal.set(userUid);
	}

	private static String checkedPrincipal() {

		final String userUid = threadLocalPincipal.get();

		if (userUid == null) {
			throw new IllegalStateException(
					"LogQueue: Principal has not been set");
		}

		return userUid.length() <= 40 ? userUid : userUid.substring(0, 40);
	}

	private static final ThreadLocal<String> threadLocalPincipal = new ThreadLocal<String>();

	@Override
	public Logged log(final Loggable loggable) {

		return insert(loggable, null);
	}

	private Logged insert(final Loggable loggable, @Nullable final Logged logged) {

		checkNotNull(loggable, "loggable");

		String message = loggable.getMessage(MAX_LENGTH);

		if (message.length() > MAX_LENGTH) {

			message = message.substring(MAX_LENGTH - 1) + "…";
		}

		final int logId;

		try {

			logId = executeUpdateGetAutoIncrement("insertNewLog",
					new DateTime(), checkedPrincipal(), message);

		} catch (final SQLException e) {

			return new LoggedDb(null);
		}

		return new LoggedDb(logId);
	}

	@Override
	public void log(final Loggable loggable, final Logged logged) {

		checkNotNull(logged, "logged");

		insert(loggable, logged);
	}

	private static class LoggedDb extends Logged {

		/**
		 * for serialization.
		 */
		private static final long serialVersionUID = 4725261469686777527L;

		public LoggedDb(@Nullable final Integer logId) {

			this.logId = logId;
		}

		@Nullable
		private final Integer logId;

		@Override
		public String toString() {

			return "" + logId;
		}
	}
}
