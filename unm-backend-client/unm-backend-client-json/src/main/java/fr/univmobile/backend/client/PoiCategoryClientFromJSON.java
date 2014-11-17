package fr.univmobile.backend.client;

import java.io.IOException;

import javax.annotation.Nullable;
import javax.inject.Inject;

import net.avcompris.binding.annotation.XPath;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import fr.univmobile.backend.client.json.PoiCategoryJSONClient;

public class PoiCategoryClientFromJSON extends
		AbstractClientFromJSON<PoiCategoryJSONClient> implements
		PoiCategoryClient {
	
	@Inject
	public PoiCategoryClientFromJSON(final PoiCategoryJSONClient jsonClient) {

		super(jsonClient);
	}
	
	private static Log log = LogFactory.getLog(PoiCategoryClientFromJSON.class);

	@Override
	public PoiCategory getPoiCategory(int id) throws IOException,
			ClientException {
		if (log.isDebugEnabled()) {
			log.debug("getPoiCategory():" + id + "...");
		}

		return unmarshall(jsonClient.getPoiCategoryJSON(id), PoiCategoryJSON.class);
	}
	
	@XPath("/*")
	public interface PoisCategoriesJSON extends PoisCategories {
		
	}

	@XPath("/*")
	public interface PoiCategoryJSON extends PoiCategory {
		
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

		@XPath("@cursorUrl")
		@Nullable
		@Override
		String getCursorUrl();
		
		@XPath("children")
		@Nullable
		@Override
		PoiCategoryJSON[] getChildCategories();

	}

}
