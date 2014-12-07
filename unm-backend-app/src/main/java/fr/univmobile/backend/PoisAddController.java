package fr.univmobile.backend;

import static com.google.common.base.Preconditions.checkNotNull;
import static org.apache.commons.lang3.StringUtils.isBlank;

import java.io.IOException;
import java.sql.SQLException;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.annotation.Nullable;

import fr.univmobile.backend.client.ClientException;
import fr.univmobile.backend.core.PoiBuilder;
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
import fr.univmobile.web.commons.PageNotFoundException;
import fr.univmobile.web.commons.Paths;
import fr.univmobile.web.commons.Regexp;
import fr.univmobile.web.commons.View;

@Paths({ "poisadd" })
public class PoisAddController extends AbstractBackendController {

	public PoisAddController(final TransactionManager tx,
			final PoiDataSource pois, final PoisController poisController,
			final RegionDataSource regions) {

		this.tx = checkNotNull(tx, "tx");
		this.pois = checkNotNull(pois, "pois");
		this.poisController = checkNotNull(poisController, "poisController");
		this.regions = checkNotNull(regions, "regions");
	}

	private final TransactionManager tx;
	private final PoiDataSource pois;
	private final PoisController poisController;
	private final RegionDataSource regions;

	@Override
	public View action() throws IOException, SQLException,
			TransactionException, ClientException, PageNotFoundException {

		final Map<String, Region> dsRegions = regions.getAllBy(String.class,
				"uid");

		final List<Region> regionsData = new ArrayList<Region>();

		for (final Region r : dsRegions.values())
			regionsData.add(r);
		setAttribute("regionsData", regionsData);

		// 1 HTTP

		final Poiadd form = getHttpInputs(Poiadd.class);

		if (!form.isHttpValid()) {
			return new View("poiadd.jsp");
		}

		// 2. APPLICATION VALIDATION

		final String name = form.name();

		final Lock lock = tx.acquireLock(5000, "pois", name);
		try {

			return poiadd(lock, form);

		} finally {
			lock.release();
		}
	}

	private View poiadd(Lock lock, Poiadd form) throws IOException,
			TransactionException {

		final PoiBuilder poi = pois.create();

		poi.setAuthorName(getDelegationUser().getAuthorName());

		poi.setUid(form.uid());
		poi.setName(form.name());

		NumberFormat nf = NumberFormat.getInstance();
		nf.setMaximumFractionDigits(2);

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

		poi.setUniversities(form.university());
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

		poi.setLatitudes(String.valueOf(nf.format(latitude)));
		poi.setLongitudes(String.valueOf(nf.format(longitude)));

		if (form.active().equals("yes"))
			poi.setActive("true");
		else
			poi.setActive("false");

		String uid = String.valueOf(form.uid());
		if (!isBlank(uid)) {
			if (!pois.isNullByUid(Integer.parseInt(uid))) {
				hasErrors = true;
				setAttribute("err_duplicateUid", true);
			}
		}

		if (hasErrors) {

			setAttribute("poiadd", poi);
			// Show the data in the view

			return new View("poiadd.jsp");
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
	interface Poiadd extends HttpInputs {

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
	}
}
