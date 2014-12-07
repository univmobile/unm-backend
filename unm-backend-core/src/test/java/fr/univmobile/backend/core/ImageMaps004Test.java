package fr.univmobile.backend.core;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

import org.junit.Test;

import fr.univmobile.backend.core.ImageMap.PoiInfo;

public class ImageMaps004Test extends AbstractImageMapsTest {

	public ImageMaps004Test() {
		super(new File("src/test/data/imagemaps/004"));
	}

	@Test
	public void testCount() throws Exception {

		assertEquals(2, imageMaps.getAllByInt("uid").size());
	}
	
	@Test
	public final void test_imageMap_2() throws Exception {

		final ImageMap imageMap = imageMaps.getByUid(2);
		
		assertEquals(2, imageMap.getUid());
		assertEquals("Plan Campus YYY", imageMap.getName());
		assertEquals("Description Plan Campus YYY", imageMap.getDescription());
		assertEquals("http://univmobile-dev.univ-paris1.fr/image/plan/imagemap2.png", imageMap.getImageUrl());
		
		// We keep only the active categories
		Map<Integer, String> ids = new HashMap<Integer, String>();
		ids.put(20156, "12, 25");
		ids.put(20177, "126, 245");

		for (PoiInfo poiInfo : imageMap.getPoiInfos()) {
			assertTrue(ids.containsKey(poiInfo.getId()));
			assertEquals(ids.get(poiInfo.getId()), poiInfo.getCoordinates());
			ids.remove(poiInfo.getId());
		}
	}

}
