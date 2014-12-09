package fr.univmobile.backend.client.json;

import static com.google.common.base.Preconditions.checkNotNull;

import java.io.IOException;

import javax.inject.Inject;
import javax.inject.Named;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import fr.univmobile.backend.client.Poi;
import fr.univmobile.backend.client.PoiClient;
import fr.univmobile.backend.client.PoiGroup;
import fr.univmobile.backend.client.Pois;
import fr.univmobile.backend.client.Pois.MapInfo;
import fr.univmobile.backend.json.JSONList;
import fr.univmobile.backend.json.JSONMap;

public class PoiJSONClientImpl implements PoiJSONClient {

	@Inject
	public PoiJSONClientImpl(@Named("PoiJSONClientImpl")//
			final PoiClient poiClient) {

		this.poiClient = checkNotNull(poiClient, "poiClient");
	}

	private final PoiClient poiClient;

	private static final Log log = LogFactory.getLog(PoiJSONClientImpl.class);

	@Override
	public String getPoisJSON() throws IOException {

		log.debug("getPoisJSON()...");

		final Pois p = poiClient.getPois();

		return poisJSON(p);
	}

	@Override
	public String getPoisByRegionJSON(String regionId) throws IOException {

		log.debug("getPoisByRegionJSON()...");

		final Pois p = poiClient.getPoisByRegion(regionId);

		return poisJSON(p);
	}

	@Override
	public String getPoisByCategoryJSON(int categoryId) throws IOException {
		log.debug("getPoisByCategoryJSON()...");

		final Pois p = poiClient.getPoisByCategory(categoryId);

		return poisJSON(p);
	}

	@Override
	public String getPoisByRegionAndCategoryJSON(String regionId, Integer categoryId) throws IOException {
		log.debug("getPoisByCategoryJSON()...");

		final Pois p = poiClient.getPoisByRegionAndCategory(regionId, categoryId);

		return poisJSON(p);
	}

	@Override
	public String getPoisJSON(final double lat, final double lng)
			throws IOException {

		log.debug("getPoisJSON():" + lat + "," + lng + "...");

		final Pois p = poiClient.getPois(lat, lng);

		return poisJSON(p);
	}

	private static String poisJSON(final Pois p) {

		final JSONMap json = new JSONMap();

		final MapInfo mapInfo = p.getMapInfo();

		if (mapInfo != null) {

			final JSONMap m = new JSONMap() // ;
					.put("zoom", mapInfo.getPreferredZoom()) //
					.put("lat", mapInfo.getLat()) //
					.put("lng", mapInfo.getLng());

			json.put("mapInfo", m);
		}

		final JSONList list = new JSONList();

		json.put("groups", list);

		for (final PoiGroup group : p.getGroups()) {

			final JSONList pois = new JSONList();

			list.add(new JSONMap() //
					.put("groupLabel", group.getGroupLabel()) //
					.put("pois", pois));

			for (final Poi poi : group.getPois()) {

				final JSONMap map = new JSONMap() //
						.put("id", poi.getId()) //
						.put("name", poi.getName()) //
						.put("name", poi.getName()) //
						.put("coordinates", poi.getCoordinates()) //
						.put("lat", poi.getLatitude()) //
						.put("lng", poi.getLongitude()) //
						.put("address", poi.getAddress()) //
						.put("floor", poi.getFloor()) //
						.put("itinerary", poi.getItinerary()) //
						.put("openingHours", poi.getOpeningHours()) //
						.put("phone", poi.getPhone()) //
						.put("fax", poi.getFax()) //
						.put("email", poi.getEmail()) //
						.put("url", poi.getUrl()) //
						.put("markerType", poi.getMarkerType()) //
						.put("markerIndex", poi.getMarkerIndex());

				if (poi.getParentUid() != null)
					map.put("parentUid", poi.getParentUid());

				if (poi.getCategory() != null)
					map.put("categoryId", poi.getCategory());

				// TODO: Do not add the fields if null

				final String imageUrl = poi.getImageUrl();

				map.put("image", new JSONMap() //
						.put("url", imageUrl) //
						.put("width", poi.getImageWidth()) //
						.put("height", poi.getImageHeight()));

				map.put("comments", new JSONMap() //
						.put("url", poi.getCommentsUrl()));

				pois.add(map);
			}
		}

		return json.toJSONString();
	}

}
