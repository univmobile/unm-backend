package fr.univmobile.commons.tx;

import fr.univmobile.commons.datasource.impl.TransactionManagerImpl;

public abstract class TransactionManager {

	public static TransactionManager getInstance() {

		if (instance == null) {

			loadInstance();
		}

		return instance;
	}

	private static synchronized void loadInstance() {

		if (instance == null) {

			instance = TransactionManagerImpl.getInstance();
		}
	}

	private static TransactionManager instance;

	public abstract Lock acquireLock(int timeoutMs, String lockType, Object id)
			throws TransactionException;
}
