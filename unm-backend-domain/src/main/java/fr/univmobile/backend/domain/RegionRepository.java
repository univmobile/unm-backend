package fr.univmobile.backend.domain;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

public interface RegionRepository extends JpaRepository<Region, Long> {

	Region findByLabel(String label);

	List<Region> findAllByOrderByNameAsc();
}