package fr.univmobile.backend.domain;

import java.util.List;

import org.springframework.data.repository.CrudRepository;

public interface CategoryRepository extends CrudRepository<Category, Long> {

	Category findByName(String name);

	List<Category> findByLegacyStartingWithOrderByLegacyAsc(String legacy);

}
