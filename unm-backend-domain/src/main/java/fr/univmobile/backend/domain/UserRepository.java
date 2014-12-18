package fr.univmobile.backend.domain;

import org.springframework.data.repository.CrudRepository;

public interface UserRepository extends CrudRepository<User, Long> {
	
	User findByRemoteUser(String remoteUser);
	User findByUsername(String username);

}
