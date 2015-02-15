package fr.univmobile.backend.domain;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.security.access.prepost.PostFilter;

public interface UniversityRepository extends JpaRepository<University, Long> {

	University findByTitle(String title);
	
	List<University> findAllByOrderByTitleAsc();

	List<University> findAllByOrderByRegion_NameAscTitleAsc();

	@PostFilter("hasRole('superadmin') or (isAuthenticated() and principal.university.id == filterObject.id)")
	@Query("select u from University u order by u.title")
	List<University> getAuthorizedUniversities();

}