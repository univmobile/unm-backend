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

/**
 * When getting the POIs of a University, we should return :
 * the POIs of the University + all the Bibliotheques POIs + the POIs of the linked CROUS
 * 
 * @author
 *
 */
public interface PoiRepository extends JpaRepository<Poi, Long> {

	// List<Poi> findByUniversityAndLegacyStartingWithOrderByLegacyAsc(University university, String legacy);
	
	List<Poi> findByName(String name);

	@RestResource(exported = false)
	List<Poi> findByUniversity(University university);
	
	@RestResource(exported = false)
	List<Poi> findAllByOrderByNameAsc();
	
	@RestResource(exported = false)
	List<Poi> findByCategory_LegacyStartingWithOrderByNameAsc(String categoryLegacy);
	
	@RestResource(exported = false)
	List<Poi> findByCategory_LegacyNotLike(String categoryLegacy);

	@RestResource(exported = false)
	List<Poi> findByUniversityAndCategory_LegacyNotLike(University university, String categoryLegacy);

	@RestResource(exported = false)
	List<Poi> findByCategory_LegacyStartingWithAndUniversity_RegionOrderByNameAsc(String categoryLegacy, Region region);

	@RestResource(exported = false)
	List<Poi> findByParentIsNullAndCategory_LegacyStartingWithOrderByNameAsc(String categoryLegacy);
	
	@RestResource(exported = false)
	List<Poi> findByParentIsNullAndCategory_LegacyStartingWithAndUniversity_RegionOrderByNameAsc(String categoryLegacy, Region region);
	
	@RestResource(exported = false)
	List<Poi> findByParentIsNullAndCategory_LegacyStartingWithAndUniversityOrderByNameAsc(String categoryLegacy, University university);

	@RestResource(exported = false)
	List<Poi> findByCategoryOrderByNameAsc(Category category);

	@RestResource(exported = false)
	List<Poi> findByCategoryAndUniversity_RegionOrderByNameAsc(Category category, Region region);

	@RestResource(exported = false)
	List<Poi> findByCategoryAndUniversityOrderByNameAsc(Category category, University university);

	@RestResource(exported = false)
	List<Poi> findByCategory_LegacyStartingWithAndUniversityOrderByNameAsc(String categoryLegacy, University university);

	@RestResource(exported = false)
	List<Poi> findByIdIn(Collection<Long> ids);

	@Query("Select p from Poi p where p.university.id = :universityId and (p.name like CONCAT('%',:val,'%') or p.description like CONCAT('%',:val,'%')) order by p.name asc")
	Page<Poi> searchValue(@Param("val") String val, @Param("universityId") Long universityId, Pageable pageable);
	
	@Query("Select p from Poi p where p.category.active = TRUE and p.category.legacy like CONCAT((select c.legacy from Category c WHERE c.id = :categoryId),'%') and (p.name like CONCAT('%',:val,'%') or p.description like CONCAT('%',:val,'%')) order by p.category.name, p.name asc")
	Page<Poi> searchValueInCategoryRoot(@Param("val") String val, @Param("categoryId") Long categoryId, Pageable pageable);
	
	/** All the libraries should be searchable with a university */
	// TODO Add the linked CROUS
	@Query("Select p from Poi p where ( (p.university.id = :universityId OR p.university in (select uc.crous from UniversityCrous uc where uc.university.id = :universityId) ) and p.category.legacy like CONCAT((select c.legacy from Category c WHERE c.id = :categoryId),'%') OR p.category.id = 4) and (p.name like CONCAT('%',:val,'%') or p.description like CONCAT('%',:val,'%')) order by p.category.name, p.name asc")
	Page<Poi> searchValueInUniversityAndCategoryRoot(@Param("val") String val, @Param("universityId") Long universityId, @Param("categoryId") Long categoryId, Pageable pageable);
	
	@PreAuthorize(value="hasRole('superadmin')")
	@Query("Select p from Poi p where (p.name like CONCAT('%',:val,'%') or p.description like CONCAT('%',:val,'%')) order by p.category.name, p.name asc")
	Page<Poi> searchGlobalValue(@Param("val") String val, Pageable pageable);
	
	Page<Poi> findByUniversity(@Param("universityId") University universityId, Pageable pageable);

