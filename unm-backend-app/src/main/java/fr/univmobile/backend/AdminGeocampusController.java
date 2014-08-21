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
import fr.univmobile.web.commons.Paths;

@Paths({ "pois", "pois/" })
public class AdminGeocampusController extends AbstractBackendController {

	public AdminGeocampusController(final UserDataSource users,
			final RegionDataSource regions, final PoiDataSource pois,
			final PoiTreeDataSource poiTrees) {

		super(users, regions, pois, poiTrees);

		poiClient = new PoiClientFromLocal(pois, poiTrees, regions);
	}

	private final PoiClient poiClient;

	@Override
	public String action() throws IOException {

		setAttribute("map", new Map("48.84650925911,2.3459243774", 13));

		final List<Pois> list = new ArrayList<Pois>();

		setAttribute("pois", list);

		for (final PoiGroup poiGroup : poiClient.getPois()) {

			list.add(new Pois(poiGroup));
		}

		return "pois.jsp";
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
