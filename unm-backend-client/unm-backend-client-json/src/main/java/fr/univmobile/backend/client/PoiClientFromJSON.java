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
