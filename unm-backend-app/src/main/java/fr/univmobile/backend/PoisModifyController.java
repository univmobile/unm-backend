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
import fr.univmobile.backend.domain.Poi;
import fr.univmobile.backend.domain.PoiRepository;
import fr.univmobile.backend.domain.Region;
import fr.univmobile.backend.domain.RegionRepository;
import fr.univmobile.backend.domain.UniversityRepository;
import fr.univmobile.backend.domain.User;
import fr.univmobile.web.commons.HttpInputs;
import fr.univmobile.web.commons.HttpMethods;
import fr.univmobile.web.commons.HttpParameter;
import fr.univmobile.web.commons.HttpRequired;
import fr.univmobile.web.commons.PathVariable;
import fr.univmobile.web.commons.Paths;
import fr.univmobile.web.commons.Regexp;
import fr.univmobile.web.commons.View;

@Paths({ "poismodify/${id}" })
public class PoisModifyController extends AbstractBackendController {

	@PathVariable("${id}")
	private long getPoiId() {

		return getPathLongVariable("${id}");
	}

	public PoisModifyController(final PoiRepository poiRepository,
			final CategoryRepository categoryRepository,
			final RegionRepository regionRepository,
			final UniversityRepository universityRepository,
			final PoisController poisController) {
		this.poiRepository = checkNotNull(poiRepository, "poiRepository");
		this.categoryRepository = checkNotNull(categoryRepository,
				"categoryRepository");
		this.regionRepository = checkNotNull(regionRepository,
				"regionRepository");
		this.universityRepository = checkNotNull(universityRepository,
				"universityRepository");
		this.poisController = checkNotNull(poisController, "poisController");
	}

	private PoiRepository poiRepository;
	private CategoryRepository categoryRepository;
	private RegionRepository regionRepository;
	private UniversityRepository universityRepository;
	private PoisController poisController;

	@Override
	public View action() throws IOException {

		Long uId = poiRepository.findOne(getPoiId()).getUniversity().getId();

		if (getDelegationUser().isAdmin())
			if (getDelegationUser().getUniversity().getId() != uId)
				return sendError400();
				//return sendError403("Vous n'êtes pas autorisé sur ce POI");

		// CATEGORIES

		List<Category> categories = categoryRepository
				.findByLegacyStartingWithOrderByLegacyAsc(Category
						.getPlansLegacy());

		setAttribute("poiCategoriesData", categories);

		// REGIONS

		Iterable<Region> allRegions = regionRepository.findAll();

		List<Region> regions = new ArrayList<Region>();

		for (Region r : allRegions)
			regions.add(r);

		setAttribute("regionsData", regions);

		// USER

		User user = getDelegationUser();

		setAttribute("user", user);

		// 1. POI

		Poi poi = poiRepository.findOne(getPoiId());

		setAttribute("poimodify", poi);

		// 2. HTTP

		final Poimodify form = getHttpInputs(Poimodify.class);

		if (!form.isHttpValid()) {

			return new View("poimodify.jsp");
		}

		// 3. APPLICATION VALIDATION

		return poimodify(form, poi);
	}

	private View poimodify(final Poimodify form, Poi poi) {

		boolean hasErrors = false;

		poi.setName(form.name());
		if (isBlank(form.name())) {
			hasErrors = true;
			setAttribute("err_poimodify_name", true);
			setAttribute("err_incorrectFields", true);
		}

		poi.setCategory(categoryRepository.findByName(form.category()));
		if (isBlank(form.category())) {
			hasErrors = true;
			setAttribute("err_poimodify_category", true);
			setAttribute("err_incorrectFields", true);
		}

		poi.setAddress(form.address());
		poi.setCity(form.city());
		poi.setCountry(form.country());
		poi.setEmail(form.email());
		poi.setFloor(form.floor());
		poi.setItinerary(form.itinerary());

		try {
			if (form.lat() != null && form.lat().length() > 0
					&& form.lat() != null && form.lat().length() > 0) {
				Double lat = Double.parseDouble(form.lat().trim());
				poi.setLat(lat);
			} else {
				poi.setLat(null);
			}
		} catch (NumberFormatException e) {
			hasErrors = true;		
			setAttribute("err_coordinates", true);
		}
		
		try {
			if (form.lng() != null && form.lng().length() > 0
					&& form.lng() != null && form.lng().length() > 0) {
				Double lng = Double.parseDouble(form.lng().trim());
				poi.setLng(lng);
			} else {
				// TODO use Geocoding to try to get the coordinates from the address field.
				// eg. http://code.google.com/p/geocoder-java/
				poi.setLng(null);
			}
		} catch (NumberFormatException e) {
			hasErrors = true;		
			setAttribute("err_coordinates", true);
		}

		poi.setOpeningHours(form.openingHours());
		poi.setPhones(form.phones());
		poi.setUniversity(universityRepository.findByTitle(form.university()));
		poi.setUrl(form.url());
		poi.setZipcode(form.zipcode());

		if (form.active().equals("yes"))
			poi.setActive(true);
		else
			poi.setActive(false);

		if (hasErrors) {

			setAttribute("poimodify", poi);
			// Show the data in the view

			return new View("poimodify.jsp");
		}

		// 3. SAVE DATA

		// Otherwise, we’re clear: Save the data.

		poiRepository.save(poi);

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
		String phones();

		@HttpParameter
		String address();

		@HttpParameter
		String email();

		@HttpParameter
		String itinerary();

		@HttpParameter
		String url();

		@HttpParameter
		String active();

		@HttpParameter
		String category();

		@HttpParameter
		String lat();

		@HttpParameter
		String lng();

		@HttpParameter
		String city();

		@HttpParameter
		String country();

		@HttpParameter
		String zipcode();
		
		@HttpParameter
		String description();
	}
}
