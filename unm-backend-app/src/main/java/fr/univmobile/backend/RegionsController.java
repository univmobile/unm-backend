package fr.univmobile.backend;

import static com.google.common.base.Preconditions.checkNotNull;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.TreeSet;

import fr.univmobile.backend.core.Region;
import fr.univmobile.backend.core.RegionDataSource;
import fr.univmobile.commons.tx.Lock;
import fr.univmobile.commons.tx.TransactionException;
import fr.univmobile.commons.tx.TransactionManager;
import fr.univmobile.web.commons.HttpInputs;
import fr.univmobile.web.commons.HttpMethods;
import fr.univmobile.web.commons.HttpParameter;
import fr.univmobile.web.commons.HttpRequired;
import fr.univmobile.web.commons.Paths;
import fr.univmobile.web.commons.View;

@Paths({ "regions", "regions/" })
public class RegionsController extends AbstractBackendController {

	public RegionsController(final TransactionManager tx,
			final RegionDataSource regions) {

		this.tx = checkNotNull(tx, "tx");
		this.regions = checkNotNull(regions, "regions");
	}

	private final TransactionManager tx;
	private final RegionDataSource regions;

	@Override
	public View action() throws IOException, TransactionException {

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

		// 2.2. REGIONS

		final Map<String, Region> allRegions = regions.getAllBy(String.class,
				"uid");

		final List<Region> r = new ArrayList<Region>();

		setAttribute("regions", r);

		for (final String uid : new TreeSet<String>(allRegions.keySet())) {

			r.add(allRegions.get(uid));
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

		// 9. END

		return new View("regions.jsp");
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
