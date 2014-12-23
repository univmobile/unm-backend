package fr.univmobile.backend.domain.test;

import static org.junit.Assert.assertNotNull;

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

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = "classpath:META-INF/application-context.xml")
public class PoiRepositoryTest {

	@Autowired
	PoiRepository poiRepository;

	@Autowired
	UniversityRepository universityRepository;

	@Autowired
	RegionRepository regionRepository;
	
	@Autowired
	CategoryRepository categoryRepository;

	@Test
	public void test() {

		// poiRepository.deleteAll();
		// universityRepository.deleteAll();
		// regionRepository.deleteAll();
		
		Poi p1 = new Poi();
		p1.setName("p1");
		p1.setActive(true);
		p1.setCategory(categoryRepository.findByName("Important"));
		p1.setAddress("Vigil 123");
		p1.setCity("Tandil");
		p1.setCountry("Argentina");
		p1.setEmail("mauriurraco@gmail.com");
		p1.setFloor("1st");
		p1.setItinerary("Not determinated yet");
		p1.setLat(0.48537235d);
		p1.setLng(0.48537235d);
		p1.setOpeningHours("08:00 - 12:00");
		p1.setPhones("15 48 56 26 14");
		p1.setUniversity(universityRepository.findByTitle("Université Panthéon-Sorbonne - Paris I"));
		p1.setUrl("www.google.com");
		p1.setZipcode("8000");
		
		Poi p2 = new Poi();
		p2.setName("p2");
		p2.setActive(true);
		p2.setCategory(categoryRepository.findByName("Important"));
		p2.setAddress("Vigil 123");
		p2.setCity("Tandil");
		p2.setCountry("Argentina");
		p2.setEmail("mauriurraco@gmail.com");
		p2.setFloor("1st");
		p2.setItinerary("Not determinated yet");
		p2.setLat(0.48537235d);
		p2.setLng(0.48537235d);
		p2.setOpeningHours("08:00 - 12:00");
		p2.setPhones("15 48 56 26 14");
		p2.setUniversity(universityRepository.findByTitle("Université de Rennes 1"));
		p2.setUrl("www.google.com");
		p2.setZipcode("8000");
		
		poiRepository.save(p1);
		poiRepository.save(p2);

		Poi dbpoi = poiRepository.findByName("p1").get(0);
		assertNotNull(dbpoi);
		System.out.println(dbpoi);
	}

}
