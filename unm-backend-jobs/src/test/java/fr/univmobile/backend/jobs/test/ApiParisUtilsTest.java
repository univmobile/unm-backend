package fr.univmobile.backend.jobs.test;

import static org.junit.Assert.*;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import fr.univmobile.backend.domain.CategoryRepository;
import fr.univmobile.backend.domain.Poi;
import fr.univmobile.backend.domain.PoiRepository;
import fr.univmobile.backend.domain.RegionRepository;
import fr.univmobile.backend.domain.UniversityRepository;
import fr.univmobile.backend.jobs.utils.ApiParisUtils;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = PersistenceJPAConfig.class)
public class ApiParisUtilsTest {

	@Autowired
	PoiRepository poiRepository;

	@Autowired
	CategoryRepository categoryRepository;

	@Autowired
	UniversityRepository universityRepository;

	@Autowired
	RegionRepository regionRepository;

	@Test
	public void test() {

		poiRepository.deleteAll();

		ApiParisUtils apiParisUtils = new ApiParisUtils(poiRepository, categoryRepository, universityRepository, regionRepository);
		apiParisUtils.getEvents("0", "0", "3", "0");
		
		assertEquals(poiRepository.findAll().size(), 3);
		
		System.out.println("--- RESULT ---");
		
		for (Poi p : poiRepository.findAll())
			System.out.println(p);
		
		System.out.println("--- END RESULT ---");
	}

}