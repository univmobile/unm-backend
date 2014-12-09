package fr.univmobile.backend.client;

import java.io.IOException;

import javax.annotation.Nullable;
import javax.inject.Inject;

import net.avcompris.binding.annotation.XPath;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import fr.univmobile.backend.client.json.ImageMapJSONClient;

public class ImageMapClientFromJSON extends AbstractClientFromJSON<ImageMapJSONClient> implements
		ImageMapClient {

	@Inject
	public ImageMapClientFromJSON(final ImageMapJSONClient jsonClient) {
		super(jsonClient);
	}
	
	private static Log log = LogFactory.getLog(ImageMapClientFromJSON.class);

	@Override
	public ImageMap getImageMap(int id, int poiId) throws IOException,
			ClientException {
		if (log.isDebugEnabled()) {
			log.debug("getImageMap():" + id + " ; poi :" + poiId + "...");
		}

		return unmarshall(jsonClient.getImageMapJSON(id, poiId), ImageMapJSON.class);
	}

	@Override
	public ImageMap getImageMap(int id) throws IOException,
			ClientException {
		if (log.isDebugEnabled()) {
			log.debug("getImageMap():" + id);
		}

		return unmarshall(jsonClient.getImageMapJSON(id), ImageMapJSON.class);
	}
	
	
	@XPath("/*")
	public interface ImageMapJSON extends ImageMap {
		
		@XPath("@id")
		@Override
		int getId();

		@XPath("@name")
		@Override
		String getName();

		@XPath("@description")
		@Nullable
		@Override
		String getDescription();
		
		/**
		 * Url of the map to display (URL of an image)
		 * @return
		 */
		@XPath("@imageUrl")
		@Override
		String getImageUrl();

		@XPath("selectedPoi")
		@Nullable
		@Override
		ImageMapPoiJSON getSelectedPoi();
		
		@XPath("pois")
		@Nullable
		@Override
		ImageMapPoiJSON[] getPois();

	}
	
	public interface ImageMapPoiJSON extends ImageMapPoi {
		@XPath("@id")
		@Override
		int getId();

		@XPath("@name")
		@Override
		String getName();

		@XPath("@address")
		@Nullable
		@Override
		String getAddress();

		@XPath("@phone")
		@Nullable
		@Override
		String getPhone();

		@XPath("@floor")
		@Nullable
		@Override
		String getFloor();

		@XPath("@email")
		@Nullable
		@Override
		String getEmail();

		@XPath("@fax")
		@Nullable
		@Override
		String getFax();

		@XPath("@openingHours")
		@Override
		@Nullable
		String getOpeningHours();

		@XPath("@itinerary")
		@Nullable
		@Override
		String getItinerary();

		@XPath("poiCategory/id")
		@Nullable
		@Override
		Integer getCategory();

		@XPath("@parentUid")
		@Nullable
		@Override
		Integer getParentUid();

		@XPath("@coordinates")
		@Override
		String getCoordinates();

		@XPath("@lat")
		@Override
		String getLatitude();

		@XPath("@lng")
		@Override
		String getLongitude();

		@XPath("@url")
		@Override
		@Nullable
		String getUrl();

		@XPath("comments/@url")
		@Override
		String getCommentsUrl();

		@XPath("image/@url")
		@Override
		@Nullable
		String getImageUrl();

		@Override
		@XPath("image/@width")
		int getImageWidth();

		@Override
		@XPath("image/@Height")
		int getImageHeight();

		/**
		 * e.g. "green"
		 */
		@Override
		@XPath("@markerType")
		String getMarkerType();

		/**
		 * e.g. "A"
		 */
		@Override
		@XPath("@markerIndex")
		String getMarkerIndex();

		/**
		 * get the x position on the image map
		 */
		@Override
		@XPath("@imageMapX")
		int getImageMapX();
		
		/**
		 * get the y position on the image map
		 */
		@Override
		@XPath("@imageMapY")
		int getImageMapY();
 
	}


}
