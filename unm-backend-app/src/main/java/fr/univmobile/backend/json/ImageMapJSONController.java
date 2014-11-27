package fr.univmobile.backend.json;

import static com.google.common.base.Preconditions.checkNotNull;
import static org.apache.commons.lang3.StringUtils.substringAfter;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import fr.univmobile.backend.client.ImageMap;
import fr.univmobile.backend.client.json.ImageMapJSONClient;
import fr.univmobile.backend.core.ImageMapDataSource;
import fr.univmobile.web.commons.PageNotFoundException;
import fr.univmobile.web.commons.PathVariable;
import fr.univmobile.web.commons.Paths;

/**
 * This controller permits to manage to kind of different URL :
 *   - imagemap/${id} : Permit to return the {@link ImageMap} object of the Map at image format of id ${id} (Image file + POI to place in this map with its X,Y positions). There is in this case no selected POI
 *   - imagemap/${id}/${selectedPoiId} : same but with a selected POI (the one of ID ${selectedPoiId}). It will be possible to highlight this POI in the Map for eg.
 *
 */
@Paths({ "json/imagemap/${id}", "json/imagemap/${id}/${selectedPoiId}" })
public class ImageMapJSONController extends AbstractJSONController {

	/**
	 * Get the image map Id in the URL
	 */
	@PathVariable("${id}")
	private int getImageMapId() {
		return getPathIntVariable("${id}");
	}

	/**
	 * Get the selected POI Id in the url
	 */
	@PathVariable("${selectedPoiId}")
	private int getSelectedPoiId() {
		return getPathIntVariable("${selectedPoiId}");
	}
	
	public ImageMapJSONController(final ImageMapDataSource imageMapDataSource, final ImageMapJSONClient imageMapJSONClient) {
		
		this.imageMapDataSource = checkNotNull(imageMapDataSource, "imageMapDataSource");
		this.imageMapJSONClient = checkNotNull(imageMapJSONClient, "imageMapJSONClient");
	}
	
	private final ImageMapDataSource imageMapDataSource;
	private final ImageMapJSONClient imageMapJSONClient;
	
	private static final Log log = LogFactory
			.getLog(ImageMapJSONController.class);

	@Override
	public String actionJSON(String baseURL) throws Exception {

		log.debug("actionJSON()...");

		int imageMapId = getImageMapId();
		
		int selectedPoiId = getSelectedPoiId();

		if (log.isInfoEnabled()) {
			log.info("imageMapId: " + imageMapId + " ; selectedPoiId : " + selectedPoiId);
		}
		
		if (imageMapDataSource.isNullByUid(imageMapId)) {
			throw new PageNotFoundException();
		}

		final String imageMapJSON = imageMapJSONClient.getImageMapJSON(imageMapId, selectedPoiId);

		final String json = "{\"url\":\""
				+ composeJSONendPoint(baseURL, "/imagemap/" + imageMapId + "/" + selectedPoiId) + "\","
				+ substringAfter(imageMapJSON, "{");

		return json;
	}

}
