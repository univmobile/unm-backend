package fr.univmobile.backend.domain;

import java.util.List;

import org.springframework.data.repository.CrudRepository;

public interface PoiRepository extends CrudRepository<Poi, Long> {

	//List<Poi> findByUniversityAndLegacyStartingWithOrderByLegacyAsc(University university, String legacy);
	
	List<Poi> findByName(String name);

	List<Poi> findByUniversity(University university);
	
	List<Poi> findAllByOrderByNameAsc();

	List<Poi> findByCategory_LegacyStartingWithOrderByNameAsc(String categoryLegacy);
	
	List<Poi> findByCategory_LegacyStartingWithAndUniversity_RegionOrderByNameAsc(String categoryLegacy, Region region);
	
}
