package fr.univmobile.backend.domain.test;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import fr.univmobile.backend.domain.ImageMap;
import fr.univmobile.backend.domain.ImageMapRepository;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = "classpath:META-INF/application-context.xml")
public class ImageMapRepositoryTest {

	@Autowired
	ImageMapRepository repository;

	@Test
	public void test() {
		ImageMap imageMap = new ImageMap();
		/*
		imageMap.setTitle("imagemap title");
		imageMap.setAuthorName("mauricio");
		imageMap.setUid(1);
		imageMap.setActive(true);
		imageMap.setName("imagemap");
		imageMap.setDescription("description");
		imageMap.setUrl("https://google.com.ar");
		// imageMap.setPoi(..);

		repository.save(imageMap);

		ImageMap dbimageMap = repository.findOne(imageMap.getUid());
		assertNotNull(dbimageMap);
		System.out.println(dbimageMap);
		*/
	}

}
