package fr.univmobile.backend.client.json;

import static com.google.common.base.Preconditions.checkNotNull;

import java.io.IOException;

import javax.inject.Inject;
import javax.inject.Named;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.json.simple.JSONArray;

import fr.univmobile.backend.client.ClientException;
import fr.univmobile.backend.client.ImageMap;
import fr.univmobile.backend.client.ImageMapClient;
import fr.univmobile.backend.client.ImageMapPoi;
import fr.univmobile.backend.json.JSONMap;

public class ImageMapJSONClientImpl implements ImageMapJSONClient {

	@Inject
	public ImageMapJSONClientImpl(@Named("ImageMapJSONClientImpl")//
			final ImageMapClient imageMapClient) {

		this.imageMapClient = checkNotNull(imageMapClient, "imageMapClient");
	}
	
	private final ImageMapClient imageMapClient;

	private static final Log log = LogFactory.getLog(ImageMapJSONClientImpl.class);

	@Override
	public String getImageMapJSON(int id, int poiId) throws IOException {
		return getImageMapJSONWithOrWithoutSelectedPoi(id, poiId);
	}

	@Override
	public String getImageMapJSON(int id) throws IOException {
		return getImageMapJSONWithOrWithoutSelectedPoi(id, null);
	}

	private String getImageMapJSONWithOrWithoutSelectedPoi(int id, Integer poiId) throws IOException {
		log.debug("getImageMap()...");
		try {
			final ImageMap p = poiId == null ? imageMapClient.getImageMap(id) : imageMapClient.getImageMap(id, poiId);
			return imageMapJSON(p).toJSONString();
		} catch (final ClientException e) {

			log.error(e);

			throw new RuntimeException(e);
		}
	}
	
	private static JSONMap imageMapJSON(final ImageMap imageMap) {
		final JSONMap json = new JSONMap() //
			.put("id", imageMap.getId()) //
			.put("name", imageMap.getName())
			.put("imageUrl", imageMap.getImageUrl());
		if (imageMap.getDescription() != null) {
			json.put("description", imageMap.getDescription());
		}
		if (imageMap.getSelectedPoi() != null) {
			json.put("selectedPoi", poiJSON(imageMap.getSelectedPoi()));
		}
		JSONArray poisJSON = new JSONArray();
		for (ImageMapPoi poi : imageMap.getPois()) {
			poisJSON.add(poiJSON(poi));
		}
		json.put("pois", poisJSON);
		return json;
	}
	
	private static JSONMap poiJSON(final ImageMapPoi poi) {

		final JSONMap json = new JSONMap() //
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
				.put("markerType", poi.getMarkerType());

		final String imageUrl = poi.getImageUrl();

		json.put("image", new JSONMap() //
				.put("url", imageUrl) //
				.put("width", poi.getImageWidth()) //
				.put("height", poi.getImageHeight()));

		json.put("comments", new JSONMap() //
				.put("url", poi.getCommentsUrl()));
		
		json.put("imageMapX", poi.getImageMapX());
		json.put("imageMapY", poi.getImageMapY());

		return json;
	}

}
