package fr.univmobile.backend.domain;

import java.util.List;

import org.springframework.data.repository.CrudRepository;

public interface UniversityRepository extends CrudRepository<University, Long> {

	List<University> findByTitle(String title);

}
