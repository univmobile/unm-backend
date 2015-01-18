package fr.univmobile.backend.domain;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ImageMapRepository extends JpaRepository<ImageMap, Long> {
	
	List<ImageMap> findByName(String name);
	
}
