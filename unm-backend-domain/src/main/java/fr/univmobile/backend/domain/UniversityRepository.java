package fr.univmobile.backend.domain;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

public interface UniversityRepository extends JpaRepository<University, Long> {

	University findByTitle(String title);
	
	List<University> findAllByOrderByTitleAsc();

}