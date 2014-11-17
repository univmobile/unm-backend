package fr.univmobile.backend.client;

import static com.google.common.base.Preconditions.checkNotNull;

import java.io.IOException;

import javax.annotation.Nullable;
import javax.inject.Inject;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import fr.univmobile.backend.core.PoiCategoryDataSource;
import fr.univmobile.backend.core.RegionDataSource;
import fr.univmobile.commons.DataBeans;

public class PoiCategoryClientFromLocal extends AbstractClientFromLocal implements
		PoiCategoryClient {

	@Inject
	public PoiCategoryClientFromLocal(String baseURL,
			final PoiCategoryDataSource poiCategoryDataSource,
			final RegionDataSource regionDataSource) {
		super(baseURL);

		this.poiCategoryDataSource = checkNotNull(poiCategoryDataSource, "poiCategoryDataSource");
		
		this.regionDataSource = checkNotNull(regionDataSource,
				"regionDataSource");
	}
	
	private final PoiCategoryDataSource poiCategoryDataSource;

	private final RegionDataSource regionDataSource;
	
	private static final Log log = LogFactory.getLog(PoiCategoryClientFromLocal.class);

	@Override
	public PoiCategory getPoiCategory(int id) throws IOException,
			ClientException {
		if (log.isDebugEnabled()) {
			log.debug("getPoiCategory(): " + id + "...");
		}

		final fr.univmobile.backend.core.PoiCategory dsPoiCategory = poiCategoryDataSource.getByUid(id);
		

		final MutablePoiCategory poiCategory = createPoiCategoryFromData(dsPoiCategory);

		if (poiCategory == null) {
			throw new PoiCategoryNotFoundException(id);
		}

		return poiCategory;
	}
	
	@Nullable
	private MutablePoiCategory createPoiCategoryFromData(
			final fr.univmobile.backend.core.PoiCategory dsPoiCategory) {

		final int poiCategoryUid = dsPoiCategory.getUid();

		final MutablePoiCategory poiCategory = DataBeans //
				.instantiate(MutablePoiCategory.class) //
				.setId(poiCategoryUid) //
				.setName(dsPoiCategory.getName());

		if (dsPoiCategory.getDescription() != null && !dsPoiCategory.getDescription().trim().isEmpty()) {
			poiCategory.setDescription(dsPoiCategory.getDescription());
		}
		if (dsPoiCategory.getCursorUrl() != null && !dsPoiCategory.getCursorUrl().trim().isEmpty()) {
			poiCategory.setCursorUrl(dsPoiCategory.getCursorUrl());
		}
		
		/**
		 * Construit la liste des categories filles actives
		 */
		PoiCategory[]  childCategories = new PoiCategory[dsPoiCategory.getChildren().length];
		int index = 0;
		for (int categoryUid : dsPoiCategory.getChildren()) {
			fr.univmobile.backend.core.PoiCategory childDsPoiCategory = poiCategoryDataSource.getByUid(categoryUid);
			childCategories[index] = createPoiCategoryFromData(childDsPoiCategory);
			index++;
		}
		poiCategory.setChildCategories(childCategories);

		return poiCategory;
	}

	
	private interface MutablePoiCategory extends PoiCategory {

		MutablePoiCategory setId(int id);

		MutablePoiCategory setName(String name);

		MutablePoiCategory setDescription(@Nullable String description);
		
		MutablePoiCategory setCursorUrl(@Nullable String cursorUrl);
		
		MutablePoiCategory setChildCategories(PoiCategory[] childCategories);
		
	}

}
