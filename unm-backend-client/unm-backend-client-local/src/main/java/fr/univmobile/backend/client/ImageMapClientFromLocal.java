package fr.univmobile.backend.client;

import static com.google.common.base.Preconditions.checkNotNull;
import static org.apache.commons.lang3.StringUtils.isBlank;
import static org.apache.commons.lang3.StringUtils.substringAfter;
import static org.apache.commons.lang3.StringUtils.substringBefore;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.annotation.Nullable;
import javax.inject.Inject;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.avcompris.lang.NotImplementedException;
import com.google.common.collect.Iterables;

import fr.univmobile.backend.core.ImageMap.PoiInfo;
import fr.univmobile.backend.core.ImageMapDataSource;
import fr.univmobile.backend.core.PoiDataSource;
import fr.univmobile.commons.DataBeans;

public class ImageMapClientFromLocal extends AbstractClientFromLocal implements
		ImageMapClient {

	@Inject
	public ImageMapClientFromLocal(String baseURL,
			final ImageMapDataSource imageMapDataSource,
			final PoiDataSource poiDataSource) {
		super(baseURL);

		this.imageMapDataSource = checkNotNull(imageMapDataSource, "imageMapDataSource");
		
		this.poiDataSource = checkNotNull(poiDataSource, "poiDataSource");
	}

	private final ImageMapDataSource imageMapDataSource;
	private final PoiDataSource poiDataSource;

	private static final Log log = LogFactory.getLog(ImageMapClientFromLocal.class);

	@Override
	public ImageMap getImageMap(int id, int poiId) throws IOException,
			ClientException {
		if (log.isDebugEnabled()) {
			log.debug("getImageMap(): " + id + " ; poi : " + poiId + " ...");
		}

		final fr.univmobile.backend.core.ImageMap dsImageMap = imageMapDataSource.getByUid(id);
		

		final MutableImageMap imageMap = createImageMapFromData(poiDataSource, dsImageMap, poiId);

		if (imageMap == null) {
			throw new ImageMapNotFoundException(id);
		}

		return imageMap;
	}
	
	@Nullable
	private MutableImageMap createImageMapFromData(final PoiDataSource poiDataSource,
			final fr.univmobile.backend.core.ImageMap dsImageMap, int selectedPoiId) {
		
		final MutableImageMap imageMap = DataBeans //
				.instantiate(MutableImageMap.class) //
				.setId(dsImageMap.getUid()) //
				.setName(dsImageMap.getName())
				.setImageUrl(dsImageMap.getImageUrl());

		if (dsImageMap.getDescription() != null && !dsImageMap.getDescription().trim().isEmpty()) {
			imageMap.setDescription(dsImageMap.getDescription());
		}

		if (dsImageMap.getPoiInfos() != null) {
			ImageMapPoi pois[] = new ImageMapPoi[dsImageMap.getPoiInfos().length];
			int index = 0;
			for (PoiInfo poiInfo: dsImageMap.getPoiInfos()) {
				final fr.univmobile.backend.core.Poi poi = poiDataSource.getByUid(poiInfo.getId());
				pois[index] = createPoiFromData(poi, poiInfo);
				if (poiInfo.getId() == selectedPoiId) {
					imageMap.setSelectedPoi(pois[index]);
				}
				index++;
			}
			imageMap.setPois(pois);
		}
		
		return imageMap;
	}
	
	@Nullable
	private MutableImageMapPoi createPoiFromData(
			final fr.univmobile.backend.core.Poi dsPoi, final PoiInfo poiInfo) {

		final int poiUid = dsPoi.getUid();

		final String coordinates = dsPoi.getCoordinates();
		final String latitude = substringBefore(coordinates, ",");
		final String longitude = substringAfter(coordinates, ",");

		if (isBlank(coordinates) || isBlank(latitude) || isBlank(longitude)) {
			return null;
		}

		final MutableImageMapPoi poi = DataBeans //
				.instantiate(MutableImageMapPoi.class) //
				.setId(poiUid) //
				.setName(dsPoi.getName()) //
				.setCoordinates(coordinates) //
				.setLatitude(latitude) //
				.setLongitude(longitude)
				.setMarkerType(dsPoi.getMarkerType().toString());

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

		final String[] dsUniversities = dsPoi.getUniversityIds();

		final List<String> universityIds = new ArrayList<String>();

		for (final String dsUniversity : dsUniversities) {

			universityIds.add(dsUniversity);
		}

		poi.setUniversityIds(Iterables.toArray(universityIds, String.class));

		// END
		final String imageCoordinates = poiInfo.getCoordinates();
		try {
			if (imageCoordinates != null) {
				final int x = Integer.valueOf(substringBefore(imageCoordinates, ",").trim());
				final int y = Integer.valueOf(substringAfter(imageCoordinates, ",").trim());
				poi.setImageMapX(x);
				poi.setImageMapY(y);
			} else {
				// The X, Y coordinates are mandatory for Imag Map Point of interest
				return null;
			}
		} catch (NumberFormatException ex) {
			log.error("Impossible to get the x,y coordinates of the poi : " + poiUid, ex);
			// The X, Y coordinates are mandatory for Imag Map Point of interesr
			return null;
		}

		return poi;
	}

	private interface MutableImageMap extends ImageMap {

		MutableImageMap setId(int id);

		MutableImageMap setName(String name);
		
		MutableImageMap setImageUrl(String cursorUrl);

		MutableImageMap setDescription(@Nullable String description);
		
		MutableImageMap setSelectedPoi(@Nullable ImageMapPoi poi);
		
		MutableImageMap setPois(ImageMapPoi[] pois);
		
	}

	private interface MutableImageMapPoi extends ImageMapPoi {

		MutableImageMapPoi setId(int id);

		MutableImageMapPoi setName(String name);

		MutableImageMapPoi setAddress(@Nullable String address);

		MutableImageMapPoi setPhone(@Nullable String phone);

		MutableImageMapPoi setFloor(@Nullable String floor);

		MutableImageMapPoi setEmail(@Nullable String email);

		MutableImageMapPoi setFax(@Nullable String fax);

		MutableImageMapPoi setOpeningHours(@Nullable String openingHours);

		MutableImageMapPoi setItinerary(@Nullable String itinerary);

		MutableImageMapPoi setCoordinates(String coordinates);

		MutableImageMapPoi setLatitude(String latitude);

		MutableImageMapPoi setLongitude(String longitude);

		MutableImageMapPoi setUrl(@Nullable String url);

		MutableImageMapPoi setCommentsUrl(String commentsUrl);

		MutableImageMapPoi setImageUrl(@Nullable String image);

		MutableImageMapPoi setImageWidth(int width);

		MutableImageMapPoi setImageHeight(int height);

		/**
		 * e.g. "green"
		 */
		MutableImageMapPoi setMarkerType(String markerType);

		/**
		 * e.g. "A"
		 */
		MutableImageMapPoi setMarkerIndex(String markerIndex);

		MutableImageMapPoi setUniversityIds(String[] universityIds);
		
		/**
		 * get the x position on the image
		 */
		@Nullable
		MutableImageMapPoi setImageMapX(int x);
		
		/**
		 * get the y position on the image
		 */
		@Nullable
		MutableImageMapPoi setImageMapY(int y);
	}

}
