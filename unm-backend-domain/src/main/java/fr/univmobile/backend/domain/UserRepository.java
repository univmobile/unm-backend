package fr.univmobile.backend.domain;

import java.util.List;

import org.springframework.data.repository.CrudRepository;

public interface UserRepository extends CrudRepository<User, Long> {

	User findByRemoteUser(String remoteUser);

	User findByUsername(String username);
	
	List<User> findByUniversityAndRole(University university, String role);
	
}