package fr.univmobile.backend.domain.test;

import static org.junit.Assert.assertNotNull;

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

		// CREATE
		Comment comment = new Comment();

		comment.setActive(true);
		comment.setMessage("This comment is only for testing purposes.");
		comment.setTitle("The comment");
		comment.setPoi(poiRepository.findByName("Poi 1").get(0));

		commentRepository.save(comment);

		Comment dbcomment = commentRepository.findOne(comment.getId());
		assertNotNull(dbcomment);
		System.out.println(dbcomment);
	}
}
