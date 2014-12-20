package fr.univmobile.backend.domain.test;

import static org.junit.Assert.assertNotNull;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import fr.univmobile.backend.domain.RegionRepository;
import fr.univmobile.backend.domain.UniversityRepository;
import fr.univmobile.backend.domain.User;
import fr.univmobile.backend.domain.UserRepository;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = "classpath:META-INF/application-context.xml")
public class UserRepositoryTest {

	@Autowired
	UserRepository userRepository;

	@Autowired
	UniversityRepository universityRepository;

	@Autowired
	RegionRepository regionRepository;

	@Test
	public void test() {

		// userRepository.deleteAll();
		// universityRepository.deleteAll();
		// regionRepository.deleteAll();
		
		User u = new User();

		u.setUsername("dandriana");
		u.setDisplayName("Nicolas");
		u.setRemoteUser("dandriana@univ-paris1.fr");
		u.setRole("superadmin");
		u.setTitleCivilite("M.");
		u.setEmail("dandriana@univ-paris1.fr");
		u.setClassicLoginAllowed(true);
		u.setUniversity(universityRepository.findByTitle("Université Panthéon-Sorbonne - Paris I"));

		userRepository.save(u);

		User dbuser = userRepository.findByUsername("dandriana");
		assertNotNull(dbuser);
		System.out.println(dbuser);
	}

}
