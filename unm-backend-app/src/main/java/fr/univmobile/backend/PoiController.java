package fr.univmobile.backend;

import static fr.univmobile.commons.DataBeans.instantiate;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import fr.univmobile.backend.client.PoiClient;
import fr.univmobile.backend.client.PoiClientFromLocal;
import fr.univmobile.backend.client.PoiGroup;
import fr.univmobile.backend.core.PoiDataSource;
import fr.univmobile.backend.core.PoiTree;
import fr.univmobile.backend.core.PoiTreeDataSource;
import fr.univmobile.backend.core.RegionDataSource;
import fr.univmobile.backend.core.UserDataSource;
import fr.univmobile.backend.model.Pois;
import fr.univmobile.commons.tx.TransactionException;
import fr.univmobile.commons.tx.TransactionManager;
import fr.univmobile.web.commons.Paths;
import fr.univmobile.web.commons.View;

@Paths({ "pois", "pois/" })
public class PoiController extends AbstractBackendController {

	public PoiController(final TransactionManager tx,
			final UserDataSource users, final RegionDataSource regions,
			final PoiDataSource pois, final PoiTreeDataSource poiTrees) {

		super(tx, users, regions, pois, poiTrees);
	}

	private PoiClient getPoiClient() {

		return new PoiClientFromLocal(getBaseURL(), pois, poiTrees, regions);
	}

	@Override
	public View action() throws IOException, TransactionException {

		// 1. POIS INFO
		
		final PoiTree poiTree = poiTrees.getByUid("ile_de_france"); // TODO

		final PoisInfo pois = instantiate(PoisInfo.class).setCount(
				poiTree.sizeOfAllPois());

		setAttribute("poisInfo", pois);

		// 2. POIS DATA
		
		final List<Pois> list = new ArrayList<Pois>();

		setAttribute("pois", list);

		for (final PoiGroup poiGroup : getPoiClient().getPois()) {

			list.add(new Pois(poiGroup));
		}

		// 9. END
		
		return new View("pois.jsp");
	}
}
