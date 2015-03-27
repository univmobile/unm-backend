package fr.univmobile.backend.domain;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.security.access.prepost.PostFilter;

public interface UniversityRepository extends JpaRepository<University, Long> {
	
	/**
	 * Only the universities (not CROUS) should be returned by the list
	 */
	@Query("select u from University u where u.crous = FALSE order by u.region.id, u.title")
	public java.util.List<University> findAllWithoutCROUS();

	University findByTitle(String title);
	
	List<University> findAllByOrderByTitleAsc();

	List<University> findAllByOrderByRegion_NameAscTitleAsc();

	@PostFilter("hasRole('superadmin') or (isAuthenticated() and principal.university.id == filterObject.id)")
	@Query("select u from University u order by u.region.id, u.title")
	List<University> getAuthorizedUniversities();

	@PostFilter("hasRole('superadmin') or (isAuthenticated() and principal.university.id == filterObject.id)")
	@Query("select u from University u where u.crous = FALSE order by u.region.id, u.title")
	List<University> getAuthorizedUniversitiesWithoutCROUS();
	
	/** Get all the universities of type CROUS */
	@Query("select u from University u where u.crous = TRUE order by u.region.id, u.title")
	List<University> getCrous();

	List<University> findAllByRegion_Id(Long regionId);

}