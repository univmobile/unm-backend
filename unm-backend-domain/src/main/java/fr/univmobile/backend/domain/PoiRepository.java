package fr.univmobile.backend.domain;

import java.util.Collection;
import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.data.rest.core.annotation.RestResource;
import org.springframework.security.access.prepost.PreAuthorize;

public interface PoiRepository extends JpaRepository<Poi, Long> {

	// List<Poi> findByUniversityAndLegacyStartingWithOrderByLegacyAsc(University university, String legacy);
	
	List<Poi> findByName(String name);

	@RestResource(exported = false)
	List<Poi> findByUniversity(University university);
	
	List<Poi> findAllByOrderByNameAsc();
	
	List<Poi> findByCategory_LegacyStartingWithOrderByNameAsc(String categoryLegacy);
	
	List<Poi> findByCategory_LegacyNotLike(String categoryLegacy);

	List<Poi> findByUniversityAndCategory_LegacyNotLike(University university, String categoryLegacy);

	List<Poi> findByCategory_LegacyStartingWithAndUniversity_RegionOrderByNameAsc(String categoryLegacy, Region region);

	List<Poi> findByParentIsNullAndCategory_LegacyStartingWithOrderByNameAsc(String categoryLegacy);
	
	List<Poi> findByParentIsNullAndCategory_LegacyStartingWithAndUniversity_RegionOrderByNameAsc(String categoryLegacy, Region region);
	
	List<Poi> findByParentIsNullAndCategory_LegacyStartingWithAndUniversityOrderByNameAsc(String categoryLegacy, University university);

	List<Poi> findByCategoryOrderByNameAsc(Category category);

	List<Poi> findByCategoryAndUniversity_RegionOrderByNameAsc(Category category, Region region);

	List<Poi> findByCategoryAndUniversityOrderByNameAsc(Category category, University university);

	List<Poi> findByCategory_LegacyStartingWithAndUniversityOrderByNameAsc(String categoryLegacy, University university);

	List<Poi> findByIdIn(Collection<Long> ids);

	@PreAuthorize(value="hasRole('superadmin') or (hasRole('admin') and principal.university.id == #universityId)")
	@Query("Select p from Poi p where p.university.id = :universityId and (p.name like CONCAT('%',:val,'%') or p.description like CONCAT('%',:val,'%')) order by p.name asc")
	Page<Poi> searchValue(@Param("val") String val, @Param("universityId") Long universityId, Pageable pageable);
	
	Page<Poi> findByUniversity(@Param("universityId") University universityId, Pageable pageable);

	Page<Poi> findByUniversityAndCategory(@Param("universityId") University universityId, @Param("categoryId") Category categoryId, Pageable pageable);

	List<Poi> findByParent(Poi poi);
	
	Poi findByExternalId(Long externalId);
}
