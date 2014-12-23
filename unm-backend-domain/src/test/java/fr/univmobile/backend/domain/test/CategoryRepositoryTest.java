package fr.univmobile.backend.domain.test;

import static org.junit.Assert.assertNotNull;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import fr.univmobile.backend.domain.Category;
import fr.univmobile.backend.domain.CategoryRepository;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = "classpath:META-INF/application-context.xml")
public class CategoryRepositoryTest {

	@Autowired
	CategoryRepository repository;

	@Test
	public void test() {

		// DELETE ALL
		repository.deleteAll();

		// CREATE
		Category c1 = new Category();

		c1.setActive(true);
		c1.setName("Important");
		c1.setDescription("Critical events");
		c1.setParent(null);
		c1.setLegacy("/1/");
		
		Category c2 = new Category();

		c2.setActive(true);
		c2.setName("Important 1");
		c2.setDescription("Critical events");
		c2.setParent(null);
		c2.setLegacy("/1/2");
		
		Category c3 = new Category();

		c3.setActive(true);
		c3.setName("Important 2");
		c3.setDescription("Critical events");
		c3.setParent(null);
		c3.setLegacy("/2/4");

		repository.save(c1);
		repository.save(c2);
		repository.save(c3);

		Category dbcategory = repository.findOne(c3.getId());
		assertNotNull(dbcategory);
		System.out.println(dbcategory);
	}

}
