// Author: Mauricio

package fr.univmobile.backend;

import static com.google.common.base.Preconditions.checkNotNull;

import java.io.IOException;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.annotation.Nullable;

import fr.univmobile.backend.client.ClientException;
import fr.univmobile.backend.core.Poi;
import fr.univmobile.backend.core.PoiBuilder;
import fr.univmobile.backend.core.PoiCategory;
import fr.univmobile.backend.core.PoiCategoryDataSource;
import fr.univmobile.backend.core.PoiDataSource;
import fr.univmobile.backend.core.Region;
import fr.univmobile.backend.core.RegionDataSource;
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

@Paths({ "poismodify/${uid}" })
public class PoisModifyController extends AbstractBackendController {

	@PathVariable("${uid}")
	private int getPoiUid() {

		return getPathIntVariable("${uid}");
	}

	public PoisModifyController(final TransactionManager tx,
			final PoiDataSource pois, final PoisController poisController,
			final RegionDataSource regions,
			final PoiCategoryDataSource poicategories) {

		this.pois = checkNotNull(pois, "pois");
		this.tx = checkNotNull(tx, "tx");
		this.poisController = checkNotNull(poisController, "poisController");
		this.regions = checkNotNull(regions, "regions");
		this.poicategories = checkNotNull(poicategories, "poicategories");
	}

	private final TransactionManager tx;
	private final PoiDataSource pois;
	private final PoisController poisController;
	private final RegionDataSource regions;
	private final PoiCategoryDataSource poicategories;

	@Override
	public View action() throws IOException, TransactionException,
			ClientException {

		// CATEGORIES

		final Map<Integer, PoiCategory> dsPoiCategories = poicategories
				.getAllBy(Integer.class, "uid");

		final List<PoiCategory> poiCategoriesData = new ArrayList<PoiCategory>();

		for (final PoiCategory p : dsPoiCategories.values())
			if (String.valueOf(p.getUid()).startsWith(
					String.valueOf(PoiCategory.ROOT_UNIVERSITIES_CATEGORY_UID))
					&& p.getActive())
				poiCategoriesData.add(p);
		setAttribute("poiCategoriesData", poiCategoriesData);

		// REGIONS

		final Map<String, Region> dsRegions = regions.getAllBy(String.class,
				"uid");

		final List<Region> regionsData = new ArrayList<Region>();

		for (final Region r : dsRegions.values())
			regionsData.add(r);
		setAttribute("regionsData", regionsData);

		// 1.1 POI CATEGORY

		Poi poi;

		poi = pois.getByUid(getPoiUid());

		setAttribute("poimodify", poi);

		// 1.2 HTTP

		final Poimodify form = getHttpInputs(Poimodify.class);

		if (!form.isHttpValid()) {

			return new View("poimodify.jsp");
		}

		// 2. APPLICATION VALIDATION

		final Integer uid = form.uid();

		final Lock lock = tx.acquireLock(5000, "pois", uid);
		try {

			return poimodify(lock, form);

		} finally {
			lock.release();
		}
	}

	private View poimodify(final Lock lock, final Poimodify form)
			throws IOException, TransactionException {

		final Integer uid = form.uid();

		final PoiBuilder poi = pois.create();

		poi.setAuthorName(getDelegationUser().getAuthorName());

		poi.setUid(uid);
		poi.setName(form.name());

		NumberFormat nf = NumberFormat.getInstance();
		nf.setMaximumFractionDigits(2);

		if (!form.poiCategory().equals("(aucune)"))
			poi.setCategoryId(Integer.parseInt(form.poiCategory()));

		boolean hasErrors = false;

		double latitude = 0;
		double longitude = 0;

		try {
			latitude = Double.parseDouble(form.coordinates().split(",")[0]);
			longitude = Double.parseDouble(form.coordinates().split(",")[1]);
		} catch (Exception e) {
			hasErrors = true;
			setAttribute("err_coord_not_valid", true);
		}

		poi.setUniversityIds(form.university());
		poi.setFloors(form.floor());
		poi.setOpeningHours(form.openingHours());
		poi.setPhones(form.phone());
		// poi.setAddresses(form.address());
		poi.setFullAddresses(form.address());
		// or? fullAddress = floor + address + zipCode + city
		poi.setEmails(form.email());
		poi.setItineraries(form.itinerary());
		poi.setUrls(form.url());
		poi.setCoordinates(form.coordinates());

		if (form.active().equals("yes"))
			poi.setActive("true");
		else
			poi.setActive("false");

		poi.setLatitudes(String.valueOf(nf.format(latitude)));
		poi.setLongitudes(String.valueOf(nf.format(longitude)));

		if (hasErrors) {

			setAttribute("poimodify", poi);
			// Show the data in the view

			return new View("poimodify.jsp");
		}

		// 3. SAVE DATA

		// Otherwise, we’re clear: Save the data.

		lock.save(poi);

		lock.commit();

		return poisController.action();
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
	interface Poimodify extends HttpInputs {

		@HttpRequired
		@HttpParameter(trim = true)
		@Regexp("[0-9]+")
		int uid();

		@HttpRequired
		@HttpParameter
		@Regexp(".*")
		String name();

		@HttpParameter
		String university();

		@HttpParameter
		String floor();

		@HttpParameter
		String openingHours();

		@HttpParameter
		String phone();

		@HttpParameter
		String address();

		@HttpParameter
		String email();

		@HttpParameter
		String itinerary();

		@HttpParameter
		String url();

		@HttpParameter
		String coordinates();

		@HttpParameter
		String active();

		@HttpParameter
		String poiCategory();
	}
}
