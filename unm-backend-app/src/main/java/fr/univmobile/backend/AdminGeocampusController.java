package fr.univmobile.backend;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import fr.univmobile.backend.client.Poi;
import fr.univmobile.backend.client.PoiClient;
import fr.univmobile.backend.client.PoiClientFromLocal;
import fr.univmobile.backend.client.PoiGroup;
import fr.univmobile.backend.core.PoiDataSource;
import fr.univmobile.backend.core.PoiTreeDataSource;
import fr.univmobile.backend.core.RegionDataSource;
import fr.univmobile.backend.core.UserDataSource;
import fr.univmobile.commons.tx.TransactionException;
import fr.univmobile.commons.tx.TransactionManager;
import fr.univmobile.web.commons.Paths;
import fr.univmobile.web.commons.View;

@Paths({ "geocampus", "geocampus/" })
public class AdminGeocampusController extends AbstractBackendController {

	public AdminGeocampusController(
			final TransactionManager tx, final UserDataSource users,
			final RegionDataSource regions, final PoiDataSource pois,
			final PoiTreeDataSource poiTrees) {

		super(tx, users, regions, pois, poiTrees);

		//poiClient = 
	}

	private PoiClient getPoiClient() {
		
		return new PoiClientFromLocal(getBaseURL(), pois, poiTrees, regions);
	}

	@Override
	public View action() throws IOException, TransactionException {

		setAttribute("map", new Map("48.84650925911,2.3459243774", 13));

		final List<Pois> list = new ArrayList<Pois>();

		setAttribute("pois", list);

		for (final PoiGroup poiGroup :getPoiClient().getPois()) {

			list.add(new Pois(poiGroup));
		}

		return new View("geocampus_pois.jsp");
	}

	public static class Pois {

		private Pois(final PoiGroup poiGroup) {

			this.poiGroup = poiGroup;
		}

		private final PoiGroup poiGroup;

		public String getName() {

			return poiGroup.getGroupLabel();
		}

		public Poi[] getPois() {

			return poiGroup.getPois();
		}
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
