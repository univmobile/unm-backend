package fr.univmobile.backend.domain;

import java.util.List;

import org.springframework.data.repository.CrudRepository;

public interface RegionRepository extends CrudRepository<Region, Long> {
	
	public Region findByLabel(String label);
	
}
