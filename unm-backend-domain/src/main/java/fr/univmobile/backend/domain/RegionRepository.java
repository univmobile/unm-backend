package fr.univmobile.backend.domain;

import java.util.List;

import org.springframework.data.repository.CrudRepository;

public interface RegionRepository extends CrudRepository<Region, Long> {

	Region findByLabel(String label);

	List<Region> findAllByOrderByNameAsc();
}