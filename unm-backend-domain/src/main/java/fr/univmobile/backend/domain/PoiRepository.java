package fr.univmobile.backend.domain;

import java.util.List;

import org.springframework.data.repository.CrudRepository;

public interface PoiRepository extends CrudRepository<Poi, Long> {

	//List<Poi> findByUniversityAndLegacyStartingWithOrderByLegacyAsc(University university, String legacy);
	List<Poi> findAllByOrderByNameAsc();
}
