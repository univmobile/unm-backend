package fr.univmobile.backend;

import static com.google.common.base.Preconditions.checkNotNull;
import static fr.univmobile.commons.DataBeans.instantiate;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.TreeSet;

import javax.annotation.Nullable;

import fr.univmobile.backend.core.PoiDataSource;
import fr.univmobile.backend.core.PoiTree;
import fr.univmobile.backend.core.PoiTreeDataSource;
import fr.univmobile.backend.core.Region;
import fr.univmobile.backend.core.RegionDataSource;
import fr.univmobile.backend.core.University;
import fr.univmobile.backend.core.User;
import fr.univmobile.backend.core.UserDataSource;
import fr.univmobile.commons.tx.Lock;
import fr.univmobile.commons.tx.TransactionException;
import fr.univmobile.commons.tx.TransactionManager;
import fr.univmobile.web.commons.AbstractJspController;
import fr.univmobile.web.commons.HttpInputs;
import fr.univmobile.web.commons.HttpMethods;
import fr.univmobile.web.commons.HttpParameter;
import fr.univmobile.web.commons.HttpRequired;
import fr.univmobile.web.commons.View;

abstract class AbstractBackendController extends AbstractJspController {

	protected static final String DELEGATION_USER = "delegationUser";

	protected AbstractBackendController( //
			final TransactionManager tx, //
			final UserDataSource users, //
			final RegionDataSource regions, //
			final PoiDataSource pois, //
			final PoiTreeDataSource poiTrees //
	) {
		this.tx = checkNotNull(tx, "tx");
		this.users = checkNotNull(users, "users");
		this.regions = checkNotNull(regions, "regions");
		this.pois = checkNotNull(pois, "pois");

		this.poiTrees = checkNotNull(poiTrees, "poiTrees");
		// .getByUid("ile_de_france");
	}

	protected final TransactionManager tx;
	protected final UserDataSource users;
	protected final RegionDataSource regions;
	protected final PoiDataSource pois;
	protected final PoiTreeDataSource poiTrees;

	protected final User getUser() {

		return getSessionAttribute("user", User.class);
	}

	protected final User getDelegationUser() {

		return getSessionAttribute(DELEGATION_USER, User.class);
	}

	protected final View entered() throws TransactionException {

		// 1. UPDATE?

		final UpdateRegions ur = getHttpInputs(UpdateRegions.class);

		if (ur.isHttpValid()) {

			final Region ile_de_france = regions.getByUid("ile_de_france");
			final Region bretagne = regions.getByUid("bretagne");
			final Region unrpcl = regions.getByUid("unrpcl");

			if (!ile_de_france.getLabel().equals(ur.region_ile_de_france())) {
				updateRegionLabel(ile_de_france, ur.region_ile_de_france());
			}

			if (!bretagne.getLabel().equals(ur.region_bretagne())) {
				updateRegionLabel(bretagne, ur.region_bretagne());
			}

			if (!unrpcl.getLabel().equals(ur.region_unrpcl())) {
				updateRegionLabel(unrpcl, ur.region_unrpcl());
			}
		}

		// 2. VIEW

		// 2.1. USERS

		final Map<String, User> allUsers = users.getAllBy(String.class, "uid");

		final List<User> u = new ArrayList<User>();

		setAttribute("users", u);

		for (final String uid : new TreeSet<String>(allUsers.keySet())) {

			u.add(allUsers.get(uid));
		}

		// 2.2. REGIONS

		final Map<String, Region> allRegions = regions.getAllBy(String.class,
				"uid");

		final List<Region> r = new ArrayList<Region>();

		setAttribute("regions", r);

		for (final String uid : new TreeSet<String>(allRegions.keySet())) {

			r.add(allRegions.get(uid));
		}

		// 2.3. POIS

		final PoiTree poiTree = poiTrees.getByUid("ile_de_france"); // TODO

		final PoisInfo pois = instantiate(PoisInfo.class).setCount(
				poiTree.sizeOfAllPois());

		setAttribute("poisInfo", pois);

		for (final String uid : new TreeSet<String>(allRegions.keySet())) {

			final Region region = allRegions.get(uid);

			final PoisInfo.Region region2 = instantiate(PoisInfo.Region.class)
					.setUid(region.getUid()).setLabel(region.getLabel());

			pois.addToRegions(region2);

			for (final University university : region.getUniversities()) {

				final String universityId = university.getId();

				final PoisInfo.University university2 = instantiate(
						PoisInfo.University.class)
						.setId(universityId)
						.setTitle(university.getTitle())
						.setPoiCount(
								poiTree.sizeOfAllPoisByUniversityId(universityId));

				region2.addToUniversities(university2);
			}
		}

		/*
		 * regions: type: fr.univmobile.backend.core.Region2[] value: - uid:
		 * bretagne label: Bretagne universities: type:
		 * fr.univmobile.backend.core.University[] value: - id: ubo title:
		 * Université de Bretagne Occidentale poiCount: 103 - id: rennes1 title:
		 * Université de Rennes 1 poiCount: 923
		 * 
		 * properties.getProperty("Buildinfo-BuildDisplayName"))
		 * .setBuildId(properties.getProperty("Buildinfo-BuildId"))
		 * .setGitCommitId(properties.getProperty("Buildinfo-Rev"));
		 */

		// 9 END

		return new View("entered.jsp");
	}

	private void updateRegionLabel(final Region region, final String label)
			throws TransactionException {

		final Lock lock = tx.acquireLock(5000, "regions", region.getUid());
		try {

			lock.save(regions.update(region).setLabel(label));

			lock.commit();

		} finally {
			lock.release();
		}
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

interface PoisInfo {

	/**
	 * Total count of POIs in the DataBase.
	 */
	int getCount();

	PoisInfo setCount(int count);

	/**
	 * e.g. "POIs de plus haut niveau"
	 */
	@Nullable
	String getContext();
	
	PoisInfo setContext(String context);

	/**
	 * Count of POIs returned by the search.
	 */
	int getResultCount();
	
	PoisInfo setResultCount(int count);

	Region[] getRegions();

	PoisInfo addToRegions(Region region);

	interface Region {

		String getUid();

		Region setUid(String uid);

		String getLabel();

		Region setLabel(String label);

		University[] getUniversities();

		Region addToUniversities(University university);
	}

	interface University {

		String getId();

		University setId(String id);

		String getTitle();

		University setTitle(String title);

		int getPoiCount();

		University setPoiCount(int poiCount);
	}
}
