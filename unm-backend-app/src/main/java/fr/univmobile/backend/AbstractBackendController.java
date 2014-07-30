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
import fr.univmobile.web.commons.HttpInputs;
import fr.univmobile.web.commons.HttpMethods;
import fr.univmobile.web.commons.HttpParameter;
import fr.univmobile.web.commons.HttpRequired;

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

		// 1. UPDATE?

		final UpdateRegions ur = getHttpInputs(UpdateRegions.class);
		
		if (ur.isHttpValid()) {

			final Region ile_de_france = regions.getByUid("ile_de_france");
			final Region bretagne = regions.getByUid("bretagne");
			final Region unrpcl = regions.getByUid("unrpcl");

			if (!ile_de_france.getLabel().equals(ur.region_ile_de_france())) {
				regions.update(ile_de_france)
						.setLabel(ur.region_ile_de_france()).save();
			}

			if (!bretagne.getLabel().equals(ur.region_bretagne())) {
				regions.update(bretagne).setLabel(ur.region_bretagne()).save();
			}

			if (!unrpcl.getLabel().equals(ur.region_unrpcl())) {
				regions.update(unrpcl).setLabel(ur.region_unrpcl()).save();
			}
		}

		// 2. VIEW

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

	@HttpMethods("POST")
	interface UpdateRegions extends HttpInputs {

		@HttpRequired
		@HttpParameter(trim = true)
		String region_ile_de_france();

		@HttpRequired
		@HttpParameter(trim = true)
		String region_bretagne();

		@HttpRequired
		@HttpParameter(trim = true)
		String region_unrpcl();
	}
}
