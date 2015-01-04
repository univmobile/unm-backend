package fr.univmobile.backend.domain;

import java.util.Collection;
import java.util.List;

import org.springframework.data.repository.CrudRepository;

public interface PoiRepository extends CrudRepository<Poi, Long> {

	//List<Poi> findByUniversityAndLegacyStartingWithOrderByLegacyAsc(University university, String legacy);
	
	List<Poi> findByName(String name);

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
	
}
