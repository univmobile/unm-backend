package fr.univmobile.backend.domain;

import org.springframework.data.repository.CrudRepository;

public interface UniversityRepository extends CrudRepository<University, Long> {

	University findByTitle(String title);

}