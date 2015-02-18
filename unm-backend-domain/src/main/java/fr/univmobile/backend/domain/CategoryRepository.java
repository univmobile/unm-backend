package fr.univmobile.backend.domain;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface CategoryRepository extends JpaRepository<Category, Long> {

	Category findByName(String name);

	List<Category> findByLegacyStartingWithOrderByLegacyAsc(String legacy);

	List<Category> findByParent(Category parent);

	@Query("Select c from Category c where c.name like CONCAT('%',:val,'%') or c.description like CONCAT('%',:val,'%') order by c.name asc")
	Page<Category> searchValue(@Param("val") String val, Pageable pageable);

	@Query("Select c from Category  c where c.apiParisId is not null and c.active = true")
	List<Category> findAllWhereApiParisIdIsNotNullAndActive();

	Category findByApiParisId(Long id);

	List<Category> findByNameContainingOrderByNameAsc(String name);
}