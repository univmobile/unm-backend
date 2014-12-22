package fr.univmobile.backend.domain;

import java.util.List;

import org.springframework.data.repository.CrudRepository;

public interface ImageMapRepository extends CrudRepository<ImageMap, Long> {
	
	List<ImageMap> findByName(String name);
	
}
