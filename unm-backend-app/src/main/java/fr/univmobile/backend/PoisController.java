package fr.univmobile.backend;

import static com.google.common.base.Preconditions.checkNotNull;
import static fr.univmobile.commons.DataBeans.instantiate;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.annotation.Nullable;

import fr.univmobile.backend.client.PoiClient;
import fr.univmobile.backend.client.PoiClientFromLocal;
import fr.univmobile.backend.client.PoiGroup;
import fr.univmobile.backend.core.PoiDataSource;
import fr.univmobile.backend.core.PoiTree;
import fr.univmobile.backend.core.PoiTreeDataSource;
import fr.univmobile.backend.core.RegionDataSource;
import fr.univmobile.backend.model.Pois;
import fr.univmobile.commons.tx.TransactionException;
import fr.univmobile.web.commons.Paths;
import fr.univmobile.web.commons.View;

@Paths({ "pois", "pois/" })
public class PoisController extends AbstractBackendController {

	public PoisController(final RegionDataSource regions,
			final PoiDataSource pois, final PoiTreeDataSource poiTrees) {

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

		final PoiGroup[] poiGroups = getPoiClient().getPois().getGroups();

		// 1. POIS INFO

		final PoiTree poiTree = poiTrees.getByUid("ile_de_france"); // TODO

		int resultCount = 0;

		for (final PoiGroup poiGroup : poiGroups) {

			resultCount += poiGroup.getPois().length;
		}

		final PoisInfo poisInfo = instantiate(PoisInfo.class) //
				.setCount(poiTree.sizeOfAllPois()) //
				.setContext("POIS de plus haut niveau") //
				.setResultCount(resultCount);

		setAttribute("poisInfo", poisInfo);

		// 2. POIS DATA

		final List<Pois> list = new ArrayList<Pois>();

		setAttribute("pois", list);

		for (final PoiGroup poiGroup : poiGroups) {

			list.add(new Pois(poiGroup));
		}

		// 9. END

		return new View("pois.jsp");
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
