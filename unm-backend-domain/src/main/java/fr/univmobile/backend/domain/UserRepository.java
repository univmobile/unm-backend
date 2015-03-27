package fr.univmobile.backend.domain;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.security.access.prepost.PostAuthorize;
import org.springframework.security.access.prepost.PreAuthorize;

public interface UserRepository extends JpaRepository<User, Long> {

	@Override
	@PreAuthorize(value="isAuthenticated()")
	@Query("Select u from User u where " +
			"(1 = ?#{hasRole('superadmin')?1:0}) or " +
			"(1 = ?#{hasRole('admin')?1:0} and u.university.id = ?#{principal.university.id}) or " +
			"(1 = ?#{hasRole('student')?1:0} and u.id = ?#{principal.id})")
	Page<User> findAll(Pageable var1);

	@PostAuthorize(value="hasRole('superadmin') or " +
			"(hasRole('admin') and returnObject.university.id == principal.university.id) or" +
			"(hasRole('student') and returnObject.id == principal.id)")
	User findOne(Long var1);

    User findFirstById(Long var1);

	User findByRemoteUser(String remoteUser);

	User findByUsername(String username);
	
	List<User> findByUniversityAndRole(University university, String role);

	@PreAuthorize(value="hasRole('superadmin') or (hasRole('admin') and principal.university.id == #universityId)")
	@Query("Select u from User u " +
			"where  u.university.id = :universityId and " +
			"(u.username like CONCAT('%',:val,'%') " +
			"or u.twitterScreenName like CONCAT('%',:val,'%') " +
			"or u.title like CONCAT('%',:val,'%') " +
			"or u.remoteUser like CONCAT('%',:val,'%') " +
			"or u.email like CONCAT('%',:val,'%') " +
			"or u.displayName like CONCAT('%',:val,'%') " +
			"or u.description like CONCAT('%',:val,'%')) " +
			"order by u.displayName asc")
	Page<User> searchValue(@Param("val") String val, @Param("universityId") Long universityId, Pageable pageable);
	
	@PreAuthorize(value="hasRole('superadmin')")
	@Query("Select u from User u " +
			"where  " +
			"(u.username like CONCAT('%',:val,'%') " +
			"or u.twitterScreenName like CONCAT('%',:val,'%') " +
			"or u.title like CONCAT('%',:val,'%') " +
			"or u.remoteUser like CONCAT('%',:val,'%') " +
			"or u.email like CONCAT('%',:val,'%') " +
			"or u.displayName like CONCAT('%',:val,'%') " +
			"or u.description like CONCAT('%',:val,'%')) " +
			"order by u.displayName asc")
	Page<Poi> searchGlobalValue(@Param("val") String val, Pageable pageable);
	
}