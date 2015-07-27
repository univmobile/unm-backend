package fr.univmobile.backend.domain;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.data.rest.core.annotation.RestResource;

import java.util.List;

public interface ImageMapRepository extends JpaRepository<ImageMap, Long> {
	
	List<ImageMap> findByName(String name);

	@RestResource(exported = false)
	@Query("Select i from ImageMap i where i.university.id = :universityId order by i.name asc")
	List<ImageMap> findByUniversity(@Param("universityId") Long universityId);

	@Query("Select i from ImageMap i where i.name like CONCAT('%',:val,'%') or i.description like CONCAT('%',:val,'%') order by i.name asc")
	Page<ImageMap> searchValue(@Param("val") String val, Pageable pageable);
}
