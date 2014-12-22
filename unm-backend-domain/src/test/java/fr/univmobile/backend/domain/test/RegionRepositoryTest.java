package fr.univmobile.backend.domain.test;

import static org.junit.Assert.assertNotNull;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import fr.univmobile.backend.domain.Region;
import fr.univmobile.backend.domain.RegionRepository;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = "classpath:META-INF/application-context.xml")
public class RegionRepositoryTest {

	@Autowired
	RegionRepository repository;

	@Test
	public void test() {
		Region r1 = new Region();
		r1.setLabel("ile_de_france");
		r1.setName("Ile de France");
		r1.setUrl("http://localhost/regions/ile_de_france");

		Region r2 = new Region();
		r2.setLabel("bretagne");
		r2.setName("Bretagne");
		r2.setUrl("http://localhost/regions/bretagne");

		repository.save(r1);
		repository.save(r2);

		Region dbregion = repository.findByLabel("ile_de_france");
		assertNotNull(dbregion);
		System.out.println(dbregion);
	}
}