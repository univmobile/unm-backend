package fr.univmobile.backend.domain.test;

import static org.junit.Assert.assertNotNull;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import fr.univmobile.backend.domain.UniversityRepository;
import fr.univmobile.backend.domain.User;
import fr.univmobile.backend.domain.UserRepository;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = "classpath:META-INF/application-context.xml")
public class UserRepositoryTest {

	@Autowired
	UserRepository repository;

	@Autowired
	UniversityRepository universityRepository;

	@Test
	public void test() {
		User u = new User();
		/*
		u.setTitle("user title");
		u.setAuthorName("mauricio");
		u.setUid("pedro");
		u.setDisplayName("Pedro Gonzales");
		u.setRemoteUser("remote_user_pedro_gonzales");
		u.setMail("pedrogonzales@gmail.com");
		u.setRole("superadmin");
		u.setScreenName("Pedro");
		u.setLogin_classic(false);
		u.setSupannCivilite("M.");
		u.setPrimaryUniversity(universityRepository.findOne("paris1"));
		u.setSecondaryUniversity(universityRepository.findOne("rennes1"));

		repository.save(u);

		User dbuser = repository.findOne("pedro");
		assertNotNull(dbuser);
		System.out.println(dbuser);
		*/
	}

}
