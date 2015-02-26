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

	@Query("Select p from Poi p where p.university.id = :universityId and (p.name like CONCAT('%',:val,'%') or p.description like CONCAT('%',:val,'%')) order by p.name asc")
	Page<Poi> searchValue(@Param("val") String val, @Param("universityId") Long universityId, Pageable pageable);
	
	@PreAuthorize(value="hasRole('superadmin')")
	@Query("Select p from Poi p where (p.name like CONCAT('%',:val,'%') or p.description like CONCAT('%',:val,'%')) order by p.name asc")
	Page<Poi> searchGlobalValue(@Param("val") String val, Pageable pageable);
	
	Page<Poi> findByUniversity(@Param("universityId") University universityId, Pageable pageable);

	Page<Poi> findByUniversityAndCategory(@Param("universityId") University universityId, @Param("categoryId") Category categoryId, Pageable pageable);
	
	@Query("Select p from Poi p where (p.university.id = :universityId OR (:allRestos = TRUE AND p.restoId IS NOT NULL)) and p.category.active = TRUE and p.category.legacy like CONCAT((select c.legacy from Category c WHERE c.id = :categoryId),'%') order by p.name asc")	
	Page<Poi> findByUniversityAndCategoryRoot(@Param("universityId") Long universityId, @Param("allRestos") Boolean allRestos, @Param("categoryId") Long categoryId, Pageable pageable);

	@Query("Select p from Poi p where (p.university.id = :universityId OR (:allRestos = TRUE AND p.restoId IS NOT NULL)) and p.category.active = TRUE and p.category in :categories order by p.name asc")
	Page<Poi> findByUniversityAndCategoryIn(@Param("universityId") Long universityId, @Param("allRestos") Boolean allRestos, @Param("categories") Collection<Category> categories, Pageable pageable);
	
	@Query("Select p from Poi p where p.category.active = TRUE and p.category in :categories order by p.name asc")
	Page<Poi> findByCategoryIn(@Param("categories") Collection<Category> categories, Pageable pageable);
	
	List<Poi> findByParent(Poi poi);

	List<Poi> findAllByRestoMenuUrlNotNull();
	
	Poi findByExternalId(Long externalId);

	@Query("Select p from Poi p where p.parent IS NULL and p.category.legacy like CONCAT(:category, '%') and (p.name like CONCAT('%',:name_description,'%') or p.description like CONCAT('%',:name_description,'%')) order by p.name asc")
	List<Poi> findByParentIsNullAndRootCategoryAndNameAndDescription(@Param("category") String category, @Param("name_description") String name_description);

	@Query("Select p from Poi p where p.parent IS NULL and p.category.legacy like CONCAT(:category, '%') and (p.name like CONCAT('%',:name_description,'%') or p.description like CONCAT('%',:name_description,'%')) and p.university = :university order by p.name asc")
	List<Poi> findByParentIsNullAndRootCategoryAndUniversityAndNameAndDescription(@Param("category") String category, @Param("university") University university, @Param("name_description") String name_description);
	
	@Query("Select p from Poi p where p.category.active = TRUE and p.category.legacy like CONCAT((select c.legacy from Category c WHERE c.id = :categoryId),'%') order by p.name asc")	
	Page<Poi> findByCategoryRoot(@Param("categoryId") Long categoryId, Pageable pageable);

	@Query("Select p from Poi p where p.category.active = TRUE and p.category.legacy like '/4/%' order by p.name asc") // Unhardcode legacy root	
	List<Poi> findAllLibraries();

}
