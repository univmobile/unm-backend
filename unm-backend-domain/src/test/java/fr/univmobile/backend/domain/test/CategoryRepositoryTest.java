package fr.univmobile.backend.domain.test;

import static org.junit.Assert.*;

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
		Category category = new Category();

		/*
		category.setTitle("category title");
		category.setAuthorName("mauricio");
		category.setUid(1);
		category.setActive(true);
		category.setName("category");
		category.setDescription("description");
		category.setParentUid(0);

		repository.save(category);

		Category dbcategory = repository.findOne(category.getUid());
		assertNotNull(dbcategory);
		System.out.println(dbcategory);
		*/
	}

}
