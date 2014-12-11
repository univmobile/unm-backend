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
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

import javax.annotation.Nullable;
import javax.inject.Inject;

import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.avcompris.lang.NotImplementedException;
import com.google.common.collect.Iterables;

import fr.univmobile.backend.client.Pois.MapInfo;
import fr.univmobile.backend.core.PoiCategory;
import fr.univmobile.backend.core.PoiDataSource;
import fr.univmobile.backend.core.RegionDataSource;
import fr.univmobile.commons.DataBeans;

public class PoiClientFromLocal extends AbstractClientFromLocal implements
		PoiClient {

	@Inject
	public PoiClientFromLocal(final String baseURL,
			final PoiDataSource poiDataSource,
			final RegionDataSource regionDataSource) {

		super(baseURL);

		this.poiDataSource = checkNotNull(poiDataSource, "poiDataSource");

		this.regionDataSource = checkNotNull(regionDataSource,
				"regionDataSource");
	}

	private final PoiDataSource poiDataSource;
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

    private static boolean isPoiNear(double lat, double lon, Poi poi, double metersAway) {

    	if (poi.getCoordinates() == null || !poi.getCoordinates().contains(",")) {
    		return false;
    	}
    		
    	String[] coordsSplited = poi.getCoordinates().split(",");
    	double poiLat = Double.parseDouble(coordsSplited[0]);
    	double poiLng = Double.parseDouble(coordsSplited[1]);

        if (getDistance(poiLat, poiLng, lat, lon) * 1000 <= metersAway) {
            return true ;
        } else {
            return false;
        }

    }

    private static double getDistance(final double lat1, final double lon1,
            final double lat2, final double lon2) {

        // Previous implementation
        // private static double getDistance(final double lat1, final double lng1,    final double lat2, final double lng2)
        // TODO more accurate
        // return Math.abs(lat1 - lat2) + Math.abs(lng1 - lng2);
        double r = 6371; // Radius of the earth in km
        double dLat = deg2rad(lat2-lat1);  // deg2rad below
        double dLon = deg2rad(lon2-lon1);
        double a =
                (double) (Math.sin(dLat/2) * Math.sin(dLat/2) +
                Math.cos(deg2rad(lat1)) * Math.cos(deg2rad(lat2)) *
                Math.sin(dLon/2) * Math.sin(dLon/2))
                ;
        double c = (double) (2 * Math.atan2(Math.sqrt(a), Math.sqrt(1-a)));
        double d = r * c; // Distance in km
        return d;
    }

    private static double deg2rad(double deg) {
      return (double) (deg * (Math.PI/180));
    }

    @Override
    public Pois getNearestPois(double lat, double lon, double metersAway) throws IOException {
		final MutablePoiGroup nearestsPoiGroup = DataBeans.instantiate(MutablePoiGroup.class).setGroupLabel("pois");        
        final MutablePois p = getPois();
        Poi[] poisInGroup;

        final PoiGroup[] poiGroups = p.getGroups();


        for (final PoiGroup poiGroup : poiGroups) {

            poisInGroup = poiGroup.getPois();

            for (final Poi poiInGroup : poisInGroup) {
                if ((poiInGroup.getCategory() == null || poiInGroup.getCategory() != PoiCategory.ROOT_IMAGE_MAP_CATEGORY_UID) && isPoiNear(lat, lon, poiInGroup, metersAway)) {
                	nearestsPoiGroup.addToPois(poiInGroup);
                }
            }

        }

        final MutablePois pois = DataBeans.instantiate(MutablePois.class);
		pois.addToGroups(nearestsPoiGroup);

		return pois;
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
	public MutablePois getPoisByRegion(String regionUid) throws IOException {
		return getPoisPerRegionAndCategory(regionUid, null, null, null, false);
	}

	@Override
	public MutablePois getPoisByCategory(int categoryId) throws IOException {
		return getPoisPerRegionAndCategory(null, categoryId, null, null, false);
	}

	@Override
	public MutablePois getPoisByRegionAndCategory(String regionUid, Integer categoryId) throws IOException {
		return getPoisPerRegionAndCategory(regionUid, categoryId, null, null, false);
	}

	@Override
	public MutablePois getPoisByRegionAndCategory(String regionUid, Integer categoryId, int[] excludeCategories, String filterByUniversity, boolean completeWholeTree) throws IOException {
		return getPoisPerRegionAndCategory(regionUid, categoryId, excludeCategories, filterByUniversity, completeWholeTree);
	}

	@Override
	public MutablePois getPois() throws IOException {
		return getPoisPerRegionAndCategory(null, null, null, null, false);
	}

	private MutablePois getPoisPerRegionAndCategory(String filterRegionUid, Integer filterCategoryId, int[] excludeCategories, String filterByUniversity, boolean completeWholeTree) throws IOException {

		log.debug("getPois()...");

		final Map<String, fr.univmobile.backend.core.Region> dsRegions;
		
		if (filterRegionUid == null) {
			dsRegions = regionDataSource.getAllBy(String.class, "uid");
		} else {
			dsRegions = new HashMap<String, fr.univmobile.backend.core.Region>();
			fr.univmobile.backend.core.Region filterRegion = regionDataSource.getByUid(filterRegionUid);
			if (filterRegion != null) {
				dsRegions.put(filterRegionUid, filterRegion);
			}
		}

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

		final Map<Integer, fr.univmobile.backend.core.Poi> allPois = poiDataSource
				.getAllBy(Integer.class, "uid");
		
		final List<fr.univmobile.backend.core.Poi> myPois = new ArrayList<fr.univmobile.backend.core.Poi>();
		for (final Integer uid : new TreeSet<Integer>(allPois.keySet())) {
			fr.univmobile.backend.core.Poi tmpPoi = allPois.get(uid);
			
			if (complyWithUniversity(tmpPoi, filterByUniversity) && !isInCategories(tmpPoi, excludeCategories)) {
				if (filterCategoryId == null) {
					myPois.add(tmpPoi);
				} else {
					if (tmpPoi.getCategoryId() == filterCategoryId) {				
						myPois.add(tmpPoi);
					}
				}
			}
		}

		Map<Integer, fr.univmobile.backend.core.Poi> alreadySelectedPois = new HashMap<Integer, fr.univmobile.backend.core.Poi>();
		Set<Integer> startingPoiIds = new HashSet<Integer>();
		
		for (final fr.univmobile.backend.core.Region dsRegion : sortedSet) {

			final MutablePoiGroup poiGroup = DataBeans //
					.instantiate(MutablePoiGroup.class) //
					.setGroupLabel("Région : " + dsRegion.getLabel());

			poiGroups[i] = poiGroup;

			++i;

			fr.univmobile.backend.core.University[] universities = dsRegion
					.getUniversities();

			for (fr.univmobile.backend.core.Poi p : myPois) {

				final fr.univmobile.backend.core.Poi dsPoi = poiDataSource
						.getByUid(p.getUid());

				if (universities.length > 0) {
					boolean belongs = false;
					for (int j = 0; j < universities.length; j++)
						if (dsPoi.getUniversityIds().length > 0)
							if (universities[j].getId().equals(
									dsPoi.getUniversityIds()[0]))
								belongs = true;

					if (belongs == true) {

						final MutablePoi poi = createPoiFromData(dsPoi);

						if (poi == null) {
							continue; // Skip empty POIs
						} else
							poi.setRegion(dsRegion.getUid());

						/**
						 * TODO Check with the MarketType is hardcoded to green
						 */
						poi.setMarkerType("green");
						poi.setMarkerIndex(Character
								.toString((char) ('A' + markerIndex)));

						markerIndex = (markerIndex + 1) % 26;

						poiGroup.addToPois(poi);
						if (completeWholeTree) {
							alreadySelectedPois.put(p.getUid(), p);
							if (p.getParentUid() != 0 && p.getParentUid() != p.getUid()) {
								startingPoiIds.add(p.getParentUid());
							}
						}
					}
				}
			}
		}

		final MutablePois pois = DataBeans.instantiate(MutablePois.class);

		for (final PoiGroup poiGroup : poiGroups) {

			pois.addToGroups(poiGroup);
		}
		
		if (completeWholeTree) {
			pois.addToGroups(fillWholeTree(startingPoiIds, allPois, alreadySelectedPois));
		}

		return pois;
	}

	private boolean complyWithUniversity(fr.univmobile.backend.core.Poi poi, String universityId) {
		return universityId == null || Arrays.asList(poi.getUniversityIds()).contains(universityId);
	}
	
	private boolean isInCategories(fr.univmobile.backend.core.Poi poi, int[] categories) {
		if (categories != null) {
			for (int i = 0; i < categories.length; i++) {
				if (poi.getCategoryId() == categories[i]) {
					return true;
				}
			}
		}
		return false;
	}
	
	private MutablePoiGroup fillWholeTree(Set<Integer> startingPoiIds, 
			Map<Integer, fr.univmobile.backend.core.Poi> allPois, 
			Map<Integer, fr.univmobile.backend.core.Poi> alreadySelectedPois) {
		
		Map<Integer, fr.univmobile.backend.core.Poi> filled = new HashMap<Integer, fr.univmobile.backend.core.Poi>();
		int currentId;
		fr.univmobile.backend.core.Poi currentPoi;
		
		for (Integer uid : startingPoiIds) {
			currentId = uid;
			while (currentId != 0) {
				currentPoi = allPois.get(currentId);
				if (!alreadySelectedPois.containsKey(currentId)) {
					filled.put(currentId, currentPoi);
				}
				currentId = currentPoi.getParentUid();
			}
		}
		
		
		MutablePoi poi;
		fr.univmobile.backend.core.Poi dsPoi;
		MutablePoiGroup poiGroup = DataBeans.instantiate(MutablePoiGroup.class).setGroupLabel("filledTree");
		
		Iterator<fr.univmobile.backend.core.Poi> filledIter = filled.values().iterator();
		while (filledIter.hasNext()) {
			dsPoi = poiDataSource.getByUid(filledIter.next().getUid());
			poi = createPoiFromData(dsPoi);
			//poi.setRegion(dsRegion.getUid());
			poi.setMarkerType("green");
			poi.setMarkerIndex("A");
			poiGroup.addToPois(poi);
		}
		return poiGroup;
	}
	
	@Nullable
	private MutablePoi createPoiFromData(
			final fr.univmobile.backend.core.Poi dsPoi) {

		final int poiUid = dsPoi.getUid();

		final String coordinates = dsPoi.getCoordinates();
		final String latitude = substringBefore(coordinates, ",");
		final String longitude = substringAfter(coordinates, ",");

		if (isBlank(coordinates) || isBlank(latitude) || isBlank(longitude)) {
			// return null;
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

		// Author: Mauricio (begin)

		if (dsPoi.getFloors().length != 0) {
			poi.setFloor(dsPoi.getFloors()[0]);
		}
		if (dsPoi.getOpeningHours().length != 0) {
			poi.setOpeningHours(dsPoi.getOpeningHours()[0]);
		}
		if (dsPoi.getEmails().length != 0) {
			poi.setEmail(dsPoi.getEmails()[0]);
		}
		if (dsPoi.getItineraries().length != 0) {
			poi.setItinerary(dsPoi.getItineraries()[0]);
		}

		if (dsPoi.getCategoryId() > 0) {
			poi.setCategory(dsPoi.getCategoryId());
		}
		// Author: Mauricio (end)

		if (dsPoi.getParentUid() > 0) {
			poi.setParentUid(dsPoi.getParentUid());
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

		final String[] dsUniversities = dsPoi.getUniversityIds();

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

		MutablePoi setRegion(String region);

		MutablePoi setAddress(@Nullable String address);

		MutablePoi setPhone(@Nullable String phone);

		MutablePoi setFloor(@Nullable String floor);

		MutablePoi setEmail(@Nullable String email);

		MutablePoi setFax(@Nullable String fax);

		MutablePoi setOpeningHours(@Nullable String openingHours);

		MutablePoi setItinerary(@Nullable String itinerary);
		
		MutablePoi setCategory(@Nullable Integer categoryId);

		MutablePoi setParentUid(@Nullable Integer parentUid);

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
