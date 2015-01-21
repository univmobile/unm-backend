package fr.univmobile.backend.domain;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface UserRepository extends JpaRepository<User, Long> {

	User findByRemoteUser(String remoteUser);

	User findByUsername(String username);
	
	List<User> findByUniversityAndRole(University university, String role);

	@Query("Select u from User u " +
			"where u.username like CONCAT('%',:val,'%') " +
			"or u.twitterScreenName like CONCAT('%',:val,'%') " +
			"or u.title like CONCAT('%',:val,'%') " +
			"or u.remoteUser like CONCAT('%',:val,'%') " +
			"or u.email like CONCAT('%',:val,'%') " +
			"or u.displayName like CONCAT('%',:val,'%') " +
			"or u.description like CONCAT('%',:val,'%') " +
			"order by u.displayName asc")
	Page<User> searchValue(@Param("val") String val, Pageable pageable);
}