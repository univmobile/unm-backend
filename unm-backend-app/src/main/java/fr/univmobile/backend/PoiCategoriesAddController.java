// Author: Mauricio

package fr.univmobile.backend;

import static com.google.common.base.Preconditions.checkNotNull;
import static org.apache.commons.lang3.StringUtils.isBlank;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.annotation.Nullable;

import fr.univmobile.backend.domain.Category;
import fr.univmobile.backend.domain.CategoryRepository;
import fr.univmobile.commons.tx.TransactionException;
import fr.univmobile.web.commons.HttpInputs;
import fr.univmobile.web.commons.HttpMethods;
import fr.univmobile.web.commons.HttpParameter;
import fr.univmobile.web.commons.HttpRequired;
import fr.univmobile.web.commons.PathVariable;
import fr.univmobile.web.commons.Paths;
import fr.univmobile.web.commons.Regexp;
import fr.univmobile.web.commons.View;

@Paths({ "poicategoriesadd", "poicategoriesadd/${id}" })
public class PoiCategoriesAddController extends AbstractBackendController {

	@PathVariable("${id}")
	private Long getParentCategoryId() {
		Long id;
		try {
			id = getPathLongVariable("${id}");
		} catch (Exception e) {
			id = null;
		}
		return id;
	}

	public PoiCategoriesAddController(
			final CategoryRepository categoryRepository,
			final PoiCategoriesController poiCategoriesController) {
		this.categoryRepository = checkNotNull(categoryRepository,
				"categoryRepository");
		checkNotNull(poiCategoriesController,
				"poiCategoriesController");
	}

	private CategoryRepository categoryRepository;
	
	@Override
	public View action() throws IOException, TransactionException {

		if (!getDelegationUser().isSuperAdmin()) {
			return sendError403("FORBIDDEN");
		}

		Category parentCategory = getParentCategoryId() != null ? categoryRepository.findOne(getParentCategoryId()) : null;
		if (parentCategory != null) {
			setAttribute("parentCategory", parentCategory);
		}

		// 1. CATEGORIES

		Iterable<Category> allCategories = categoryRepository.findAll();

		final List<Category> poicategories = new ArrayList<Category>();

		for (Category c : allCategories)
			poicategories.add(c);

		setAttribute("poicategories", poicategories);

		// 2. HTTP

		final PoiCategoryadd form = getHttpInputs(PoiCategoryadd.class);

		if (!form.isHttpValid()) {
			return new View("poicategoryadd.jsp");
		}

		// 3. APPLICATION VALIDATION

		return poicategoryadd(form);

	}

	private View poicategoryadd(final PoiCategoryadd form) {

		boolean hasErrors = false;

		Category poicategory = new Category();

		if (!form.parentCategory().equals("(aucune)")) {
			Long parentId = Long.parseLong(form.parentCategory());
			poicategory.setParent(categoryRepository.findOne(parentId));
		}

		poicategory.setName(form.name());
		if (!isBlank(form.name())) {
			if (categoryRepository.findByName(form.name()) != null) {
				hasErrors = true;
				setAttribute("err_duplicateName", true);
			}
		} else {
			hasErrors = true;
			setAttribute("err_poicategoryadd_name", true);
			setAttribute("err_incorrectFields", true);
		}

		if (form.active() != null)
			poicategory.setActive(true);

		if (form.description() != null)
			poicategory.setDescription(form.description());
		
		if (form.cid() != null && form.cid().trim().length() > 0) {
			try {
				poicategory.setApiParisId(Long.parseLong(form.cid()));
			} catch (java.lang.NumberFormatException e) {
				hasErrors = true;
				setAttribute("err_poicategoryadd_apiparisid", true);
				setAttribute("err_incorrectFields", true);
			}
		}
		
		if (hasErrors) {

			setAttribute("poicategoryadd", poicategory);
			// Show the data in the view

			return new View("poicategoryadd.jsp");
		}

		// 3. SAVE DATA

		// Otherwise, we’re clear: Save the data.

		categoryRepository.save(poicategory);

		if (poicategory.getParent() != null)
			setAttribute("parent", poicategory.getParent());
		return new View("poicategorymodify_redirect.jsp");

	}

	/**
	 * <ol>
	 * <li>
	 * By default, all parameters are required (that is, not blank) at the
	 * application level. Use the {@link Nullable}-annotation to specify an
	 * optional parameter.
	 * <li>
	 * By default, no parameter is required at the HTTP level. Use the
	 * {@link HttpRequired}-annotation to specify that a parameter must be
	 * present in the HTTP request for the form to be valid.
	 * </ol>
	 */
	@HttpMethods("POST")
	interface PoiCategoryadd extends HttpInputs {

		@HttpRequired
		@HttpParameter(trim = true)
		@Regexp("[0-9]+")
		String parentCategory();

		@HttpParameter
		String name();

		@HttpParameter
		String active();

		@HttpParameter
		String description();
		
		@HttpParameter
		String cid();
	}
}
