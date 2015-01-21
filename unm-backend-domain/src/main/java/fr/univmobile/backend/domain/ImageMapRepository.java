package fr.univmobile.backend.domain;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface ImageMapRepository extends JpaRepository<ImageMap, Long> {
	
	List<ImageMap> findByName(String name);

	@Query("Select i from ImageMap i where i.name like CONCAT('%',:val,'%') or i.description like CONCAT('%',:val,'%') order by i.name asc")
	Page<ImageMap> searchValue(@Param("val") String val, Pageable pageable);
}
