package fr.univmobile.backend.domain.test;

import static org.junit.Assert.*;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import fr.univmobile.backend.domain.University;
import fr.univmobile.backend.domain.UniversityRepository;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = "classpath:META-INF/application-context.xml")
public class UniversityRepositoryTest {

	@Autowired
	UniversityRepository repository;

	@Test
	public void test() {
		University u1 = new University();
		//u1.setId("paris1");
		u1.setTitle("Université Panthéon-Sorbonne - Paris I");

		University u2 = new University();
		//u2.setId("rennes1");
		u2.setTitle("Université de Rennes 1");

		repository.save(u1);
		repository.save(u2);

		University dbuniversity = repository.findOne("paris1");
		assertNotNull(dbuniversity);
		System.out.println(dbuniversity);
	}

}
