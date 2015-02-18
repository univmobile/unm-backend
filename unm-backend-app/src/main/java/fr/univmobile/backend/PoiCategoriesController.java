// Author: Mauricio

package fr.univmobile.backend;

import static com.google.common.base.Preconditions.checkNotNull;
import static fr.univmobile.commons.DataBeans.instantiate;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.annotation.Nullable;

import antlr.StringUtils;
import fr.univmobile.backend.PoiCategoriesAddController.PoiCategoryadd;
import fr.univmobile.backend.domain.Category;
import fr.univmobile.backend.domain.CategoryRepository;
import fr.univmobile.web.commons.HttpInputs;
import fr.univmobile.web.commons.HttpMethods;
import fr.univmobile.web.commons.HttpRequired;
import fr.univmobile.web.commons.PathVariable;
import fr.univmobile.web.commons.Paths;
import fr.univmobile.web.commons.View;

@Paths({ "poicategories", "poicategories/", "poicategories/${value}" })
public class PoiCategoriesController extends AbstractBackendController {

	@PathVariable("${value}")
	private String getPoiCategoryId() {

		return getPathStringVariable("${value}");
	}

	private boolean hasPoiCategoryContext() {

		return hasPathStringVariable("${value}");
	}

	public PoiCategoriesController(final CategoryRepository categoryRepository) {
		this.categoryRepository = checkNotNull(categoryRepository,
				"categoryRepository");
	}

	private CategoryRepository categoryRepository;

	public static boolean isLong(String s) {
		try {
			Long.parseLong(s);
		} catch (NumberFormatException e) {
			return false;
		}
		// only got here if we didn't return false
		return true;
	}

	@Override
	public View action() throws IOException {

		if (getDelegationUser().isLibrarian()) {
			return sendError403("FORBIDDEN");
		}

		// System.out.println(getPathStringVariable("${id}"));

		// 1. CATEGORIES DATA

		Category parent = null;

		int size = 0;
		
		List<Category> poicategories;

		if (hasPoiCategoryContext()) {

			String value = getPoiCategoryId();

			if (isLong(value)) {

				Long cId = Long.parseLong(value);
				parent = categoryRepository.findOne(cId);

				setAttribute("has_father", true);
				setAttribute("father", parent);
				
				Iterable<Category> allCategories = categoryRepository
						.findByParent(parent);

				poicategories = new ArrayList<Category>();

				for (Category c : allCategories) {
					poicategories.add(c);
					size++;
				}
			} else {
				
				poicategories = categoryRepository.findByNameContainingOrderByNameAsc(value);
				
			}
		} else {

			Iterable<Category> allCategories = categoryRepository
					.findByParent(parent);

			poicategories = new ArrayList<Category>();

			for (Category c : allCategories) {
				poicategories.add(c);
				size++;
			}
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
