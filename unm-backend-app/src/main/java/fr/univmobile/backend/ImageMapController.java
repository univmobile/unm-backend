package fr.univmobile.backend;

import static com.google.common.base.Preconditions.checkNotNull;
import fr.univmobile.backend.client.ImageMap;
import fr.univmobile.backend.client.ImageMapClient;
import fr.univmobile.backend.client.ImageMapClientFromLocal;
import fr.univmobile.backend.client.PoiNotFoundException;
import fr.univmobile.backend.core.ImageMapDataSource;
import fr.univmobile.backend.core.PoiDataSource;
import fr.univmobile.web.commons.PageNotFoundException;
import fr.univmobile.web.commons.PathVariable;
import fr.univmobile.web.commons.Paths;
import fr.univmobile.web.commons.View;

/**
 * This controller permits to manage to kind of different URL :
 *   - imagemap/${id} : Permit to return the {@link ImageMap} object of the Map at image format of id ${id} (Image file + POI to place in this map with its X,Y positions). There is in this case no selected POI
 *   - imagemap/${id}/${selectedPoiId} : same but with a selected POI (the one of ID ${selectedPoiId}). It will be possible to highlight this POI in the Map for eg.
 *
 */
@Paths({ "imagemap/${id}", "imagemap/${id}/${selectedPoiId}" })
public class ImageMapController extends AbstractBackendController {

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
	
	public ImageMapController(final ImageMapDataSource imageMap,
			final PoiDataSource pois) {
		
		this.imageMap = checkNotNull(imageMap, "imageMap");
		this.pois = checkNotNull(pois, "pois");
	}
	
	private final ImageMapDataSource imageMap;
	private final PoiDataSource pois;
	
	private ImageMapClient getImageMapClient() {
		return new ImageMapClientFromLocal(getBaseURL(), imageMap, pois);
	}

	@Override
	public View action() throws Exception {
		final int id = getImageMapId();
		
		final int selectedPoiId = getSelectedPoiId();
		
		final ImageMap imageMap;
		
		try {

			imageMap = getImageMapClient().getImageMap(id, selectedPoiId);

		} catch (final PoiNotFoundException e) {

			throw new PageNotFoundException();
		}

		setAttribute("imageMap", imageMap);

		return new View("imagemap.jsp");
	}

}
