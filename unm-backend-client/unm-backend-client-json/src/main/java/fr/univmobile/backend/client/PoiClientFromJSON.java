package fr.univmobile.backend.client;

import static com.google.common.base.Preconditions.checkNotNull;

import java.io.IOException;

import javax.annotation.Nullable;
import javax.inject.Inject;

import net.avcompris.binding.annotation.XPath;
import net.avcompris.binding.json.JsonBinder;
import net.avcompris.binding.json.impl.DomJsonBinder;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.json.simple.JSONValue;

import fr.univmobile.backend.client.json.PoiJSONClient;

public class PoiClientFromJSON implements PoiClient {

	@Inject
	public PoiClientFromJSON(final PoiJSONClient jsonClient) {

		this.jsonClient = checkNotNull(jsonClient, "jsonClient");
	}

	private final PoiJSONClient jsonClient;

	private static Log log = LogFactory.getLog(PoiClientFromJSON.class);

	@Override
	public PoiGroup[] getPois() throws IOException {

		System.out.println("dddd");
		if (log.isDebugEnabled()) {
			log.debug("getPois()...");
		}

		final String json = jsonClient.getPoisJSON();

		System.out.println(json);
		if (log.isDebugEnabled()) {
			log.debug("json.length(): " + json.length());
			log.debug("json: "
					+ (json.length() <= 80 ? json
							: (json.substring(0, 80) + "...")));
		}

		final Object jsonObject = JSONValue.parse(json);

		final JsonBinder binder = new DomJsonBinder();

		final PoisJSON poisJSON = binder.bind(jsonObject, PoisJSON.class);

		return poisJSON.getPois();
	}

	@XPath("/*")
	public interface PoisJSON {

		@XPath("groups")
		PoiGroupJSON[] getPois();

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

			@XPath("@image")
			@Override
			@Nullable
			String getImage();

			@Override
			@XPath("@imageWidth")
			int getImageWidth();

			@Override
			@XPath("@imageHeight")
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
