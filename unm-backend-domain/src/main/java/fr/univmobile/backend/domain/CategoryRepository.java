package fr.univmobile.backend.domain;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

public interface CategoryRepository extends JpaRepository<Category, Long> {

	Category findByName(String name);

	List<Category> findByLegacyStartingWithOrderByLegacyAsc(String legacy);
	
	List<Category> findByParent(Category parent);
 
}
