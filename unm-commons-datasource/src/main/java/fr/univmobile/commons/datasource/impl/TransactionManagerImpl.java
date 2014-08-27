package fr.univmobile.commons.datasource.impl;

import static com.google.common.base.Preconditions.checkNotNull;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import fr.univmobile.commons.datasource.Entry;
import fr.univmobile.commons.datasource.EntryBuilder;
import fr.univmobile.commons.tx.Lock;
import fr.univmobile.commons.tx.LockTimeoutException;
import fr.univmobile.commons.tx.TransactionException;
import fr.univmobile.commons.tx.TransactionManager;

public class TransactionManagerImpl extends TransactionManager {

	public static TransactionManagerImpl getInstance() {

		if (instance == null) {

			loadInstance();
		}

		return instance;
	}

	private static synchronized void loadInstance() {

		if (instance == null) {

			instance = new TransactionManagerImpl();
		}
	}

	private static TransactionManagerImpl instance;

	private TransactionManagerImpl() {

	}

	private final Map<String, Lock> locks = new HashMap<String, Lock>();

	@Override
	public synchronized Lock acquireLock(final int timeoutMs,
			final String lockType, final Object id) throws TransactionException {

		final long start = System.currentTimeMillis();

		final String key = lockType + ":" + id;

		while (System.currentTimeMillis() < start + timeoutMs) {

			if (log.isDebugEnabled()) {
				log.debug("Attempt to acquire lock: " + key);
			}

			synchronized (locks) {

				if (!locks.containsKey(key)) {

					final Lock lock = new LockImpl(key);

					locks.put(key, lock);

					return lock;
				}
			}

			try {

				Thread.sleep(500);

			} catch (final InterruptedException e) {

				log.error(e);
			}
		}

		throw new LockTimeoutException("Cannot acquire lock for: " + key);
	}

	private void release(final LockImpl lock) {

		if (log.isDebugEnabled()) {
			log.debug("Releasing lock on: " + lock.key);
		}

		checkNotNull(lock, "lock");

		locks.remove(lock.key);
	}

	private static final Log log = LogFactory
			.getLog(TransactionManagerImpl.class);

	/**
	 * package private class.
	 */
	private class LockImpl implements Lock {

		public LockImpl(final String key) {

			this.key = checkNotNull(key, "key");
		}

		public final String key;

		/**
		 * Update all links to resources tagged by the corresponding "save()"
		 * methods.
		 */
		public synchronized void commit() throws TransactionException {

			for (final EntryBuilderImpl<?> data : stagedData) {

				data.cache();
			}

			release();

			//
			// @SuppressWarnings("unchecked")
			// final EntryBuilderImpl<E> entryBuilderImpl =
			// (EntryBuilderImpl<E>)
			// entryBuilder;

			// return entryBuilderImpl.cache();
		}

		public synchronized void release() throws TransactionException {

			stagedData.clear();

			TransactionManagerImpl.this.release(this);
		}

		public <E extends Entry<E>> E save(final EntryBuilder<E> entryBuilder)
				throws TransactionException {

			final EntryBuilderImpl<E> data = (EntryBuilderImpl<E>) entryBuilder;

			stagedData.add(data);
		
			try {
			
				return data.save();

			} catch (final IOException e) {
				throw new TransactionException(e);
			}			
		}

		private final List<EntryBuilderImpl<?>> stagedData = new ArrayList<EntryBuilderImpl<?>>();
	}
}
