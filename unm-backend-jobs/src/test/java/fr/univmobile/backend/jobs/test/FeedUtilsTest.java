package fr.univmobile.backend.jobs.test;

import static org.junit.Assert.assertEquals;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import fr.univmobile.backend.domain.FeedRepository;
import fr.univmobile.backend.domain.News;
import fr.univmobile.backend.domain.NewsRepository;
import fr.univmobile.backend.domain.PoiRepository;
import fr.univmobile.backend.domain.RestoMenu;
import fr.univmobile.backend.domain.RestoMenuRepository;
import fr.univmobile.backend.jobs.utils.FeedUtils;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = PersistenceJPAConfig.class)
public class FeedUtilsTest {

	@Autowired
	FeedRepository feedRepository;

	@Autowired
	NewsRepository newsRepository;

	@Autowired
	RestoMenuRepository restoMenuRepository;
	
	@Autowired
	PoiRepository poiRepository;

	/*@Test
	public void testArticle() {

		newsRepository.deleteAll();

		FeedUtils feedUtils = new FeedUtils(feedRepository, newsRepository, restoMenuRepository, poiRepository);
		feedUtils.persistArticleFeed("http://crous.parking.einden.com/static/creteil-actu.xml");
		
		assertEquals(newsRepository.findAll().size(), 6);
		
		System.out.println("--- RESULT ---");
		
		for (News n : newsRepository.findAll())
			System.out.println(n);
		
		System.out.println("--- END RESULT ---");
	}*/
	
	@Test
	public void testRssFeed() {
		
		restoMenuRepository.deleteAll();

		FeedUtils feedUtils = new FeedUtils(feedRepository, newsRepository, restoMenuRepository, poiRepository);
		feedUtils.persistRssFeed("http://www.feedforall.com/sample-feed.xml");
		
		assertEquals(restoMenuRepository.findAll().size(), 8);
		
		System.out.println("--- RESULT ---");
		
		for (News n : newsRepository.findAll())
			System.out.println(n);
		
		System.out.println("--- END RESULT ---");
	}

}
