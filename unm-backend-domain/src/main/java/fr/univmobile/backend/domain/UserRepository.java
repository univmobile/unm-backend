package fr.univmobile.backend.domain;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<User, Long> {

	User findByRemoteUser(String remoteUser);

	User findByUsername(String username);
	
	List<User> findByUniversityAndRole(University university, String role);
	
}