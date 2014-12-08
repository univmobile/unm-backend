// Author: Mauricio

package fr.univmobile.backend;

import static com.google.common.base.Preconditions.checkNotNull;
import static org.apache.commons.lang3.StringUtils.isBlank;

import java.io.IOException;

import javax.annotation.Nullable;

import org.apache.commons.lang3.StringUtils;

import fr.univmobile.backend.core.PoiCategory;
import fr.univmobile.backend.core.PoiCategoryBuilder;
import fr.univmobile.backend.core.PoiCategoryDataSource;
import fr.univmobile.commons.tx.Lock;
import fr.univmobile.commons.tx.TransactionException;
import fr.univmobile.commons.tx.TransactionManager;
import fr.univmobile.web.commons.HttpInputs;
import fr.univmobile.web.commons.HttpMethods;
import fr.univmobile.web.commons.HttpParameter;
import fr.univmobile.web.commons.HttpRequired;
import fr.univmobile.web.commons.PathVariable;
import fr.univmobile.web.commons.Paths;
import fr.univmobile.web.commons.Regexp;
import fr.univmobile.web.commons.View;

@Paths({ "poicategoriesmodify/${uid}" })
public class PoiCategoriesModifyController extends AbstractBackendController {

	@PathVariable("${uid}")
	private int getPoiCategoryUid() {

		return getPathIntVariable("${uid}");
	}

	public PoiCategoriesModifyController(final TransactionManager tx,
			final PoiCategoryDataSource poiCategories,
			final PoiCategoriesController poiCategoriesController) {

		this.poiCategories = checkNotNull(poiCategories, "poicategories");
		this.tx = checkNotNull(tx, "tx");
		this.poiCategoriesController = checkNotNull(poiCategoriesController,
				"poiCategoriesController");
	}

	private final TransactionManager tx;
	private final PoiCategoryDataSource poiCategories;
	private final PoiCategoriesController poiCategoriesController;

	@Override
	public View action() throws IOException, TransactionException {

		// 1.1 POI CATEGORY

		final PoiCategory poicategory;

		poicategory = poiCategories.getByUid(getPoiCategoryUid());

		setAttribute("poicategorymodify", poicategory);

		// 1.2 HTTP

		final PoiCategorymodify form = getHttpInputs(PoiCategorymodify.class);

		if (!form.isHttpValid()) {

			return new View("poicategorymodify.jsp");
		}

		// 2. APPLICATION VALIDATION

		final String uid = form.uid();

		final Lock lock = tx.acquireLock(5000, "poiscategories", uid);
		try {

			return poicategorymodify(lock, form);

		} finally {
			lock.release();
		}
	}

	private View poicategorymodify(final Lock lock, final PoiCategorymodify form)
			throws IOException, TransactionException {

		final String uid = form.uid();

		final PoiCategoryBuilder poicategory = poiCategories.create();

		boolean hasErrors = false;

		poicategory.setAuthorName(getDelegationUser().getAuthorName());

		if (!isBlank(form.uid()))
			poicategory.setUid(Integer.parseInt(uid));

		if (!isBlank(form.parentUid()))
			poicategory.setParentUid(Integer.parseInt(form.parentUid()));

		if (!form.parentUid().equals("(aucune)"))
			poicategory.setParentUid(Integer.parseInt(form.parentUid()));

		poicategory.setName(form.name());
		poicategory.setDescription(form.description());

		if (form.active() != null)
			poicategory.setActive(true);

		if (hasErrors) {

			setAttribute("poicategorymodify", poicategory);
			// Show the data in the view

			return new View("poicategorymodify.jsp");
		}

		lock.save(poicategory);

		lock.commit();

		return poiCategoriesController.action();
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
		@Regexp("[0-9]+")
		String uid();

		@HttpRequired
		@HttpParameter(trim = true)
		@Regexp("[0-9]+")
		String parentUid();

		@HttpRequired
		@HttpParameter(trim = true)
		@Regexp("[0-9]+")
		String externalUid();

		@HttpRequired
		@HttpParameter
		@Regexp(".*")
		String name();

		@HttpParameter
		String active();

		@HttpRequired
		@HttpParameter
		@Regexp(".*")
		String description();
	}
}
