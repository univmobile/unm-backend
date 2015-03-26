// Author: Mauricio

package fr.univmobile.backend;

import static com.google.common.base.Preconditions.checkNotNull;
import static org.apache.commons.lang3.StringUtils.isBlank;

import java.io.IOException;

import javax.annotation.Nullable;

import fr.univmobile.backend.domain.Category;
import fr.univmobile.backend.domain.CategoryRepository;
import fr.univmobile.web.commons.HttpInputs;
import fr.univmobile.web.commons.HttpMethods;
import fr.univmobile.web.commons.HttpParameter;
import fr.univmobile.web.commons.HttpRequired;
import fr.univmobile.web.commons.PathVariable;
import fr.univmobile.web.commons.Paths;
import fr.univmobile.web.commons.View;

@Paths({ "poicategoriesmodify/${id}" })
public class PoiCategoriesModifyController extends AbstractBackendController {

	@PathVariable("${id}")
	private long getPoiCategoryId() {

		return getPathLongVariable("${id}");
	}

	public PoiCategoriesModifyController(
			final CategoryRepository categoryRepository,
			final PoiCategoriesController poiCategoriesController) {

		this.categoryRepository = checkNotNull(categoryRepository,
				"categoryRepository");
		this.poiCategoriesController = checkNotNull(poiCategoriesController,
				"poiCategoriesController");
	}

	private final CategoryRepository categoryRepository;
	private final PoiCategoriesController poiCategoriesController;

	@Override
	public View action() throws IOException {

		if (getDelegationUser().isLibrarian()) {
			return sendError403("FORBIDDEN");
		}

		// 1. POI CATEGORY

		Category poicategory = categoryRepository.findOne(getPoiCategoryId());

		setAttribute("poicategorymodify", poicategory);

		// 2. HTTP

		final PoiCategorymodify form = getHttpInputs(PoiCategorymodify.class);

		if (!form.isHttpValid()) {

			return new View("poicategorymodify.jsp");
		}

		// 3. APPLICATION VALIDATION

		return poicategorymodify(form);
	}

	private View poicategorymodify(final PoiCategorymodify form) {

		Category poicategory = categoryRepository.findOne(getPoiCategoryId());

		boolean hasErrors = false;

		if (!isBlank(form.name())) {
			if (categoryRepository.findByName(form.name()) != null
					&& !poicategory.getName().equals(form.name())) {
				hasErrors = true;
				setAttribute("err_duplicateName", true);
				setAttribute("duplicateName", form.name());
			} else
				poicategory.setName(form.name());
		} else {
			hasErrors = true;
			setAttribute("err_poicategorymodify_name", true);
			setAttribute("err_incorrectFields", true);
		}

		if (form.active() != null)
			poicategory.setActive(true);
		else
			poicategory.setActive(false);

		if (form.description() != null)
			poicategory.setDescription(form.description());
		
		if (form.cid() != null && !form.cid().isEmpty())
			poicategory.setApiParisId(Long.parseLong(form.cid()));
		else poicategory.setApiParisId(null);

		if (hasErrors) {

			setAttribute("poicategorymodify", poicategory);
			// Show the data in the view

			return new View("poicategorymodify.jsp");
		}

		categoryRepository.save(poicategory);

		// return poiCategoriesController.action();

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
	interface PoiCategorymodify extends HttpInputs {

		@HttpRequired
		@HttpParameter(trim = true)
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
