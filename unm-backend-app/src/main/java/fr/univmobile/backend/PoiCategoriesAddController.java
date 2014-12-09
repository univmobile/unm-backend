// Author: Mauricio

package fr.univmobile.backend;

import static com.google.common.base.Preconditions.checkNotNull;
import static org.apache.commons.lang3.StringUtils.isBlank;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.TreeSet;

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
import fr.univmobile.web.commons.Paths;
import fr.univmobile.web.commons.Regexp;
import fr.univmobile.web.commons.View;

@Paths({ "poicategoriesadd" })
public class PoiCategoriesAddController extends AbstractBackendController {

	public PoiCategoriesAddController(final TransactionManager tx,
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

		// 1.1 POI CATEGORIES INFO

		final Map<Integer, PoiCategory> allCategories = poiCategories.getAllBy(
				Integer.class, "uid");

		final List<PoiCategory> pc = new ArrayList<PoiCategory>();

		setAttribute("poicategories", pc);

		for (final Integer uid : new TreeSet<Integer>(allCategories.keySet())) {
			pc.add(allCategories.get(uid));
		}

		// 1.2 HTTP

		final PoiCategoryadd form = getHttpInputs(PoiCategoryadd.class);

		if (!form.isHttpValid()) {

			return new View("poicategoryadd.jsp");
		}

		// 2. APPLICATION VALIDATION

		final String uid = form.uid();

		final Lock lock = tx.acquireLock(5000, "poiscategories", uid);
		try {

			return poicategoryadd(lock, form);

		} finally {
			lock.release();
		}
	}

	private View poicategoryadd(final Lock lock, final PoiCategoryadd form)
			throws IOException, TransactionException {

		final String uid = form.uid();

		final PoiCategoryBuilder poicategory = poiCategories.create();

		boolean hasErrors = false;

		poicategory.setAuthorName(getDelegationUser().getAuthorName());

		if (!isBlank(uid))
			if (StringUtils.isNumeric(uid.trim()))
				poicategory.setUid(Integer.parseInt(uid.trim()));
			else {
				setAttribute("err_poicategoryadd_uid", true);
				hasErrors = true;
			}

		if (!form.parentUid().equals("(aucune)"))
			poicategory.setParentUid(Integer.parseInt(form.parentUid()));
		else
			poicategory.setParentUid(99);

		if (!isBlank(form.externalUid()))
			if (StringUtils.isNumeric(form.externalUid().trim()))
				poicategory.setExternalUid(Integer.parseInt(form.externalUid()
						.trim()));
			else {
				setAttribute("err_poicategoryadd_externalUid", true);
				hasErrors = true;
			}

		if (!isBlank(form.name()))
			poicategory.setName(form.name());
		else {
			setAttribute("err_poicategoryadd_name", true);
			hasErrors = true;
		}

		if (form.active() != null)
			poicategory.setActive("true");

		if (form.description() != null)
			poicategory.setDescription(form.description());

		if (!isBlank(uid)) {
			if (!poiCategories.isNullByUid(Integer.parseInt(uid))) {
				hasErrors = true;
				setAttribute("err_duplicateUid", true);
			}
		}

		if (hasErrors) {

			setAttribute("poicategoryadd", poicategory);
			// Show the data in the view

			return new View("poicategoryadd.jsp");
		}

		// 3. SAVE DATA

		// Otherwise, we’re clear: Save the data.

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
	interface PoiCategoryadd extends HttpInputs {

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
