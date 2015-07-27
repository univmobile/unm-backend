package fr.univmobile.backend.domain;

import java.util.Set;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.query.Param;

public interface InactiveMenuRepository extends JpaRepository<InactiveMenu, Long> {

	Set<InactiveMenu> findByMenu(@Param("menuId") Menu menu);
	
	InactiveMenu findByUniversityAndMenu(@Param("universityId") University university, @Param("menuId") Menu menu);
}
