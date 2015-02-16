// Author: Mauricio

package fr.univmobile.backend;

import static com.google.common.base.Preconditions.checkNotNull;
import static fr.univmobile.commons.DataBeans.instantiate;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.annotation.Nullable;

import fr.univmobile.backend.domain.Category;
import fr.univmobile.backend.domain.CategoryRepository;
import fr.univmobile.web.commons.PathVariable;
import fr.univmobile.web.commons.Paths;
import fr.univmobile.web.commons.View;

@Paths({ "poicategories", "poicategories/", "poicategories/${id}" })
public class PoiCategoriesController extends AbstractBackendController {

	@PathVariable("${id}")
	private long getPoiCategoryId() {

		return getPathLongVariable("${id}");
	}

	private boolean hasPoiCategoryContext() {

		return hasPathStringVariable("${id}");
	}

	public PoiCategoriesController(final CategoryRepository categoryRepository) {
		this.categoryRepository = checkNotNull(categoryRepository,
				"categoryRepository");
	}

	private CategoryRepository categoryRepository;

	@Override
	public View action() throws IOException {

		if (getDelegationUser().isLibrarian()) {
			return sendError403("FORBIDDEN");
		}

		// 1. CATEGORIES DATA

		Category parent;

		if (hasPoiCategoryContext()) {

			Long cId = getPoiCategoryId();
			parent = categoryRepository.findOne(cId);
			
			setAttribute("has_father", true);
			setAttribute("father", parent);
		} else
			parent = null;

		Iterable<Category> allCategories = categoryRepository
				.findByParent(parent);

		List<Category> poicategories = new ArrayList<Category>();

		int size = 0;
		for (Category c : allCategories) {
			poicategories.add(c);
			size++;
		}

		setAttribute("poicategories", poicategories);

		// 2. USERS INFO

		final PoiCategoriesInfo poiCategoriesInfo = instantiate(
				PoiCategoriesInfo.class) //
				.setCount(size) //
				.setContext("Toutes les catégories de POI") //
				.setResultCount(size);

		setAttribute("poiCategoriesInfo", poiCategoriesInfo);

		// 3. END

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
