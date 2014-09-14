package fr.univmobile.backend;

import static fr.univmobile.commons.DataBeans.instantiate;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.TreeSet;

import javax.annotation.Nullable;

import fr.univmobile.backend.core.PoiDataSource;
import fr.univmobile.backend.core.PoiTreeDataSource;
import fr.univmobile.backend.core.RegionDataSource;
import fr.univmobile.backend.core.User;
import fr.univmobile.backend.core.UserDataSource;
import fr.univmobile.commons.tx.TransactionException;
import fr.univmobile.commons.tx.TransactionManager;
import fr.univmobile.web.commons.Paths;
import fr.univmobile.web.commons.View;

@Paths({ "users", "users/" })
public class UsersController extends AbstractBackendController {

	public UsersController(final TransactionManager tx,
			final UserDataSource users, final RegionDataSource regions,
			final PoiDataSource pois, final PoiTreeDataSource poiTrees) {

		super(tx, users, regions, pois, poiTrees);
	}

	@Override
	public View action() throws IOException, TransactionException {

		getDelegationUser();
		
		final Map<String, User> allUsers = users.getAllBy(String.class, "uid");

		final Map<String, User> results = users.getAllBy(String.class, "uid");

		// 1. USERS INFO

		final UsersInfo usersInfo = instantiate(UsersInfo.class) //
				.setCount(allUsers.size()) //
				.setContext("Tous les utilisateurs") //
				.setResultCount(results.size());

		setAttribute("usersInfo", usersInfo);

		// 2. USERS DATA

		final List<User> u = new ArrayList<User>();

		setAttribute("users", u);

		for (final String uid : new TreeSet<String>(allUsers.keySet())) {

			u.add(allUsers.get(uid));
		}

		// 9. END

		return new View("users.jsp");
	}
}

interface UsersInfo {

	/**
	 * Total count of users in the DataBase.
	 */
	int getCount();

	UsersInfo setCount(int count);

	/**
	 * e.g. "Tous les utilisateurs"
	 */
	@Nullable
	String getContext();

	UsersInfo setContext(String context);

	/**
	 * Count of users returned by the search.
	 */
	int getResultCount();

	UsersInfo setResultCount(int count);
}
