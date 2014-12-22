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
		Category category = new Category();

		category.setActive(true);
		category.setName("Important");
		category.setDescription("Critical events");
		category.setParent(null);

		repository.save(category);

		Category dbcategory = repository.findOne(category.getId());
		assertNotNull(dbcategory);
		System.out.println(dbcategory);
	}

}
