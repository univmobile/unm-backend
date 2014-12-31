package fr.univmobile.backend.domain.test;

import static org.junit.Assert.assertNotNull;

import java.util.List;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import fr.univmobile.backend.domain.Comment;
import fr.univmobile.backend.domain.CommentRepository;
import fr.univmobile.backend.domain.PoiRepository;
import fr.univmobile.backend.domain.UserRepository;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = "classpath:META-INF/application-context.xml")
public class CommentRepositoryTest {

	@Autowired
	CommentRepository commentRepository;

	@Autowired
	UserRepository userRepository;

	@Autowired
	PoiRepository poiRepository;

	@Test
	public void test() {

		// DELETE ALL
		commentRepository.deleteAll();

		// CREATE COMMENT 1
		Comment c1 = new Comment();

		c1.setActive(true);
		c1.setMessage("This comment is only for testing purposes 1.");
		c1.setTitle("The comment");
		c1.setPoi(poiRepository.findByName("p2").get(0));

		commentRepository.save(c1);

		// CREATE COMMENT 2
		Comment c2 = new Comment();

		c2.setActive(true);
		c2.setMessage("This comment is only for testing purposes 1.");
		c2.setTitle("The comment 1");
		c2.setPoi(poiRepository.findByName("p2").get(0));

		commentRepository.save(c2);

		// CREATE COMMENT 3
		Comment c3 = new Comment();

		c3.setActive(true);
		c3.setMessage("This comment is only for testing purposes 2.");
		c3.setTitle("The comment 2");
		c3.setPoi(poiRepository.findByName("p2").get(0));

		commentRepository.save(c3);

		Comment dbcomment = commentRepository.findOne(c1.getId());
		assertNotNull(dbcomment);
		System.out.println(dbcomment);
		
		List<Comment> dbcomments = commentRepository.findTop2ByOrderByCreatedOnDesc();
		for (Comment c : dbcomments) {
			System.out.println(c);
		}
	}
}
