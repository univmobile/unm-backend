package fr.univmobile.backend.client;

import static com.google.common.base.Preconditions.checkNotNull;
import static org.apache.commons.lang3.StringUtils.isBlank;
import static org.apache.commons.lang3.StringUtils.substringAfter;
import static org.apache.commons.lang3.StringUtils.substringBefore;

import java.io.IOException;
import java.text.Normalizer;
import java.text.Normalizer.Form;
import java.util.Comparator;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

import javax.annotation.Nullable;
import javax.inject.Inject;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.avcompris.lang.NotImplementedException;

import fr.univmobile.backend.core.PoiDataSource;
import fr.univmobile.backend.core.PoiTree;
import fr.univmobile.backend.core.PoiTreeDataSource;
import fr.univmobile.backend.core.RegionDataSource;
import fr.univmobile.commons.DataBeans;

public class PoiClientFromLocal implements PoiClient {

	@Inject
	public PoiClientFromLocal(final PoiDataSource poiDataSource,
			final PoiTreeDataSource poitreeDataSource,
			final RegionDataSource regionDataSource) {

		this.poiDataSource = checkNotNull(poiDataSource, "poiDataSource");

		this.poitreeDataSource = checkNotNull(poitreeDataSource,
				"poitreeDataSource");

		this.regionDataSource = checkNotNull(regionDataSource,
				"regionDataSource");
	}

	private final PoiDataSource poiDataSource;
	private final PoiTreeDataSource poitreeDataSource;
	private final RegionDataSource regionDataSource;

	private static final Log log = LogFactory.getLog(PoiClientFromLocal.class);

	@Override
	public PoiGroup[] getPois() throws IOException {

		log.debug("getPois()...");

		final Map<String, fr.univmobile.backend.core.Region> dsRegions //
		= regionDataSource.getAllBy(String.class, "uid");

		// final Map<String, PoiTree> dsPoitree //
		// = poitreeDataSource.getAllBy(String.class, "uid");

		final PoiGroup[] poiGroups = new PoiGroup[dsRegions.size()];

		int i = 0;

		final Set<fr.univmobile.backend.core.Region> sortedSet = new TreeSet<fr.univmobile.backend.core.Region>(
				new Comparator<fr.univmobile.backend.core.Region>() {

					@Override
					public int compare(
							final fr.univmobile.backend.core.Region r1,
							final fr.univmobile.backend.core.Region r2) {

						final Form form = Form.NFD;

						final String n1 = Normalizer.normalize(r1.getLabel(),
								form);
						final String n2 = Normalizer.normalize(r2.getLabel(),
								form);

						return n1.compareTo(n2);
					}
				});

		sortedSet.addAll(dsRegions.values());

		int markerIndex = 0;

		for (final fr.univmobile.backend.core.Region dsRegion : sortedSet) {

			final MutablePoiGroup poiGroup = DataBeans //
					.instantiate(MutablePoiGroup.class) //
					.setGroupLabel("Région : " + dsRegion.getLabel());

			poiGroups[i] = poiGroup;

			++i;

			final String regionId = dsRegion.getUid();

			if (poitreeDataSource.isNullByUid(regionId)) { // TODO

				System.err.println("Cannot load PoiTree for region: "
						+ regionId);

				continue;
			}

			final PoiTree poiTree = poitreeDataSource.getByUid(regionId);

			for (final PoiTree root : poiTree.getRoots()) {

				final int poiUid = Integer.parseInt(root.getUid());

				final fr.univmobile.backend.core.Poi dsPoi = poiDataSource
						.getByUid(poiUid);

				final String coordinates = dsPoi.getCoordinates();
				final String latitude = substringBefore(coordinates, ",");
				final String longitude = substringAfter(coordinates, ",");

				if (isBlank(coordinates) || isBlank(latitude)
						|| isBlank(longitude)) {
					continue; // Skip empty POIs
				}

				final MutablePoi poi = DataBeans //
						.instantiate(MutablePoi.class) //
						.setId(poiUid) //
						.setName(dsPoi.getName()) //
						.setCoordinates(coordinates) //
						.setLatitude(latitude) //
						.setLongitude(longitude);

				if (dsPoi.getAddresses().length != 0) {
					poi.setAddress(dsPoi.getAddresses()[0].getFullAddress());
				}
				if (dsPoi.getUrls().length != 0) {
					poi.setUrl(dsPoi.getUrls()[0]);
				}
				if (dsPoi.getPhones().length != 0) {
					poi.setPhone(dsPoi.getPhones()[0]);
				}
				if (dsPoi.getFaxes().length != 0) {
					poi.setFax(dsPoi.getFaxes()[0]);
				}
				if (dsPoi.getAttachments().length != 0) {
					final String image = dsPoi.getAttachments()[0].getUrl();
					if (!image.startsWith("/upload")) {
						throw new NotImplementedException("Image URL: " + image);
					}
					poi.setImageUrl("${baseURL}" + image);
					poi.setImageWidth(100).setImageHeight(100); // TODO get img
				} else {
					poi.setImageWidth(0).setImageHeight(0);
				}

				poi.setMarkerType("green");
				poi.setMarkerIndex(Character
						.toString((char) ('A' + markerIndex)));

				markerIndex = (markerIndex + 1) % 26;

				poiGroup.addToPois(poi);
			}
		}

		return poiGroups;
	}

	private interface MutablePoiGroup extends PoiGroup {

		MutablePoiGroup setGroupLabel(String label);

		MutablePoiGroup addToPois(Poi poi);
	}

	private interface MutablePoi extends Poi {

		MutablePoi setId(int id);

		MutablePoi setName(String name);

		MutablePoi setAddress(@Nullable String address);

		MutablePoi setPhone(@Nullable String phone);

		MutablePoi setFloor(@Nullable String floor);

		MutablePoi setEmail(@Nullable String email);

		MutablePoi setFax(@Nullable String fax);

		MutablePoi setOpeningHours(@Nullable String openingHours);

		MutablePoi setItinerary(@Nullable String itinerary);

		MutablePoi setCoordinates(String coordinates);

		MutablePoi setLatitude(String latitude);

		MutablePoi setLongitude(String longitude);

		MutablePoi setUrl(@Nullable String url);

		MutablePoi setImageUrl(@Nullable String image);

		MutablePoi setImageWidth(int width);

		MutablePoi setImageHeight(int height);

		/**
		 * e.g. "green"
		 */
		MutablePoi setMarkerType(String markerType);

		/**
		 * e.g. "A"
		 */
		MutablePoi setMarkerIndex(String markerIndex);
	}
}
