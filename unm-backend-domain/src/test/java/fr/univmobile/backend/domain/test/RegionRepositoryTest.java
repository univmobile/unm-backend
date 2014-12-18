package fr.univmobile.backend.domain.test;

import static org.junit.Assert.assertNotNull;

import java.util.ArrayList;
import java.util.List;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import fr.univmobile.backend.domain.Region;
import fr.univmobile.backend.domain.RegionRepository;
import fr.univmobile.backend.domain.University;
import fr.univmobile.backend.domain.UniversityRepository;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = "classpath:META-INF/application-context.xml")
public class RegionRepositoryTest {

	@Autowired
	RegionRepository repository;

	@Autowired
	UniversityRepository universityRepository;

	@Test
	public void test() {
		Region r = new Region();
		//r.setId("ile_de_france1");
		//r.setTitle("region title1");
		r.setLabel("Ile De France1");
		r.setUrl("http://localhost/regions/ile_de_france");
		
		/*
		List<University> universities = r.getUniversities();
		universities.add(universityRepository.findOne("rennes1"));
		universities.add(universityRepository.findOne("paris1"));
		*/
		repository.save(r);

		Region dbregion = repository.findByLabel("ile_de_france");
		assertNotNull(dbregion);
		/*
		for (University u: dbregion.getUniversities())
			System.out.println(u);
		System.out.println(dbregion);
		*/
	}
}
