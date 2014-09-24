package fr.univmobile.backend;

import static com.google.common.base.Preconditions.checkNotNull;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import fr.univmobile.backend.client.PoiClient;
import fr.univmobile.backend.client.PoiClientFromLocal;
import fr.univmobile.backend.client.PoiGroup;
import fr.univmobile.backend.core.PoiDataSource;
import fr.univmobile.backend.core.PoiTreeDataSource;
import fr.univmobile.backend.core.RegionDataSource;
import fr.univmobile.backend.model.Pois;
import fr.univmobile.commons.tx.TransactionException;
import fr.univmobile.web.commons.Paths;
import fr.univmobile.web.commons.View;

@Paths({ "geocampus", "geocampus/" })
public class AdminGeocampusController extends AbstractBackendController {

	public AdminGeocampusController(
			// final TransactionManager tx,
			// final UserDataSource users,
			final RegionDataSource regions, final PoiDataSource pois,
			final PoiTreeDataSource poiTrees) {

		// super(tx, users, regions, pois, poiTrees);

		this.pois = checkNotNull(pois, "pois");
		this.poiTrees = checkNotNull(poiTrees, "poiTrees");
		this.regions = checkNotNull(regions, "regions");
	}

	private final RegionDataSource regions;
	private final PoiDataSource pois;
	private final PoiTreeDataSource poiTrees;

	private PoiClient getPoiClient() {

		return new PoiClientFromLocal(getBaseURL(), pois, poiTrees, regions);
	}

	@Override
	public View action() throws IOException, TransactionException {

		setAttribute("map", new Map("48.84650925911,2.3459243774", 13));

		final List<Pois> list = new ArrayList<Pois>();

		setAttribute("pois", list);

		for (final PoiGroup poiGroup : getPoiClient().getPois().getGroups()) {

			list.add(new Pois(poiGroup));
		}

		setAttribute("postCommentUrl", getBaseURL() + "/comment");

		return new View("geocampus_pois.jsp");
	}

	public static class Map {

		private Map(final String center, final int zoom) {

			this.center = center;
			this.zoom = zoom;
		}

		public String getCenter() {

			return center;
		}

		public int getZoom() {

			return zoom;
		}

		private final String center;
		private final int zoom;
	}
}
