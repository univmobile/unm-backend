package fr.univmobile.backend.domain.test;

import static org.junit.Assert.*;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import fr.univmobile.backend.domain.RegionRepository;
import fr.univmobile.backend.domain.University;
import fr.univmobile.backend.domain.UniversityRepository;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = "classpath:META-INF/application-context.xml")
public class UniversityRepositoryTest {

	@Autowired
	UniversityRepository universityRepository;
	
	@Autowired
	RegionRepository regionRepository;

	@Test
	public void test() {
		University u1 = new University();
		u1.setTitle("Université Panthéon-Sorbonne - Paris I");
		u1.setRegion(regionRepository.findByLabel("bretagne"));

		University u2 = new University();
		u2.setTitle("Université de Rennes 1");
		u2.setRegion(regionRepository.findByLabel("ile_de_france"));

		universityRepository.save(u1);
		universityRepository.save(u2);

		University dbuniversity = universityRepository.findOne(u1.getId());
		assertNotNull(dbuniversity);
		System.out.println(dbuniversity);
	}

}