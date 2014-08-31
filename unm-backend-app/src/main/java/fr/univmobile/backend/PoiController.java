package fr.univmobile.backend;

import java.io.IOException;

import fr.univmobile.backend.client.ClientException;
import fr.univmobile.backend.client.Poi;
import fr.univmobile.backend.client.PoiClient;
import fr.univmobile.backend.client.PoiClientFromLocal;
import fr.univmobile.backend.client.PoiNotFoundException;
import fr.univmobile.backend.core.PoiDataSource;
import fr.univmobile.backend.core.PoiTreeDataSource;
import fr.univmobile.backend.core.RegionDataSource;
import fr.univmobile.backend.core.UserDataSource;
import fr.univmobile.commons.tx.TransactionException;
import fr.univmobile.commons.tx.TransactionManager;
import fr.univmobile.web.commons.PageNotFoundException;
import fr.univmobile.web.commons.PathVariable;
import fr.univmobile.web.commons.Paths;
import fr.univmobile.web.commons.View;

@Paths({ "pois/${id}" })
public class PoiController extends AbstractBackendController {

	@PathVariable("${id}")
	private int getPoiId() {

		return getPathIntVariable("${id}");
	}

	public PoiController(final TransactionManager tx,
			final UserDataSource users, final RegionDataSource regions,
			final PoiDataSource pois, final PoiTreeDataSource poiTrees) {

		super(tx, users, regions, pois, poiTrees);
	}

	private PoiClient getPoiClient() {

		return new PoiClientFromLocal(getBaseURL(), pois, poiTrees, regions);
	}

	@Override
	public View action() throws IOException, TransactionException,
			ClientException, PageNotFoundException {

		final int id = getPoiId();

		final Poi poi;

		try {

			poi = getPoiClient().getPoi(id);

		} catch (final PoiNotFoundException e) {

			throw new PageNotFoundException();
		}

		setAttribute("poi", poi);
		
		// 9. END

		return new View("poi.jsp");
	}
}
