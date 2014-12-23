package fr.univmobile.backend.client;

import java.io.IOException;

import javax.annotation.Nullable;
import javax.inject.Inject;

import net.avcompris.binding.annotation.XPath;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.avcompris.lang.NotImplementedException;

import fr.univmobile.backend.client.json.PoiJSONClient;

public class PoiClientFromJSON extends AbstractClientFromJSON<PoiJSONClient>
		implements PoiClient {

	@Inject
	public PoiClientFromJSON(final PoiJSONClient jsonClient) {

		super(jsonClient);
	}

	private static Log log = LogFactory.getLog(PoiClientFromJSON.class);

	@Override
	public Pois getPois() throws IOException {

		if (log.isDebugEnabled()) {
			log.debug("getPois()...");
		}

		return unmarshall(jsonClient.getPoisJSON(), PoisJSON.class);
	}

	@Override
	public Pois getPoisByRegion(String regionId) throws IOException {
		
		if (log.isDebugEnabled()) {
			log.debug("getPoisByRegion()...");
		}
		
		return unmarshall(jsonClient.getPoisByRegionJSON(regionId), PoisJSON.class);
	}

	@Override
	public Pois getPoisByRegionAndCategory(String regionId, Integer categoryId) throws IOException {
		
		if (log.isDebugEnabled()) {
			log.debug("getPoisByRegionAndCategory()...");
		}
		
		return unmarshall(jsonClient.getPoisByRegionAndCategoryJSON(regionId, categoryId), PoisJSON.class);
	}

	@Override
	public Pois getPoisByRegionAndCategory(String regionUid, Integer categoryId, int[] excludeCategories, String filterByUniversity, boolean completeWholeTree) throws IOException {
		if (log.isDebugEnabled()) {
			log.debug("getPoisByRegionAndCategory()...");
		}
		
		return unmarshall(jsonClient.getPoisByRegionAndCategoryJSON(regionUid, categoryId, excludeCategories, filterByUniversity, completeWholeTree), PoisJSON.class);
	}
	
	@Override
	public Pois getPoisByCategory(int categoryId) throws IOException {
		
		if (log.isDebugEnabled()) {
			log.debug("getPoisByCategory()...");
		}
		
		return unmarshall(jsonClient.getPoisByCategoryJSON(categoryId), PoisJSON.class);
	}

	@Override
	public Pois getPois(final double lat, final double lng) throws IOException {

		if (log.isDebugEnabled()) {
			log.debug("getPois():" + lat + "," + lng + "...");
		}

		return unmarshall(jsonClient.getPoisJSON(lat, lng), PoisJSON.class);
	}

	@Override
	public Poi getPoi(int id) throws IOException {

		throw new NotImplementedException();
	}

	@Override
	public Pois getNearestPois(double lat, double lon, double metersAway) throws IOException {
		if (log.isDebugEnabled()) {
			log.debug("getNearestPois():" + lat + "," + lon + "..." + metersAway);
		}

		return unmarshall(jsonClient.getNearestPoisJSON(lat, lon, metersAway), PoisJSON.class);
	}

	@XPath("/*")
	public interface PoisJSON extends Pois {

		@XPath("mapInfo")
		@Nullable
		@Override
		MapInfoJSON getMapInfo();

		interface MapInfoJSON extends MapInfo {

			@XPath("@zoom")
			@Override
			int getPreferredZoom();

			@XPath("@lat")
			@Override
			double getLat();

			@XPath("@lng")
			@Override
			double getLng();
		}

		@XPath("groups")
		@Override
		PoiGroupJSON[] getGroups();

		interface PoiGroupJSON extends PoiGroup {

			@XPath("@groupLabel")
			@Override
			String getGroupLabel();

			@XPath("pois")
			@Override
			PoiJSON[] getPois();
		}

		interface PoiJSON extends Poi {

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

			@XPath("@parentUid")
			@Nullable
			@Override
			Integer getParentUid();

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
		}
	}

}
