package fr.univmobile.backend.client.json;

import static com.google.common.base.Preconditions.checkNotNull;

import java.io.IOException;

import javax.inject.Inject;
import javax.inject.Named;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.json.simple.JSONArray;

import fr.univmobile.backend.client.ClientException;
import fr.univmobile.backend.client.PoiCategory;
import fr.univmobile.backend.client.PoiCategoryClient;
import fr.univmobile.backend.json.JSONMap;

public class PoiCategoryJSONClientImpl implements PoiCategoryJSONClient {
	
	@Inject
	public PoiCategoryJSONClientImpl(@Named("PoiCategoryJSONClientImpl")//
			final PoiCategoryClient poiCategoryClient) {

		this.poiCategoryClient = checkNotNull(poiCategoryClient, "poiCategoryClient");
	}
	
	private final PoiCategoryClient poiCategoryClient;

	private static final Log log = LogFactory.getLog(PoiCategoryJSONClientImpl.class);

	@Override
	public String getPoiCategoryJSON(int id) throws IOException {
		log.debug("getPoiCategoryJSON()...");
		try {
			final PoiCategory p = poiCategoryClient.getPoiCategory(id);
			return poiCategoryJSON(p).toJSONString();
		} catch (final ClientException e) {

			log.error(e);

			throw new RuntimeException(e);
		}

	}
	
	private static JSONMap poiCategoryJSON(final PoiCategory poiCategory) {
		final JSONMap json = new JSONMap() //
			.put("id", poiCategory.getId()) //
			.put("name", poiCategory.getName());
		if (poiCategory.getDescription() != null) {
			json.put("description", poiCategory.getDescription());
		}
		if (poiCategory.getCursorUrl() != null) {
			json.put("cursorUrl", poiCategory.getCursorUrl());
		}
		JSONArray childrenJSON = new JSONArray();
		for (PoiCategory child : poiCategory.getChildCategories()) {
			childrenJSON.add(poiCategoryJSON(child));
		}
		json.put("children", childrenJSON);
		return json;
	}

}
