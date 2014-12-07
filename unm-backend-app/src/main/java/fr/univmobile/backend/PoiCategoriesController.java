// Author: Mauricio

package fr.univmobile.backend;

import static com.google.common.base.Preconditions.checkNotNull;
import static fr.univmobile.commons.DataBeans.instantiate;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.TreeSet;

import javax.annotation.Nullable;

import fr.univmobile.backend.core.PoiCategory;
import fr.univmobile.backend.core.PoiCategoryDataSource;
import fr.univmobile.commons.tx.TransactionException;
import fr.univmobile.web.commons.Paths;
import fr.univmobile.web.commons.View;

@Paths({ "poicategories", "poicategories/" })
public class PoiCategoriesController extends AbstractBackendController {

	public PoiCategoriesController(final PoiCategoryDataSource poiCategories) {

		this.poiCategories = checkNotNull(poiCategories, "poicategories");
	}

	private PoiCategoryDataSource poiCategories;

	@Override
	public View action() throws IOException, TransactionException {

		getDelegationUser();

		final Map<Integer, PoiCategory> allCategories = poiCategories.getAllBy(
				Integer.class, "uid");

		final Map<Integer, PoiCategory> results = poiCategories.getAllBy(
				Integer.class, "uid");

		// 1. USERS INFO

		final PoiCategoriesInfo poiCategoriesInfo = instantiate(
				PoiCategoriesInfo.class) //
				.setCount(allCategories.size()) //
				.setContext("Tous les poi catégories") //
				.setResultCount(results.size());

		setAttribute("poiCategoriesInfo", poiCategoriesInfo);

		// 2. USERS DATA

		final List<PoiCategory> pc = new ArrayList<PoiCategory>();

		setAttribute("poicategories", pc);

		for (final Integer uid : new TreeSet<Integer>(allCategories.keySet())) {

			pc.add(allCategories.get(uid));
		}

		// 9. END

		return new View("poicategories.jsp");
	}
}

interface PoiCategoriesInfo {

	/**
	 * Total count of poiCategories in the DataBase.
	 */
	int getCount();

	PoiCategoriesInfo setCount(int count);

	/**
	 * e.g. "Tous les poi catégories"
	 */
	@Nullable
	String getContext();

	PoiCategoriesInfo setContext(String context);

	/**
	 * Count of poiCategories returned by the search.
	 */
	int getResultCount();

	PoiCategoriesInfo setResultCount(int count);
}
