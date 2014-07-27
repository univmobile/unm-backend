package fr.univmobile.backend;

import static com.google.common.base.Preconditions.checkNotNull;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.TreeSet;

import fr.univmobile.backend.core.Region;
import fr.univmobile.backend.core.RegionDataSource;
import fr.univmobile.backend.core.User;
import fr.univmobile.backend.core.UserDataSource;
import fr.univmobile.web.commons.AbstractController;

abstract class AbstractBackendController extends AbstractController {

	protected static final String DELEGATION_USER = "delegationUser";

	protected AbstractBackendController( //
			final UserDataSource users, //
			final RegionDataSource regions //
			) {

		this.users = checkNotNull(users, "users");
		this.regions = checkNotNull(regions, "regions");
	}

	protected final UserDataSource users;
	protected final RegionDataSource regions;

	protected final User getUser() {

		return getSessionAttribute("user", User.class);
	}

	protected final User getDelegationUser() {

		return getSessionAttribute("delegationUser", User.class);
	}

	protected final String entered() {

		final Map<String, User> allUsers = users.getAllBy("uid");

		final List<User> u = new ArrayList<User>();

		for (final String uid : new TreeSet<String>(allUsers.keySet())) {

			u.add(allUsers.get(uid));
		}

		setAttribute("users", u);

		final Map<String, Region> allRegions = regions.getAllBy("uid");

		final List<Region> r = new ArrayList<Region>();

		for (final String uid : new TreeSet<String>(allRegions.keySet())) {

			r.add(allRegions.get(uid));
		}

		setAttribute("regions", r);

		return "entered.jsp";
	}
}
