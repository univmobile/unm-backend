package fr.univmobile.backend.domain;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;

@RepositoryRestResource(collectionResourceRel = "menu", path = "menues")
public interface MenuRepository extends JpaRepository<Menu, Long> {

	@Override
	@Query("From Menu m order by m.grouping,m.ordinal")
	public List<Menu> findAll();

	Page<Menu> findByOrderByGroupingAscOrdinalAsc(Pageable pageable);
	
	Page<Menu> findByUniversityOrderByGroupingAscOrdinalAsc(@Param("universityId")University universityId, Pageable pageable);
	
	Page<Menu> findByUniversityOrderByCreatedOnDesc(@Param("universityId")University universityId, Pageable pageable);
	
	@Query("select m from Menu m where m.university.id = :universityId or m.university is null order by m.grouping, m.ordinal")
	Page<Menu> findAllForUniversity(@Param("universityId")Long universityId, Pageable pageable);
}
