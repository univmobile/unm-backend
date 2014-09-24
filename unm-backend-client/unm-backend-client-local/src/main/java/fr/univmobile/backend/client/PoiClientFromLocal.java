package fr.univmobile.backend.client;

import static com.google.common.base.Preconditions.checkNotNull;
import static org.apache.commons.lang3.StringUtils.isBlank;
import static org.apache.commons.lang3.StringUtils.substringAfter;
import static org.apache.commons.lang3.StringUtils.substringBefore;

import java.io.IOException;
import java.text.Normalizer;
import java.text.Normalizer.Form;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

import javax.annotation.Nullable;
import javax.inject.Inject;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.avcompris.lang.NotImplementedException;
import com.google.common.collect.Iterables;

import fr.univmobile.backend.client.Pois.MapInfo;
import fr.univmobile.backend.core.PoiDataSource;
import fr.univmobile.backend.core.PoiTree;
import fr.univmobile.backend.core.PoiTreeDataSource;
import fr.univmobile.backend.core.RegionDataSource;
import fr.univmobile.commons.DataBeans;

public class PoiClientFromLocal extends AbstractClientFromLocal implements
		PoiClient {

	@Inject
	public PoiClientFromLocal(final String baseURL,
			final PoiDataSource poiDataSource,
			final PoiTreeDataSource poitreeDataSource,
			final RegionDataSource regionDataSource) {

		super(baseURL);

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
	public Poi getPoi(int id) throws IOException, ClientException {

		if (log.isDebugEnabled()) {
			log.debug("getPoi(): " + id + "...");
		}

		final fr.univmobile.backend.core.Poi dsPoi = poiDataSource.getByUid(id);

		final MutablePoi poi = createPoiFromData(dsPoi);

		if (poi == null) {
			throw new PoiNotFoundException(id);
		}

		return poi;
	}

	private static double getDistance(final double lat1, final double lng1,
			final double lat2, final double lng2) {

		// TODO more accurate
		return Math.abs(lat1 - lat2) + Math.abs(lng1 - lng2);
	}

	@Override
	public Pois getPois(final double lat, final double lng) throws IOException {

		final MutablePois p = getPois();

		final PoiGroup[] poiGroups = p.getGroups();

		final Map<String, Poi[]> poiArrays = new HashMap<String, Poi[]>();

		for (final PoiGroup poiGroup : poiGroups) {

			final Poi[] pois = poiGroup.getPois();
			
			poiArrays.put(poiGroup.getGroupLabel(), pois);

			Arrays.sort(pois, new Comparator<Poi>() {

				@Override
				public int compare(final Poi p1, final Poi p2) {

					final double d1 = getDistance(
							Double.parseDouble(p1.getLatitude()),
							Double.parseDouble(p1.getLongitude()), lat, lng);

					final double d2 = getDistance(
							Double.parseDouble(p2.getLatitude()),
							Double.parseDouble(p2.getLongitude()), lat, lng);

					if (d1 < d2) {
						return -1;
					} else if (d1 > d2) {
						return 1;
					} else {
						return 0;
					}
				}
			});
		}

		Arrays.sort(poiGroups, new Comparator<PoiGroup>() {

			@Override
			public int compare(final PoiGroup g1, final PoiGroup g2) {

				final Poi[] pois1 = g1.getPois();
				final Poi[] pois2 = g2.getPois();

				if (pois1.length == 0 && pois2.length == 0) {
					return 0;
				} else if (pois1.length == 0) {
					return 1;
				} else if (pois2.length == 0) {
					return -1;
				}

				final double d1 = getDistance(
						Double.parseDouble(pois1[0].getLatitude()),
						Double.parseDouble(pois1[0].getLongitude()), lat, lng);

				final double d2 = getDistance(
						Double.parseDouble(pois2[0].getLatitude()),
						Double.parseDouble(pois2[0].getLongitude()), lat, lng);

				if (d1 < d2) {
					return -1;
				} else if (d1 > d2) {
					return 1;
				} else {
					return 0;
				}
			}
		});

		final MutableMapInfo mapInfo = DataBeans
				.instantiate(MutableMapInfo.class) //
				.setPreferredZoom(10) //
				.setLat(lat) //
				.setLng(lng);

		final MutablePois pois = DataBeans.instantiate(MutablePois.class) //
				.setMapInfo(mapInfo);

		for (final PoiGroup poiGroup : poiGroups) {

			final String groupLabel = poiGroup.getGroupLabel();

			final MutablePoiGroup poiGroup2 = DataBeans.instantiate(
					MutablePoiGroup.class).setGroupLabel(groupLabel); //

			pois.addToGroups(poiGroup2);

			for (final Poi poi : poiArrays.get(groupLabel)) {
				poiGroup2.addToPois(poi);
			}
		}

		return pois;
	}

	@Override
	public MutablePois getPois() throws IOException {

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

				final MutablePoi poi = createPoiFromData(dsPoi);

				if (poi == null) {
					continue; // Skip empty POIs
				}

				poi.setMarkerType("green");
				poi.setMarkerIndex(Character
						.toString((char) ('A' + markerIndex)));

				markerIndex = (markerIndex + 1) % 26;

				poiGroup.addToPois(poi);
			}
		}

		final MutablePois pois = DataBeans.instantiate(MutablePois.class);

		for (final PoiGroup poiGroup : poiGroups) {

			pois.addToGroups(poiGroup);
		}

		return pois;
	}

	@Nullable
	private MutablePoi createPoiFromData(
			final fr.univmobile.backend.core.Poi dsPoi) {

		final int poiUid = dsPoi.getUid();

		final String coordinates = dsPoi.getCoordinates();
		final String latitude = substringBefore(coordinates, ",");
		final String longitude = substringAfter(coordinates, ",");

		if (isBlank(coordinates) || isBlank(latitude) || isBlank(longitude)) {
			return null;
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
			poi.setImageUrl(composeURL(image));
			poi.setImageWidth(100).setImageHeight(100); // TODO get img
		} else {
			poi.setImageWidth(0).setImageHeight(0);
		}

		poi.setCommentsUrl(composeURL("/json/comments/poi" + poiUid));

		// UNIVERSITIES

		final String[] dsUniversities = dsPoi.getUniversities();

		final List<String> universityIds = new ArrayList<String>();

		for (final String dsUniversity : dsUniversities) {

			universityIds.add(dsUniversity);
		}

		poi.setUniversityIds(Iterables.toArray(universityIds, String.class));

		// END

		return poi;
	}

	private interface MutablePois extends Pois {

		MutablePois setMapInfo(MapInfo mapInfo);

		MutablePois addToGroups(PoiGroup poiGroup);
	}

	private interface MutableMapInfo extends MapInfo {

		MutableMapInfo setPreferredZoom(int zoom);

		MutableMapInfo setLat(double lat);

		MutableMapInfo setLng(double lat);
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

		MutablePoi setCommentsUrl(String commentsUrl);

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

		MutablePoi setUniversityIds(String[] universityIds);
	}
}
