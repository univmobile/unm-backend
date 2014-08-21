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
import fr.univmobile.backend.json.JSONList;
import fr.univmobile.backend.json.JSONMap;

public class PoiJSONClientImpl extends AbstractJSONClientImpl implements
		PoiJSONClient {

	@Inject
	public PoiJSONClientImpl( //
			final String baseURL, @Named("PoiJSONClientImpl")//
			final PoiClient poiClient) {

		super(baseURL);

		this.poiClient = checkNotNull(poiClient, "poiClient");
	}

	private final PoiClient poiClient;

	private static final Log log = LogFactory.getLog(PoiJSONClientImpl.class);

	@Override
	public String getPoisJSON() throws IOException {

		log.debug("getPoisJSON()...");

		final PoiGroup[] poiGroups = poiClient.getPois();

		final JSONMap json = new JSONMap();

		final JSONList list = new JSONList();

		json.put("groups", list);

		for (final PoiGroup group : poiGroups) {

			final JSONList pois = new JSONList();

			list.add(new JSONMap() //
					.put("groupLabel", group.getGroupLabel()) //
					.put("pois", pois));

			for (final Poi poi : group.getPois()) {

				final JSONMap map = new JSONMap() //
						.put("id", poi.getId()) //
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

				// TODO: Do not add the fields if null

				final String imageUrl = poi.getImageUrl();

				map.put("image", new JSONMap() //
						.put("url", filterURL(imageUrl)) //
						.put("width", poi.getImageWidth()) //
						.put("height", poi.getImageHeight()));

				pois.add(map);
			}
		}

		return json.toJSONString();
	}
}