	/** The global libraries should be returned when filtering by library, and all the POIs of the linked CROUS  */
	@Query("Select p from Poi p where (p.university.id = :universityId OR p.university in (select uc.crous from UniversityCrous uc where uc.university.id = :universityId) ) AND p.category.id = :categoryId OR :categoryId = 7 AND p.category.id = 4 order by p.category.name, p.name asc")
	Page<Poi> findByUniversityAndCategory(@Param("universityId") Long universityId, @Param("categoryId") Long categoryId, Pageable pageable);

	/** All the libraries should be displayed with a university, and all the POIs of the linked CROUS */
	@Query("Select p from Poi p where (p.university.id = :universityId OR (:allRestos = TRUE AND p.restoId IS NOT NULL) OR p.university in (select uc.crous from UniversityCrous uc where uc.university.id = :universityId) ) and p.category.active = TRUE and p.category.legacy like CONCAT((select c.legacy from Category c WHERE c.id = :categoryId),'%') OR p.category.id = 4 order by p.category.name, p.name asc")	
	Page<Poi> findByUniversityAndCategoryRoot(@Param("universityId") Long universityId, @Param("allRestos") Boolean allRestos, @Param("categoryId") Long categoryId, Pageable pageable);

	// TODO Add the the libraries
	@Query("Select p from Poi p where ( (p.university.id = :universityId OR p.university in (select uc.crous from UniversityCrous uc where uc.university.id = :universityId) ) OR (:allRestos = TRUE AND p.restoId IS NOT NULL)) and p.category.active = TRUE and p.category in :categories order by p.category.name, p.name asc")
	Page<Poi> findByUniversityAndCategoryIn(@Param("universityId") Long universityId, @Param("allRestos") Boolean allRestos, @Param("categories") Collection<Category> categories, Pageable pageable);
	
	@Query("Select p from Poi p where p.category.active = TRUE and p.category in :categories order by p.category.name, p.name asc")
	Page<Poi> findByCategoryIn(@Param("categories") Collection<Category> categories, Pageable pageable);
	
	List<Poi> findByParent(Poi poi);

	List<Poi> findAllByRestoMenuUrlNotNull();
	
	@Query("Select p from Poi p where p.restoMenuUrl IS NOT NULL AND p.restoMenuUrl <> ''") 
	List<Poi> findAllByRestoMenuUrlNotNullOrEmpty();
	
	@RestResource(exported = false)
	@Query("Select p from Poi p where p.restoId IS NOT NULL AND p.restoId <> '' and p.legacy like CONCAT(:poiLegacy, '%')")
	List<Poi> findAllChildRestaurant(@Param("poiLegacy") String poiLegacy);
	
	Poi findByExternalId(Long externalId);
	
	//@Query("Select p from Poi p where p.legacy like CONCAT(:poi.legacy, '%')")
	//List<Poi> findAllChildren(Poi poi);

	@Query("Select p from Poi p where p.parent IS NULL and p.category.legacy like CONCAT(:category, '%') and (p.name like CONCAT('%',:name_description,'%') or p.description like CONCAT('%',:name_description,'%')) order by p.category.name, p.name asc")
	List<Poi> findByParentIsNullAndRootCategoryAndNameAndDescription(@Param("category") String category, @Param("name_description") String name_description);

	@Query("Select p from Poi p where p.parent IS NULL and p.category.legacy like CONCAT(:category, '%') and (p.name like CONCAT('%',:name_description,'%') or p.description like CONCAT('%',:name_description,'%')) and p.university = :university order by p.category.name, p.name asc")
	List<Poi> findByParentIsNullAndRootCategoryAndUniversityAndNameAndDescription(@Param("category") String category, @Param("university") University university, @Param("name_description") String name_description);
	
	@Query("Select p from Poi p where p.category.active = TRUE and p.category.legacy like CONCAT((select c.legacy from Category c WHERE c.id = :categoryId),'%') order by p.category.name, p.name asc")	
	Page<Poi> findByCategoryRoot(@Param("categoryId") Long categoryId, Pageable pageable);

	@Query("Select p from Poi p where p.category.active = TRUE and p.category.legacy like '/4/%' order by p.category.name, p.name asc") // Unhardcode legacy root	
	List<Poi> findAllLibraries();

}
