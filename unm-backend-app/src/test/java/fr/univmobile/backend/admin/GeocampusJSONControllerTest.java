package fr.univmobile.backend.admin;

import java.io.File;
import org.junit.Before;
import org.junit.Test;

import fr.univmobile.backend.client.ImageMapClient;
import fr.univmobile.backend.client.ImageMapClientFromLocal;
import fr.univmobile.backend.client.PoiCategoryClient;
import fr.univmobile.backend.client.PoiCategoryClientFromLocal;
import fr.univmobile.backend.client.PoiClient;
import fr.univmobile.backend.client.PoiClientFromLocal;
import fr.univmobile.backend.client.RegionClient;
import fr.univmobile.backend.client.RegionClientFromLocal;
import fr.univmobile.backend.client.json.ImageMapJSONClient;
import fr.univmobile.backend.client.json.ImageMapJSONClientImpl;
import fr.univmobile.backend.client.json.PoiCategoryJSONClient;
import fr.univmobile.backend.client.json.PoiCategoryJSONClientImpl;
import fr.univmobile.backend.client.json.PoiJSONClient;
import fr.univmobile.backend.client.json.PoiJSONClientImpl;
import fr.univmobile.backend.client.json.RegionJSONClient;
import fr.univmobile.backend.client.json.RegionJSONClientImpl;
import fr.univmobile.backend.core.ImageMapDataSource;
import fr.univmobile.backend.core.PoiCategoryDataSource;
import fr.univmobile.backend.core.PoiDataSource;
import fr.univmobile.backend.core.RegionDataSource;
import fr.univmobile.commons.datasource.impl.BackendDataSourceFileSystem;

public class GeocampusJSONControllerTest {

	@Before
	public void setUp() throws Exception {
		/*
		regionsDataSource = BackendDataSourceFileSystem
				.newDataSource(RegionDataSource.class, new File("src/test/data/regions/004"));

		poiCategoryDataSource = BackendDataSourceFileSystem
				.newDataSource(PoiCategoryDataSource.class,new File("src/test/data/poiscategories/004"));


		poiDataSource = BackendDataSourceFileSystem
				.newDataSource(PoiDataSource.class,new File("src/test/data/pois/004"));

		imageMapDataSource = BackendDataSourceFileSystem
				.newDataSource(ImageMapDataSource.class, new File("src/test/data/imagemaps/004"));
		
		final RegionClient regionClient = new RegionClientFromLocal(
				"(dummy baseURL)", regionsDataSource, poiDataSource);

		regionJSONClient = new RegionJSONClientImpl(regionClient);

		final PoiCategoryClient poiCategoryClient = new PoiCategoryClientFromLocal(
				"(dummy baseURL)", poiCategoryDataSource, regionsDataSource);

		poiCategoryJSONClient = new PoiCategoryJSONClientImpl(poiCategoryClient);

		poiClient = new PoiClientFromLocal(
				"(dummy baseURL)", poiDataSource, regionsDataSource);

		poiJSONClient = new PoiJSONClientImpl(poiClient);

		final ImageMapClient imageMapClient = new ImageMapClientFromLocal(
				"(dummy baseURL)", imageMapDataSource, poiDataSource);

		imageMapJSONClient = new ImageMapJSONClientImpl(imageMapClient);
		*/

	}
	
	private PoiCategoryDataSource poiCategoryDataSource;  
	private PoiDataSource poiDataSource;
	private PoiClient poiClient;
	private RegionJSONClient regionJSONClient;
	private PoiCategoryJSONClient poiCategoryJSONClient;
	private PoiJSONClient poiJSONClient;
	private ImageMapJSONClient imageMapJSONClient;
	private RegionDataSource regionsDataSource;
	private ImageMapDataSource imageMapDataSource;

	//@Test
	public void test_gecampusJSONData() throws Exception {
		//GeocampusJSONController ctrl = new GeocampusJSONController(regionJSONClient, poiCategoryJSONClient, imageMapJSONClient, regionsDataSource, imageMapDataSource);
		//System.out.println(ctrl.actionJSON("test"));
		//assertEquals(12880, ctrl.actionJSON("test").length());
	}

	@Test
	public void test_gecampusPoisByRegionJSONData() throws Exception {
		//GeocampusPoisByRegionAndCategoryJSONController ctrl = new GeocampusPoisByRegionAndCategoryJSONController(poiJSONClient);
		//String n = null;
		//final Pois p = poiClient.getPoisByRegionAndCategory("bretagne", null);
		
<<<<<<< HEAD
		/*
=======

>>>>>>> Removed unused conflicting import
		PoiCategory pc1 = poiCategoryDataSource.getByUid(1);
		PoiCategory pc2 = poiCategoryDataSource.getLatest(pc1);
		System.out.println(pc1.getName());
		System.out.println(pc1.getDescription());
		System.out.println(pc2.getName());
		System.out.println(pc2.getDescription());
<<<<<<< HEAD
		*/
=======

>>>>>>> Removed unused conflicting import
		
		/*
		System.out.println(p.getGroups()[1].getPois()[0].getId());
		System.out.println(p.getGroups()[1].getPois()[0].getName());
		System.out.println(p.getGroups()[1].getPois()[0].getParentUid());
		System.out.println(p.getGroups()[1].getPois()[0].getCategory());
		*/
		/*System.out.println(ctrl.actionJSON("test"));*/
		//assertEquals(12880, ctrl.actionJSON("test").length());
		//System.out.println(poiJSONClient.getNearestPoisJSON(48.848627339149, 2.3430007696152, 100));		
	}

	//@Test
	public void test_gecampusPoisByCategoryJSONData() throws Exception {
		//GeocampusPoisByRegionJSONController ctrl = new GeocampusPoisByRegionJSONController(poiJSONClient);
		//Map<Integer, fr.univmobile.backend.core.Poi> pois = poiDataSource.getAllByInt("uid");
		//System.out.println(pois.get(1).getCategoryId());
		//poiCl
		/*System.out.println(ctrl.actionJSON("test"));*/
		//assertEquals(12880, ctrl.actionJSON("test").length());
	}
}
