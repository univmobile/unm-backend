package fr.univmobile.backend.core;

import java.io.IOException;
import java.sql.SQLException;

import fr.univmobile.commons.tx.TransactionException;

public interface UserManager {

	User getByUid(String uid) throws SQLException, IOException;

	User addUser(UserBuilder user) throws TransactionException, SQLException,
			IOException;
}
