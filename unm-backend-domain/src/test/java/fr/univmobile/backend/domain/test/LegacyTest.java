package fr.univmobile.backend.domain.test;

import static org.junit.Assert.*;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import fr.univmobile.backend.domain.CategoryRepository;
import fr.univmobile.backend.domain.Poi;
import fr.univmobile.backend.domain.PoiRepository;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = "classpath:META-INF/application-context.xml")
public class LegacyTest {

	@Autowired
	PoiRepository poiRepository;

	@Autowired
	CategoryRepository categoryRepository;

	@Test
	public void test() {

		Poi p1 = new Poi();
		p1.setId(4L);
		p1.setName("p1");
		p1.setActive(true);
		p1.setLegacy("/1/2/3/4/");
		assertTrue(p1.getLegacyIds().toString().equals("[1, 2, 3, 4]"));
	}

}
