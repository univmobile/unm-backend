package fr.univmobile.backend.admin;

import static org.junit.Assert.assertEquals;
import static fr.univmobile.backend.json.AbstractJSONController.*;

import java.io.File;

import org.junit.Before;
import org.junit.Test;

import fr.univmobile.backend.client.ImageMapClient;
import fr.univmobile.backend.client.ImageMapClientFromLocal;
import fr.univmobile.backend.client.PoiCategoryClient;
import fr.univmobile.backend.client.PoiCategoryClientFromLocal;
import fr.univmobile.backend.client.RegionClient;
import fr.univmobile.backend.client.RegionClientFromLocal;
import fr.univmobile.backend.client.json.ImageMapJSONClient;
import fr.univmobile.backend.client.json.ImageMapJSONClientImpl;
import fr.univmobile.backend.client.json.PoiCategoryJSONClient;
import fr.univmobile.backend.client.json.PoiCategoryJSONClientImpl;
import fr.univmobile.backend.client.json.RegionJSONClient;
import fr.univmobile.backend.client.json.RegionJSONClientImpl;
import fr.univmobile.backend.core.ImageMapDataSource;
import fr.univmobile.backend.core.PoiCategoryDataSource;
import fr.univmobile.backend.core.PoiDataSource;
import fr.univmobile.backend.core.RegionDataSource;
import fr.univmobile.backend.json.RegionsJSONController;
import fr.univmobile.commons.datasource.impl.BackendDataSourceFileSystem;

public class GeocampusJSONControllerTest {

	@Before
	public void setUp() throws Exception {

		final RegionDataSource regionsDataSource = BackendDataSourceFileSystem
				.newDataSource(RegionDataSource.class, new File("src/test/data/regions/004"));

		final PoiDataSource poiDataSource = BackendDataSourceFileSystem
				.newDataSource(PoiDataSource.class,new File("src/test/data/pois/004"));

		final PoiCategoryDataSource poiCategoryDataSource = BackendDataSourceFileSystem
				.newDataSource(PoiCategoryDataSource.class,new File("src/test/data/poiscategories/004"));

		imageMapDataSource = BackendDataSourceFileSystem
				.newDataSource(ImageMapDataSource.class, new File("src/test/data/imagemaps/004"));
		
		final RegionClient regionClient = new RegionClientFromLocal(
				"(dummy baseURL)", regionsDataSource, poiDataSource);

		regionJSONClient = new RegionJSONClientImpl(regionClient);

		final PoiCategoryClient poiCategoryClient = new PoiCategoryClientFromLocal(
				"(dummy baseURL)", poiCategoryDataSource, regionsDataSource);

		poiCategoryJSONClient = new PoiCategoryJSONClientImpl(poiCategoryClient);

		final ImageMapClient imageMapClient = new ImageMapClientFromLocal(
				"(dummy baseURL)", imageMapDataSource, poiDataSource);

		imageMapJSONClient = new ImageMapJSONClientImpl(imageMapClient);

		//client = new RegionClientFromLocal(regionJSONClient);
	}

	private RegionJSONClient regionJSONClient;
	private PoiCategoryJSONClient poiCategoryJSONClient;
	private ImageMapJSONClient imageMapJSONClient;
	private ImageMapDataSource imageMapDataSource;
	//private RegionClient client;

	@Test
	public void test_gecampusJSONData() throws Exception {
		GeocampusJSONController ctrl = new GeocampusJSONController(regionJSONClient, poiCategoryJSONClient, imageMapJSONClient, imageMapDataSource);
		assertEquals(6524, ctrl.actionJSON("test").length());
	}
}
